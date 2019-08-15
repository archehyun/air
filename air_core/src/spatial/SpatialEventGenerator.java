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
 * �±� ��ġ�� ������� Spatial Event�� �����Ͽ� DB�� �����ϴ� ��ü
 * 
 * @author		�ں���
 * @since       2014-02-03
 * @version     0.1       
 */
public class SpatialEventGenerator extends AIRThread implements Runnable
{
	private String tid;
	private IFRTree rtree; 									//���� �ε��� ��ü �������̽�
	private InboundDataMsgQueueForSEG inboundQueue; //Inbound Data Message Queue ��ü
	private TagMsgQueue tagControlQueue; 		//Tag Control Message Queue ��ü
	private SpatialEventTable eventTable; 			//Spatial Event Table ��ü
	private WorkingMemoryTable workingMemory; 		//Working Memory Table ��ü
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
				//notifyMonitor("���� �̺�Ʈ:"+recentEvent);
				switch(recentEvent)
				{
				case SpatialEventTable.SPATIAL_EVENT_INTO:
					/*
					 *  ���� �������κ��� OUTOF �Ͽ����� üũ
					 *  ������ IN �����̸�
					 *  	- ���� �̺�Ʈ�μ� ���� �������κ��� OUTOF üũ�� ���� �޼��� ����
					 *  OUTOF �Ͽ����� 
					 *  	- Spatial Event Table�� OUTOF �̺�Ʈ �߻� ���
					 *  	- Working Memory�� OUTOF �̺�Ʈ �߻� ���
					 *  	- ���� ��ġ���� ���� ����� �������� �˻�
					 *  	- ���� �̺�Ʈ�μ� �˻��� ���������� INTO �Ǵ� THROUGH üũ�� ���� �޼��� ����
					 */

					logger.info("SPATIAL_EVENT_INTO");

					LocationInfo op = new LocationInfo();
					op.setLocation_code(String.valueOf(workingMemory.getRecentLogisticsAreaID(msg.getTid(), msg.getCid())));


					LocationInfo item=TableBufferManager.getInstance().selectLocationInfo(op);
					area = new LogisticsArea(item); 
					if (SpatialOperator.in(point, area.getBoundary()))
					{

						logger.info("SPATIAL_EVENT_INTO TRUE");
						notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":IN ���� �̺�Ʈ("+area.getLocation_name()+")");
						sendTagControlMsgForOUTOF(msg, area);

					}
					else
					{
						notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":OUTOF ���� �̺�Ʈ("+area.getLocation_name()+")");
						eventTable.insertEvent(msg.getTid(), msg.getCid(), msg.getYear(), msg.getMonth(), msg.getDay(), msg.getHour(), msg.getMinute(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_OUTOF);
						workingMemory.updateRecentEvent(msg.getTid(), msg.getCid(), new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_OUTOF);
						area = rtree.getNearestLogisticsArea(point);
						sendTagControlMsgForINTO_THROUGH(msg, area);
					}
					break;
				case SpatialEventTable.SPATIAL_EVENT_OUTOF:
					//notifyMonitor("���� �̺�Ʈ:OUTOF");
				case SpatialEventTable.SPATIAL_EVENT_THROUGH:
					logger.info("SPATIAL_EVENT_THROUGH");
					/*
					 *  ���� ������ ����Ʈ Ÿ���� ���
					 *  	- ���� ������ THROUGH �� ���
					 *  		. Spatial Event Table�� THROUGH �̺�Ʈ �߻� ���
					 *  		. Working Memory�� THROUGH �̺�Ʈ �߻� ���
					 *  		. ���� ��ġ���� ���� ����� �������� �˻�
					 *  		. ���� �̺�Ʈ�μ� �˻��� ���������� INTO �Ǵ� THROUGH üũ�� ���� �޼��� ����
					 *  	- ���� ������ THROUGH ���� ���� ���
					 *  		. ���� ��ġ���� ���� ����� �������� �˻�
					 *  		. ���� �̺�Ʈ�μ� �˻��� ���������� INTO �Ǵ� THROUGH üũ�� ���� �޼��� ����
					 *  ���� ������ ����Ʈ Ÿ���� �ƴ� ���
					 *  	- ���� �������� INTO �� ���
					 *  		. Spatial Event Table�� INTO �̺�Ʈ �߻� ���
					 *  		. Working Memory�� INTO �̺�Ʈ �߻� ���
					 *  		. ���� �̺�Ʈ�μ� ���� �������κ��� OUTOF üũ�� ���� �޼��� ����
					 * 		- ���� �������� INTO ���� ���� ���
					 *  		. ���� ��ġ���� ���� ����� �������� �˻�
					 *  		. ���� �̺�Ʈ�μ� �˻��� ���������� INTO �Ǵ� THROUGH üũ�� ���� �޼��� ����
					 */

					String cid = msg.getCid();
					area = rtree.getNearestLogisticsArea(point);


					if (area.isGate())
					{	
						logger.debug("����Ʈ �� ���");
						Point point1 = new Point(workingMemory.getRecentLatitude(tid, cid), workingMemory.getRecentLongitude(tid, cid));
						Point point2 = point;
						notifyMonitor("����Ʈ �� ���");
						if (SpatialOperator.through(point1, point2, area.getBoundary()))
						{
							notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":THROUGH �̺�Ʈ �߻�("+area.getLogisticsAreaName()+")");
							logger.debug("THROUGH �̺�Ʈ �߻�");
							eventTable.insertEvent(tid, cid, msg.getYear(), msg.getMonth(), msg.getDay(), msg.getHour(), msg.getMinute(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_THROUGH);
							workingMemory.updateRecentEvent(tid, cid, new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_THROUGH);
						}
						area = rtree.getNearestLogisticsArea(point);
						sendTagControlMsgForINTO_THROUGH(msg, area);
					}
					else
					{	
						/**
						 * area.isGate()==FLASE ���
						 * location_code�� gate DB ��ȸ
						 * 
						 * for list 0->N
						 *     1. ���� �̺�Ʈ ��ȸ
						 *     2. THROUGH ȣ��
						 *     2-1. TRUE : THROUGH �̺�Ʈ ���� �� WM ������Ʈ 
						 *     2-2. �ֱ��� �������� ��ȸ
						 *     2-3. �Ÿ� ����
						 *     2-4. ���� ����	
						 *     3. FALSE: ���� ���� ����Ʈ �� ���� ����� ����Ʈ���� �Ÿ��� �±׿��� ����
						 */


						if (SpatialOperator.in(point, area.getBoundary()))
						{
							logger.debug("in true:");
							notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":INTO ���� �̺�Ʈ: "+area.getLocation_name());
							eventTable.insertEvent(tid, cid, msg.getYear(), msg.getMonth(), msg.getDay(), msg.getHour(), msg.getMinute(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_INTO);							
							workingMemory.updateRecentEvent(tid, cid,new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_INTO);
							sendTagControlMsgForOUTOF(msg, area);
						}
						else
						{

							logger.debug("in false:");
							area = rtree.getNearestLogisticsArea(point);
							sendTagControlMsgForINTO_THROUGH(msg, area);
							//notifyMonitor("THROUGH->IN ���� ����:FALSE, �ֱ�������:"+area.getLocation_name());
						}	


					}
					break;
				case SpatialEventTable.SPATIAL_EVENT_NONE:


					notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":���� �̺�Ʈ:NONE");
					initTagSpatialEvent(msg);
					// �±׿��� �ֱ��� �Ÿ�
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

		logger.info("�ʱ�ȭ, �ֱ��� ��������:"+area.getLocation_name()+","+area.getBoundary());
		
		if (SpatialOperator.in(point, area.getBoundary()))
		{
			/* �������� �ȿ��� ���
			 * DB���� �̺�Ʈ �������� �ʰ� Working Memory���� INTO ����Ʈ ����
			 * OUTOFüũ�� ���� �޼��� ����
			 */
			notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":�ʱ�ȭ IN ���� ���� TRUE");
			logger.info("�ʱ�ȭ IN ���� ���� TRUE");
			sendTagControlMsgForOUTOF(msg, area);
			workingMemory.updateRecentEvent(msg.getTid(), msg.getCid(), new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_INTO);
			
		}
		else
		{
			/* �������� �ۿ��� ���
			 * DB���� �̺�Ʈ �������� �ʰ� Working Memory���� OUTOF ����Ʈ ����
			 * INTO �Ǵ� THROUGH üũ�� ���� �޼��� ����
			 */
			notifyMonitor(formatter.format(new Date()).toString()+" "+tid+":�ʱ�ȭ IN ���� ���� FALSE");
			logger.info("�ʱ�ȭ IN ���� ���� False");
			sendTagControlMsgForINTO_THROUGH(msg, area);
			workingMemory.updateRecentEvent(msg.getTid(), msg.getCid(), new Double(msg.getLatitude()).longValue(), new Double(msg.getLongitude()).longValue(), area.getLogisticsAreaID(), SpatialEventTable.SPATIAL_EVENT_OUTOF);
			

		}
	}

	/**
	 * IN ���¿��� ������ �Ͼ �̺�Ʈ�� OUTOF������ üũ�� �� �ֵ��� ������ ���� Tag Control Msg�� ����� ����
	 *	- ���� ������ ����Ʈ�� ���� ��쿡�� ���� ��ġ���� �������� 4���� �� ���������� �Ÿ� �� ���� ����� �Ÿ� �̵� �� �˷� �޶�
	 *	- ���� ������ ����Ʈ�� �ִ� ��쿡�� ���� ��ġ���� �� ����Ʈ������ �Ÿ� �� ���� ����� �Ÿ� �̵� �� �˷� �޶�
	 * 
	 * @param 		msg 	���� ���� �±� ������ �޼���
	 * @param 		area 	���� ��������
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
				// ����Ʈ ���
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
				logger.info("����Ʈ �� �ּҰŸ� ����:"+min);	
			}
			
		}
		else
		{
			Quadrangle quad = area.getBoundary();
			int min =SpatialOperator.shortestDistance(point, quad);
			
			if(tagControlQueue.append(new TagControlMsgForDistanceConditionChange(msg.getTid(), msg.getCid(), min)))
			{
				logger.info("����Ʈ �� �ּҰŸ� ����:"+min);
			}
		}
	}

	/**
	 * OUT ���¿��� ������ �Ͼ �̺�Ʈ�� INTO �Ǵ� THROUGH ������ üũ�� �� �ֵ��� ������ ���� Tag Control Msg�� ����� ����
	 * 	- ���� ����� ������ ����Ʈ Ÿ���� ��쿡�� ������ �Ͼ �̺�Ʈ�� THROUGH ������ üũ�ϱ� ���� ���� ��ġ���� ����Ʈ 4���� �� ���������� �Ÿ� �� ���� �� �Ÿ� �̵� �� �˷� �޶�
	 *	- ���� ����� ������ ����Ʈ�� ���� ������ ��쿡�� ���� ��ġ���� �������� 4���� �� ���������� �Ÿ� �� ���� ����� �Ÿ� �̵� �� �˷� �޶�
	 *	- ���� ����� ������ ����Ʈ�� �ִ� ������ ��쿡�� ���� ��ġ���� �� ����Ʈ������ �Ÿ� �� ���� ����� �Ÿ� �̵� �� �˷� �޶�
	 * 
	 * @param 		msg 	���� ���� �±� ������ �޼���
	 * @param 		area 	���� ��������
	 * @return		          
	 */
	public void sendTagControlMsgForINTO_THROUGH(InboundMsgForData msg, LogisticsArea area)
	{
		logger.info("�±� �޼��� ����");
		
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
				// ����Ʈ ���
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
			logger.debug("����Ʈ ��:"+nGate);
			double min = -1;
			for (int i = 0; i < nGate; i++)
			{
				Quadrangle quad = gate[i];
				double distance = SpatialOperator.longestDistance(point, quad);
				logger.debug("����Ʈ ���� �Ÿ�:"+distance);

				if (distance < min || min < 0)
				{
					min = distance;
					logger.debug("min:"+min);
				}
			}
			logger.debug("�ּҰŸ� ����:"+min);
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
