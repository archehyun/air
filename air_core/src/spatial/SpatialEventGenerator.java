package spatial;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import buffer.dao.TableBufferManager;
import buffer.info.LocationInfo;
import buffer.info.SpatialEventTable;
import buffer.info.WorkingMemoryTable;
import lib.geometry.Point;
import lib.geometry.Quadrangle;
import lib.geometry.SpatialOperator;
import lib.rtree.IFRTree;
import lib.rtree.TempRtree;
import msg.node.InboundMsgForData;
import msg.node.TagControlMsgForDistanceConditionChange;
import msg.queue.InboundDataMsgQueueForSEG;
import msg.queue.TagMsgQueue;
import server.AIRThread;

/**
 * 태그 위치를 기반으로 Spatial Event를 생성하여 DB에 저장하는 객체
 * 
 * @author		박병권
 * @since       2014-02-03
 * @version     0.1       
 */
public class SpatialEventGenerator extends AIRThread implements Runnable
{
	private String tid;
	private IFRTree rtree; 									//공간 인덱스 객체 인터페이스
	private InboundDataMsgQueueForSEG inboundQueue; //Inbound Data Message Queue 객체
	private TagMsgQueue tagControlQueue; 		//Tag Control Message Queue 객체
	private SpatialEventTable eventTable; 			//Spatial Event Table 객체
	private WorkingMemoryTable workingMemory; 		//Working Memory Table 객체
	private static SpatialEventGenerator instance;
	private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	private SpatialEventGenerator()
	{

	}
	public static SpatialEventGenerator getInstance() {
		if(instance==null)
			instance = new SpatialEventGenerator();
		return instance;
	}

	public void run()
	{
		init();
		logger.info("seq start");
		while(isStarted)
		{
			try{
				InboundMsgForData msg = (InboundMsgForData) inboundQueue.poll();
				
				tid =msg.getTid();
				
				msg.setCid("testC1");
				
				Point point = new Point(msg.getLongitude(), msg.getLatitude());
				
				int recentEvent=-1;
				try{
					recentEvent = workingMemory.getRecentEvent(msg.getTid(), msg.getCid());
				}catch(NullPointerException e)
				{
					recentEvent = SpatialEventTable.SPATIAL_EVENT_NONE;
				}
				LogisticsArea area = null;
				//notifyMonitor("이전 이벤트:"+recentEvent);
				switch(recentEvent)
				{
				case SpatialEventTable.SPATIAL_EVENT_INTO:
					/*
					 *  현재 거점으로부터 OUTOF 하였는지 체크
					 *  여전히 IN 상태이면
					 *  	- 다음 이벤트로서 현재 거점으로부터 OUTOF 체크를 위한 메세지 전송
					 *  OUTOF 하였으면 
					 *  	- Spatial Event Table에 OUTOF 이벤트 발생 기록
					 *  	- Working Memory에 OUTOF 이벤트 발생 기록
					 *  	- 현재 위치에서 가장 가까운 물류거점 검색
					 *  	- 다음 이벤트로서 검색한 거점으로의 INTO 또는 THROUGH 체크를 위한 메세지 전송
					 */

					logger.info("SPATIAL_EVENT_INTO");

					LocationInfo op = new LocationInfo();
					op.setLocation_code(String.valueOf(workingMemory.getRecentLogisticsAreaID(msg.getTid(), msg.getCid())));


					LocationInfo item=TableBufferManager.getInstance().selectLocationInfo(op);
					area = new LogisticsArea(item); 
					if (SpatialOperator.in(point, area.getBoundary()))
					{

						logger.info("SPATIAL_EVENT_INTO TRUE");
						notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":IN 공간 이벤트("+area.getLocation_name()+")");
						sendTagControlMsgForOUTOF(msg, area);

					}
					else
					{
						notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":OUTOF 공간 이벤트("+area.getLocation_name()+")");
						eventTable.insertEvent(msg.getTid(), msg.getCid(), msg.getYear(), msg.getMonth(), msg.getDay(), msg.getHour(), msg.getMinute(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_OUTOF);
						workingMemory.updateRecentEvent(msg.getTid(), msg.getCid(), new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_OUTOF);
						area = rtree.getNearestLogisticsArea(point);
						sendTagControlMsgForINTO_THROUGH(msg, area);
					}
					break;
				case SpatialEventTable.SPATIAL_EVENT_OUTOF:
					//notifyMonitor("공간 이벤트:OUTOF");
				case SpatialEventTable.SPATIAL_EVENT_THROUGH:
					logger.info("SPATIAL_EVENT_THROUGH");
					/*
					 *  현재 거점이 게이트 타입인 경우
					 *  	- 현재 거점을 THROUGH 한 경우
					 *  		. Spatial Event Table에 THROUGH 이벤트 발생 기록
					 *  		. Working Memory에 THROUGH 이벤트 발생 기록
					 *  		. 현재 위치에서 가장 가까운 물류거점 검색
					 *  		. 다음 이벤트로서 검색한 거점으로의 INTO 또는 THROUGH 체크를 위한 메세지 전송
					 *  	- 현재 거점을 THROUGH 하지 못한 경우
					 *  		. 현재 위치에서 가장 가까운 물류거점 검색
					 *  		. 다음 이벤트로서 검색한 거점으로의 INTO 또는 THROUGH 체크를 위한 메세지 전송
					 *  현재 거점이 게이트 타입이 아닌 경우
					 *  	- 현재 거점으로 INTO 한 경우
					 *  		. Spatial Event Table에 INTO 이벤트 발생 기록
					 *  		. Working Memory에 INTO 이벤트 발생 기록
					 *  		. 다음 이벤트로서 현재 거점으로부터 OUTOF 체크를 위한 메세지 전송
					 * 		- 현재 거점으로 INTO 하지 못한 경우
					 *  		. 현재 위치에서 가장 가까운 물류거점 검색
					 *  		. 다음 이벤트로서 검색한 거점으로의 INTO 또는 THROUGH 체크를 위한 메세지 전송
					 */

					String cid = msg.getCid();
					area = rtree.getNearestLogisticsArea(point);


					if (area.isGate())
					{	
						logger.debug("게이트 일 경우");
						Point point1 = new Point(workingMemory.getRecentLatitude(tid, cid), workingMemory.getRecentLongitude(tid, cid));
						Point point2 = point;
						notifyMonitor("게이트 일 경우");
						if (SpatialOperator.through(point1, point2, area.getBoundary()))
						{
							notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":THROUGH 이벤트 발생("+area.getLogisticsAreaName()+")");
							logger.debug("THROUGH 이벤트 발생");
							eventTable.insertEvent(tid, cid, msg.getYear(), msg.getMonth(), msg.getDay(), msg.getHour(), msg.getMinute(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_THROUGH);
							workingMemory.updateRecentEvent(tid, cid, new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_THROUGH);
						}
						area = rtree.getNearestLogisticsArea(point);
						sendTagControlMsgForINTO_THROUGH(msg, area);
					}
					else
					{	
						/**
						 * area.isGate()==FLASE 경우
						 * location_code로 gate DB 조회
						 * 
						 * for list 0->N
						 *     1. 직전 이벤트 조회
						 *     2. THROUGH 호출
						 *     2-1. TRUE : THROUGH 이벤트 저장 및 WM 업데이트 
						 *     2-2. 최근접 물류거점 조회
						 *     2-3. 거리 전달
						 *     2-4. 루프 종료	
						 *     3. FALSE: 거점 내의 게이트 중 가장 가까운 게이트와의 거리를 태그에게 전송
						 */


						if (SpatialOperator.in(point, area.getBoundary()))
						{
							logger.debug("in true:");
							notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":INTO 공간 이벤트: "+area.getLocation_name());
							eventTable.insertEvent(tid, cid, msg.getYear(), msg.getMonth(), msg.getDay(), msg.getHour(), msg.getMinute(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_INTO);							
							workingMemory.updateRecentEvent(tid, cid,new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_INTO);
							sendTagControlMsgForOUTOF(msg, area);
						}
						else
						{

							logger.debug("in false:");
							area = rtree.getNearestLogisticsArea(point);
							sendTagControlMsgForINTO_THROUGH(msg, area);
							//notifyMonitor("THROUGH->IN 공간 연산:FALSE, 최근접거점:"+area.getLocation_name());
						}	


					}
					break;
				case SpatialEventTable.SPATIAL_EVENT_NONE:


					notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":공간 이벤트:NONE");
					initTagSpatialEvent(msg);
					// 태그에게 최근접 거리
					break;
				}
			}

			catch(Exception e)
			{
				e.printStackTrace();

				notifyMonitor("error:"+e.getMessage());
			}
		}
	}

	private void init()
	{		
		logger.debug("seq init...");
		inboundQueue = InboundDataMsgQueueForSEG.getInstance();
		tagControlQueue = TagMsgQueue.getInstance();
		eventTable = SpatialEventTable.getInstance();
		workingMemory = WorkingMemoryTable.getInstance();
		rtree = TempRtree.getInstance();
	}

	private void initTagSpatialEvent(InboundMsgForData msg) throws NullPointerException, SQLException
	{
		Point point = new Point(msg.getLongitude(), msg.getLatitude());
		LogisticsArea area = rtree.getNearestLogisticsArea(point);

		logger.info("초기화, 최근접 물류거점:"+area.getLocation_name()+","+area.getBoundary());
		
		if (SpatialOperator.in(point, area.getBoundary()))
		{
			/* 물류거점 안에서 출발
			 * DB에는 이벤트 저장하지 않고 Working Memory에만 INTO 에벤트 저장
			 * OUTOF체크를 위한 메세지 전송
			 */
			notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":초기화 IN 공간 연산 TRUE");
			logger.info("초기화 IN 공간 연산 TRUE");
			sendTagControlMsgForOUTOF(msg, area);
			workingMemory.updateRecentEvent(msg.getTid(), msg.getCid(), new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_INTO);
			
		}
		else
		{
			/* 물류거점 밖에서 출발
			 * DB에는 이벤트 저장하지 않고 Working Memory에만 OUTOF 에벤트 저장
			 * INTO 또는 THROUGH 체크를 위한 메세지 전송
			 */
			notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":초기화 IN 공간 연산 FALSE");
			logger.info("초기화 IN 공간 연산 False");
			sendTagControlMsgForINTO_THROUGH(msg, area);
			workingMemory.updateRecentEvent(msg.getTid(), msg.getCid(), new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_OUTOF);
			

		}
	}

	/**
	 * IN 상태에서 다음에 일어날 이벤트가 OUTOF인지를 체크할 수 있도록 다음과 같이 Tag Control Msg를 만들어 전송
	 *	- 현재 거점이 게이트가 없는 경우에는 현재 위치에서 물류거점 4변의 각 중점까지의 거리 중 가장 가까운 거리 이동 후 알려 달라
	 *	- 현재 거점이 게이트가 있는 경우에는 현재 위치에서 각 게이트까지의 거리 중 가장 가까운 거리 이동 후 알려 달라
	 * 
	 * @param 		msg 	현재 받은 태그 데이터 메세지
	 * @param 		area 	현재 물류거점
	 * @return		          
	 */
	public void sendTagControlMsgForOUTOF(InboundMsgForData msg, LogisticsArea area)
	{
		Point point = new Point(msg.getLongitude(), msg.getLatitude());
		
		if (area.hasGate())
		{
			
			LocationInfo op = new LocationInfo();
			op.setLocation_code(area.getLocation_code());
			try {
				List li = TableBufferManager.getInstance().selectListGateLocation(op);
				// 게이트 목록
				Quadrangle gateList[]= new Quadrangle[li.size()];
				Iterator iter = li.iterator();
				for(int i=0;iter.hasNext();i++)
				{
					LocationInfo gateItem=(LocationInfo) iter.next();
					gateList[i] = new Quadrangle(gateItem.getX1(), gateItem.getY1(), gateItem.getX2(), gateItem.getY2(), 
							gateItem.getX3(), gateItem.getY3(), gateItem.getX4(), gateItem.getY4());	
				}

				area.setGate(gateList);


			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			Quadrangle[] gate = area.getGateInfo();
			int nGate = area.getNumberOfGate();
			double min = -1;
			for (int i = 0; i < nGate; i++)
			{
				Quadrangle quad = gate[i];
				double distance = SpatialOperator.longestDistance(point, quad);
				if (distance < min || min < 0)
				{
					min = distance;
				}
			}
			
			if(tagControlQueue.append(new TagControlMsgForDistanceConditionChange(msg.getTid(), msg.getCid(), (int)min)))
			{
				logger.info("게이트 중 최소거리 전송:"+min);	
			}
			
		}
		else
		{
			Quadrangle quad = area.getBoundary();
			int min =SpatialOperator.shortestDistance(point, quad);
			
			if(tagControlQueue.append(new TagControlMsgForDistanceConditionChange(msg.getTid(), msg.getCid(), min)))
			{
				logger.info("게이트 중 최소거리 전송:"+min);
			}
		}
	}

	/**
	 * OUT 상태에서 다음에 일어날 이벤트가 INTO 또는 THROUGH 인지를 체크할 수 있도록 다음과 같이 Tag Control Msg를 만들어 전송
	 * 	- 가장 가까운 거점이 게이트 타입일 경우에는 다음에 일어날 이벤트가 THROUGH 인지를 체크하기 위해 현재 위치에서 게이트 4변의 각 중점까지의 거리 중 가장 먼 거리 이동 후 알려 달라
	 *	- 가장 가까운 거점이 게이트가 없는 거점일 경우에는 현재 위치에서 물류거점 4변의 각 중점까지의 거리 중 가장 가까운 거리 이동 후 알려 달라
	 *	- 가장 가까운 거점이 게이트가 있는 거점일 경우에는 현재 위치에서 각 게이트까지의 거리 중 가장 가까운 거리 이동 후 알려 달라
	 * 
	 * @param 		msg 	현재 받은 태그 데이터 메세지
	 * @param 		area 	현재 물류거점
	 * @return		          
	 */
	public void sendTagControlMsgForINTO_THROUGH(InboundMsgForData msg, LogisticsArea area)
	{
		logger.info("태그 메세지 전송");
		
		Point point = new Point(msg.getLongitude(), msg.getLatitude());

		logger.info("areaName:"+area.getLocation_name()+",isGate:"+area.isGate()+",hasGate:"+area.hasGate());
		if (area.isGate())
		{
			logger.debug(area.getLocation_name()+":isGate:True");
			Quadrangle quad = area.getBoundary();
			tagControlQueue.append(new TagControlMsgForDistanceConditionChange(msg.getTid(), msg.getCid(), (int)SpatialOperator.longestDistance(point, quad)));
		}
		else if (area.hasGate())
		{
			logger.debug(area.getLocation_name()+":hasGate:");
			LocationInfo op = new LocationInfo();
			op.setLocation_code(area.getLocation_code());
			try {
				List li = TableBufferManager.getInstance().selectListGateLocation(op);
				// 게이트 목록
				Quadrangle gateList[]= new Quadrangle[li.size()];
				Iterator iter = li.iterator();
				for(int i=0;iter.hasNext();i++)
				{
					LocationInfo gateItem=(LocationInfo) iter.next();
					gateList[i] = new Quadrangle(gateItem.getX1(), gateItem.getY1(), gateItem.getX2(), gateItem.getY2(), 
							gateItem.getX3(), gateItem.getY3(), gateItem.getX4(), gateItem.getY4());	
				}

				area.setGate(gateList);


			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Quadrangle[] gate = area.getGateInfo();
			int nGate  = area.getNumberOfGate();
			logger.debug("게이트 수:"+nGate);
			double min = -1;
			for (int i = 0; i < nGate; i++)
			{
				Quadrangle quad = gate[i];
				double distance = SpatialOperator.longestDistance(point, quad);
				logger.debug("게이트 전송 거리:"+distance);

				if (distance < min || min < 0)
				{
					min = distance;
					logger.debug("min:"+min);
				}
			}
			logger.debug("최소거리 전송:"+min);
			tagControlQueue.append(new TagControlMsgForDistanceConditionChange(msg.getTid(), msg.getCid(), (int)min));
		}
		else
		{
			logger.debug(area.getLocation_name()+":isGate:False");
			Quadrangle quad = area.getBoundary();
			tagControlQueue.append(new TagControlMsgForDistanceConditionChange(msg.getTid(), msg.getCid(), SpatialOperator.shortestDistance(point, quad)));
		}
	}
	@Override
	public void serverStart() {
		if(thread== null)
		{
			isStarted=true;
			thread = new Thread(this);  
			thread.start();
		}
		
	}

	@Override
	public void serverStop() {
		
		isStarted=false;
		thread = null;
		
	}
	
}
