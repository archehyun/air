package io;

import java.net.DatagramPacket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import buffer.dao.TableBufferManager;
import buffer.info.ResultInfo;
import msg.node.InboundControlMsgForActivationPermissionACK;
import msg.node.InboundControlMsgForActivationRequest;
import msg.node.InboundControlMsgForHeartbeat;
import msg.node.InboundMsgForData;
import msg.node.InboundMsgForIPChange;
import msg.queue.InboundDataMsgQueueForQP;
import msg.queue.InboundDataMsgQueueForSEG;
import msg.queue.OutboundMsgQueue;
import msg.queue.TagMsgQueue;
import server.ServerControl;

/**
 * @author archehyun
 *
 */
public class InboundTagInterfaceImpl implements IFInbound {

	InboundTagListener inboundListener;

	public InboundTagInterfaceImpl(InboundTagListener inboundListener) {
		this.inboundListener=inboundListener;

	}
	protected Logger 			logger = Logger.getLogger(getClass());

	TagUtil tagUtil = new TagUtil();

	TagMsgQueue tagMsgQueue= TagMsgQueue.getInstance();

	InboundDataMsgQueueForQP inboundDataMsgQueueForQP = InboundDataMsgQueueForQP.getInstance();

	InboundDataMsgQueueForSEG inboundDataMsgQueueForSEG =InboundDataMsgQueueForSEG.getInstance();

	OutboundMsgQueue outboundMsgQueue=OutboundMsgQueue.getInstance();

	private float battery;

	private byte door;

	private String[] hit; // 0:x, 1:y, 2:z

	private double[] latlng; // 0:latitude, 1:longitude

	//연도
	private short year;

	
	private byte month, day, hour, minute,second;

	private short temperature, humidity;

	private String tid, cid = null, gps_valid, tagIPaddr;

	TableBufferManager bufferManager = TableBufferManager.getInstance();	

	SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

	private int data_start_index=2;

	private int TID_LENGTH=8;
	private int CID_LENGTH=11;
	private int GPS_LENGTH=11;

	/**
	 * @param payload
	 */
	public void activationPermissionAck(byte[] payload) {
		logger.info("activationPermissionReAck");

		InboundControlMsgForActivationPermissionACK msgNode = new InboundControlMsgForActivationPermissionACK(tid, cid);

		tagMsgQueue.insert(msgNode);


		// 처리 없음
	}

	/**
	 * @설명 Tag가 모니터링을 시작하기 위해서는 서버에 등록을 요청
	 * @param payload
	 * @param inPacket
	 */
	public void activationRequest(byte[] payload, DatagramPacket inPacket) {	
		try{
			logger.info("activationRequest");		
			// 6번째 바이트 부터 11개의 바이트 추출

			// index가 0부터 시작
			tid 		= tagUtil.extractedTID(payload, data_start_index);

			cid 		= new String(payload, 10, 21);// stx:1, type:1, tid:8+ cid:11		

			inboundListener.notifyMonitor(dateFormat.format(System.currentTimeMillis())+" activationRequest "+tid);

			
			tagIPaddr = inPacket.getAddress().getHostAddress();
			//inboundDataMsgQueueForSEG.append(new InboundMsgForData(tid, cid, year, month, day, hour, minute, latlng[0], latlng[1], temperature, humidity, hit[0], hit[1], hit[2], door));

			tagMsgQueue.append(new InboundControlMsgForActivationRequest(tid, cid, tagIPaddr));

		}catch(Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			//System.err.println(latlng[0]+","+latlng[1]);
		}

	}

	public void activationRequestReAck(byte[] payload) {

		logger.info("activationRequestReAck");

		tid 		= tagUtil.extractedTID(payload, data_start_index);

		inboundListener.notifyMonitor(dateFormat.format(System.currentTimeMillis())+" activationRequest re ack "+tid);

		// MsgQueue 저장...



	}
	public void actuatorAck(byte[] payload) {
		logger.info("actuatorAck:"+payload);
	}

	/**
	 * 
	 * 컨테이너 타입:dry
	 * 질의 정보 처리
	 * @param payload
	 */
	public void cqp(byte[] payload) {


		try{

			/*
			 * start index: 0
			 * stx: 1byte;
			 * type field: 1byte;
			 */


			// startIndex:2, length: 8 
			tid 		= tagUtil.extractedTID(payload, data_start_index);
			System.out.println("extreacted tid:"+tid);

			/* GPS데이터 유효성 체크 : GPS 데이터를 받을 수 없는 곳에서는 'V'라고 표시되며, 정상 수신한 경우 'A'
			 * gps_valid_index(10) = 2 + 8;
			 */
			int gps_valid_start_index = data_start_index + TID_LENGTH;		 		
			gps_valid 		=  new String(payload, gps_valid_start_index, 1);

			// gps start index(11) = 10 + 1;

			latlng		= tagUtil.extractedLatLng(payload, gps_valid_start_index+1);
			

			/* 
			 * state data start index(21) = 10 + 11
			 * temperature(21) : 	1byte
			 * humidity(22) : 		1byte
			 * hit(23~28): 			6byte
			 * door(29) : 			1byte(0x00:닫힘, 0x01:열림, 0x02:확인불가)
			 */
			int state_data_start_index = gps_valid_start_index+GPS_LENGTH;
			temperature	= tagUtil.extractTemperature(payload, state_data_start_index);
			humidity 	= (byte)payload[state_data_start_index+1];
			hit			= tagUtil.extractHit(payload,state_data_start_index+2);
			door 		= payload[state_data_start_index+8];

			logger.info("\n위치(lat,lng): " + latlng[0]+","+latlng[1]+"\ntemperature: " + temperature+",humidity: " + humidity+
					"\nhitX: " + hit[0] + ",hity: " + hit[1] + ",hitZ: " + hit[2]+",door: " + door+"\n");
			/*notifyMonitor("위치(lat,lng): " + latlng[0]+","+latlng[1]+"\n"+"temperature: " + temperature+",humidity: " + humidity+
				"\nhitX: " + hit[0] + ",hity: " + hit[1] + ",hitZ: " + hit[2]+",door: " + door+"\n");*/

			StringBuffer strHit = new StringBuffer();

			for(int i=0;i<hit.length;i++)
			{
				strHit.append(hit[i]+" ");
			}
			inboundListener.notifyMonitor(dateFormat.format(System.currentTimeMillis())+" cqp message[tid:"+tid+"\t, 온도:" + temperature+", 습도:" + humidity+"%"
					+", 위도:"+latlng[0]+", 경도:"+latlng[1]+", door:"+door+"]");

			if(ServerControl.valueAdd)
			{
				ResultInfo info = new ResultInfo();
				info.setTid(tid);			
				info.setHumidity(humidity);
				info.setTemperature(temperature);
				String strHit2 = tagUtil.extractStrHit(payload);

				info.setHit(strHit2);
				info.setLat(latlng[0]);
				info.setLng(latlng[1]);
				info.setDoor(door);
				try {
					bufferManager.insertResultInfo(info);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			updateCurrentDateTime();
			// MsgQueue save
			InboundMsgForData msgNode1 = new InboundMsgForData(	tid,
					cid, 
					year, 
					month, 
					day, 
					hour, 
					minute, 
					latlng[0], 
					latlng[1], 
					temperature, 
					humidity, 
					hit[0], 
					hit[1], 
					hit[2], 
					door);

			msgNode1.setCurrentTime(System.currentTimeMillis());

			inboundDataMsgQueueForQP.append(msgNode1);

			InboundMsgForData msgNode = new InboundMsgForData(tid, cid);

			msgNode.setTagIPaddr(tagIPaddr);
			

			outboundMsgQueue.append(msgNode);

			//inboundDataMsgQueueForSEG.append(new InboundMsgForData(tid, cid, year, month, day, hour, minute, latlng[0], latlng[1], temperature, humidity, hit[0], hit[1], hit[2], door));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 거리 요청 응답
	 * @param payload
	 */
	public void distanceContiditionAck(byte[] payload) {
		logger.debug("distanceContiditionAck:not implementation");
		// 처리 없음
	}

	/**
	 * @param payload
	 */
	public void heartbeat(byte[] payload) {

		logger.info("check heartbeat:"+payload);		

		//batteryStartIndex(10) = 2 + 8;
		int batteryStartIndex = data_start_index + TID_LENGTH;
		battery 	= tagUtil.extractedBattery(payload,batteryStartIndex);


		InboundControlMsgForHeartbeat msgNode = new InboundControlMsgForHeartbeat(tid, cid, battery);

		msgNode.setTagIPaddr(tagIPaddr);

		tagMsgQueue.insert(msgNode);
	}

	/**
	 * @param payload
	 * @param inPacket
	 */
	public void ipChange(byte[] payload, DatagramPacket inPacket) {

		logger.info("check ipChange:"+payload);

		/*
		 * start index: 0
		 * stx: 1byte;
		 * type field: 1byte;
		 */

		/*
		 * #태그 아이디
		 * startIndex:2, length: 8
		 */

		tid 		= tagUtil.extractedTID(payload, data_start_index);

		/* 
		 * #GPS데이터
		 * 
		 * GPS데이터 유효성 체크 : GPS 데이터를 받을 수 없는 곳에서는 'V'라고 표시되며, 정상 수신한 경우 'A'
		 * gps_valid_index(10) = 2 + 8;
		 */
		int gps_valid_start_index = data_start_index + TID_LENGTH + CID_LENGTH;		 		
		gps_valid 		=  new String(payload, gps_valid_start_index, 1);		
		latlng		= tagUtil.extractedLatLng(payload, gps_valid_start_index);


		/* 
		 * #상태 데이터
		 * state data start index(21) = 10 + 11
		 * temperature(21) : 	1byte
		 * humidity(22) : 		1byte
		 * hit(23~28): 			6byte
		 * door(29) : 			1byte(0x00:닫힘, 0x01:열림, 0x02:확인불가)
		 */
		int state_data_start_index = gps_valid_start_index+GPS_LENGTH;
		temperature	= tagUtil.extractTemperature(payload, state_data_start_index);
		humidity 	= (byte)payload[state_data_start_index+1];
		hit			= tagUtil.extractHit(payload,state_data_start_index+2);
		door 		= payload[state_data_start_index+8];

		StringBuffer log = new StringBuffer();
		log.append("valid: " + gps_valid+"\n");
		log.append("lat: " + latlng[0]+", lng: " + latlng[1]+"\n");		
		log.append("temperature: " + temperature+", humidity: " + humidity+"\n");
		log.append("hitX: " + hit[0] + ",\t hity: " + hit[1] + ",\t hitZ: " + hit[2]+"\n");
		log.append("door: " + door);


		logger.debug(log.toString());


		// Control MsgQueue save
		tagMsgQueue.insert(	new InboundMsgForIPChange(	tid, 
				cid, 
				tagIPaddr, 
				(float)latlng[0], 
				latlng[1], 
				temperature, 
				humidity, 
				hit[0], 
				hit[1], 
				hit[2], 
				door));

	}


	/* (non-Javadoc)
	 * @see io.IFInbound#queryConditionAck(byte[])
	 */
	public void queryConditionAck(byte[] payload) {
		/* 
		 * #GPS 데이터
		 * GPS데이터 유효성 체크 : GPS 데이터를 받을 수 없는 곳에서는 'V'라고 표시되며, 정상 수신한 경우 'A'
		 * gps_valid_index(21) = 2 + 8 + 11;
		 */
		int gps_start_index = data_start_index + TID_LENGTH + CID_LENGTH;		 		
		gps_valid 		=  new String(payload, gps_start_index, 1);		
		latlng		= tagUtil.extractedLatLng(payload, gps_start_index+1);

	}

	/**
	 * @param payload
	 */
	public void seg(byte[] payload) {

		logger.info("seg:"+payload);


		/*
		 * start index: 0
		 * stx: 1byte;
		 * type field: 1byte;
		 */


		// startIndex:2, length: 8 
		tid 		= tagUtil.extractedTID(payload, data_start_index);

		// startIndex: 10(2+8), length: 11	

		/* 
		 * #GPS 데이터
		 * GPS데이터 유효성 체크 : GPS 데이터를 받을 수 없는 곳에서는 'V'라고 표시되며, 정상 수신한 경우 'A'
		 * gps_valid_index(21) = 2 + 8 + 11;
		 */
		int gps_start_index = data_start_index + TID_LENGTH;		 		
		gps_valid 		=  new String(payload, gps_start_index, 1);
		latlng		= tagUtil.extractedLatLng(payload, gps_start_index+1);

		/* 
		 * #상태 데이터
		 * state data start index(32) = 21 + 11
		 * temperature(33) : 	1byte
		 * humidity(34) : 		1byte
		 * hit(35~40): 			6byte
		 * door(41) : 			1byte
		 */
		int state_data_start_index = gps_start_index+GPS_LENGTH;
		temperature	= tagUtil.extractTemperature(payload, state_data_start_index);
		humidity 	= (byte)payload[state_data_start_index+1];
		hit			= tagUtil.extractHit(payload,state_data_start_index+2);
		door 		= payload[state_data_start_index+8];


		if(ServerControl.valueAdd)
		{
			ResultInfo info = new ResultInfo();
			info.setTid(tid);			
			info.setHumidity(humidity);
			info.setTemperature(temperature);
			String strHit2 = tagUtil.extractStrHit(payload);

			info.setHit(strHit2);
			info.setLat(latlng[0]);
			info.setLng(latlng[1]);
			info.setDoor(door);
			try {
				bufferManager.insertResultInfo(info);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}


		logger.debug("lat:"+latlng[0]+", lng:"+latlng[1]+
				"\ntemperature: " + temperature+", humidity: " + humidity+
				"\nhitX: " + hit[0] + ",\t hity: " + hit[1] + ",\t hitZ: " + hit[2]+
				"\ndoor: " + door+"\n");
		inboundListener.notifyMonitor("태그ID:"+tid+", 온도:" + temperature+", 습도:" + humidity+"%"
				+", 위도:"+latlng[0]+","+", 경도:"+latlng[1]);

		inboundDataMsgQueueForSEG.append(new InboundMsgForData(tid, cid, year, month, day, hour, minute, latlng[0], latlng[1], temperature, humidity, hit[0], hit[1], hit[2], door));


	}
	private void updateCurrentDateTime() {

		year = (short) Calendar.getInstance().get(Calendar.YEAR);
		month = (byte) Calendar.getInstance().get(Calendar.MONTH);
		day = (byte) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		hour = (byte) Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		minute= (byte) Calendar.getInstance().get(Calendar.MINUTE);
		second= (byte) Calendar.getInstance().get(Calendar.SECOND);
	}

	@Override
	public void cqp(byte[] payload, DatagramPacket inPacket) {
		tagIPaddr = inPacket.getAddress().getHostAddress();
		this.cqp(payload);
		
	}



}
