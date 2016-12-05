package io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import msg.node.InboundControlMsgForActivationPermissionACK;
import msg.node.InboundControlMsgForActivationRequest;
import msg.node.InboundControlMsgForHeartbeat;
import msg.node.InboundMsgForIPChange;
import msg.node.InboundMsgForData;
import msg.queue.InboundDataMsgQueueForQP;
import msg.queue.InboundDataMsgQueueForSEG;
import msg.queue.OutboundMsgQueue;
import msg.queue.TagMsgQueue;
import server.AIRThread;
import server.ServerControl;
import buffer.dao.TableBufferManager;
import buffer.info.ResultInfo;


/**
 * @deprecated
 * @author archehyun
 *
 */
public class InboundTagInterface extends AIRThread implements Runnable, IFInbound{
	public InboundTagInterface() {

	} 
	
	TagMsgQueue tagMsgQueue= TagMsgQueue.getInstance();

	InboundDataMsgQueueForQP inboundDataMsgQueueForQP = InboundDataMsgQueueForQP.getInstance();

	InboundDataMsgQueueForSEG inboundDataMsgQueueForSEG =InboundDataMsgQueueForSEG.getInstance();

	OutboundMsgQueue outboundMsgQueue=OutboundMsgQueue.getInstance();

	/**
	 * @author archehyun
	 *
	 */
	class InboundUtil
	{
		/**
		 * 
		 */
		private String strLatitude;
		
		private String strLongitude;
		/**
		 * @param payload
		 * @return
		 */
		private float extractedBattery(byte[] payload,int startIndex) {
			return Float.parseFloat(payload[startIndex] + "." + payload[startIndex+1]);
		}

		/**
		 * �� ǥ���
		 * @return
		 */
		private double doNotation(byte a, byte b, byte c)
		{
			double tempLatitude = (Double.parseDouble(String.valueOf(a))
					+ (Double.parseDouble(String.valueOf(b)) * 0.01)
					+ (Double.parseDouble(String.valueOf(c)) * 0.0001)) / 60;
			return tempLatitude;
		}
		/**��ġ ���� ����
		 * 
		 * @���� ����(4byte), ���� ����(1byte), �浵(4byte), �浵 ����(1byte)
		 * ���� ����('N','S')
		 * �浵 ����('E','W')
		 * @param payload
		 * @return
		 */
		private double[] extractedLatLng(byte[] payload, int startIndex) {

			int latStartIndex=startIndex;

			double[] temp_latlng = new double[2];

			/*
			 * ���� ��ǥ ���
			 * 4byte: ����
			 * 1byt : ���� ����
			 * 
			 */
			strLatitude = payload[latStartIndex] + "." + String.format("%02d",payload[latStartIndex+1]) + String.format("%02d",payload[latStartIndex+2]) + String.format("%02d",payload[latStartIndex+3]);
			
			

			// ���� ǥ��� ---------> �� ǥ������� ��ȯ		

			temp_latlng[0] = Double.parseDouble(String.format("%.6f", (Double.valueOf(payload[latStartIndex]) + doNotation(payload[latStartIndex+1], payload[latStartIndex+2], payload[latStartIndex+3]))));

			/*
			 * �浵 ��ǥ ���
			 * 4byte: �浵
			 * 1byt : �浵 ����
			 */
			int lngStartIndex = startIndex+5;// latidue-d:4+latidue:1
			strLongitude = (payload[lngStartIndex] & 0xFF) + "." + String.format("%02d",payload[lngStartIndex+1]) + String.format("%02d",payload[lngStartIndex+2]) + String.format("%02d",payload[lngStartIndex+3]);
			//logger.debug("���� ǥ���(lng):"+(payload[12] & 0xFF) + "." + payload[13] + payload[14] + payload[15]);


			System.out.println(strLatitude+","+strLongitude);


			// ���� ǥ��� ---------> �� ǥ������� ��ȯ	
			temp_latlng[1] = Double.parseDouble(String.format("%.6f", (Double.valueOf((payload[lngStartIndex]) & 0xFF) +  doNotation(payload[lngStartIndex+1], payload[lngStartIndex+2], payload[lngStartIndex+3]))));

			//temp_latlng[0] =Double.parseDouble(strLatitude);
			//temp_latlng[1] =Double.parseDouble(strLongitude);

			return temp_latlng;
		}

		private String extractedTagIP(DatagramPacket inPacket) {
			return inPacket.getAddress().getHostAddress().toString() + ":" + inPacket.getPort();
		}
		/**�±� ���̵� ����-
		 * ��ü����: 8byte
		 * 1. country : 1byte
		 * 2. company : 2byte
		 * 3. type 	  : 1byte
		 * 4. number  : 4byte 
		 * @param payload
		 * @return
		 */
		private String extractedTID(byte[] payload, int startIndex) {
			/*			String tid = new String(payload, 2, 1);

			int intTid = (((int)payload[3] & 0xFF) << 16) + (((int)payload[4] & 0xFF) << 8) + ((int)payload[5] & 0xFF);

			tid += intTid;*/

			/*			String country = toHex(payload[startIndex]);
			String company = new String(payload,startIndex+1,2);
			String type = new String(payload,startIndex+3,1);
			StringBuffer sb = new StringBuffer();
			for(int i=(startIndex+4);i<(startIndex+4)+4;i++)
			{
				sb.append(toHex(payload[i]));
			}*/

			StringBuffer buffer = new StringBuffer();

			for(int i=0;i<8;i++)
			{
				buffer.append(String.format("%02X",payload[startIndex]));
				startIndex++;
			}		

			return buffer.toString();
		}



		/**
		 * ����Ʈ 16���� ǥ��
		 * ����Ʈ�� 8��Ʈ�ϱ�  16������ 2�ڸ��� ǥ��
		 * @param data
		 * @return
		 */
/*		private String toHex(byte data)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(Integer.toString((data&0xF0)>>4,16));

			sb.append(Integer.toString((data&0x0F),16));
			return sb.toString();

		}*/

		/**��� ���� ����
		 * @param payload
		 * @return
		 */
		private String[] extractHit(byte[] payload, int startIndex) {

			String[] tempHit = new String[3];
			try{
				tempHit[0] = String.valueOf(payload[startIndex] + "." + payload[startIndex+1]);
				tempHit[1] = String.valueOf((payload[startIndex+2] & 0xFF) + "." + (payload[startIndex+3] & 0xFF));
				tempHit[2] =String.valueOf((payload[startIndex+4] & 0xFF) + "." + (payload[startIndex+5] & 0xFF));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return tempHit;
		}

		private String extractStrHit(byte[] payload) {

			String[] tempHit = new String[3];
			try{
				tempHit[0] = (payload[19] + "." + payload[20]);
				tempHit[1] = (payload[21] & 0xFF) + "." + (payload[22] & 0xFF);
				tempHit[2] = (payload[23] & 0xFF) + "." + (payload[24] & 0xFF);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return payload[19] + "." + payload[20]+"."+(payload[21] & 0xFF) + "." + (payload[22] & 0xFF)+"."+(payload[23] & 0xFF) + "." + (payload[24] & 0xFF);
		}
		/**�µ� ���� ����
		 * @param payload
		 * @return
		 */
		private short extractTemperature(byte[] payload,int temperature_index) {

			// temp index:17
			short temp_temperature;
			if (String.format("%08d", Integer.parseInt(Integer.toBinaryString((payload[temperature_index]) & 0xFF))).charAt(0) == '1') 
			{
				temp_temperature = Short.parseShort("-" + Integer.parseInt(String.format("%08d", Integer.parseInt(Integer.toBinaryString((payload[17]) & 0xFF).substring(1))), 2));
			}
			else
			{
				temp_temperature = (short) Integer.parseInt(Integer.toBinaryString((payload[temperature_index]) & 0xFF), 2);
			}
			return temp_temperature;
		}
	}

	private static InboundTagInterface inboundTagInterface;
	static {
		inboundTagInterface = new InboundTagInterface();
	}
	public static InboundTagInterface getInstance() {
		return inboundTagInterface;
	}
	private float battery;

	private byte[] buf; // ����Ʈ ����

	private byte door;

	private String[] hit; // 0:x, 1:y, 2:z

	private InboundUtil inboundUtil= new InboundUtil();

	private DatagramPacket inPacket; // UDP ��� ���� ��Ŷ


	private double[] latlng; // 0:latitude, 1:longitude

	private short year;

	private byte month, day, hour, minute,second;

	private int port=10002; // UDP port:default:10002

	private DatagramSocket socket; // UDP ��� ����	

	private short temperature, humidity;

	private String tid, cid = null, gps_valid, tagIPaddr;

	TableBufferManager bufferManager = TableBufferManager.getInstance();	

	SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

	int data_start_index=2;

	int TID_LENGTH=8;
	int CID_LENGTH=11;
	int GPS_LENGTH=11;


	private boolean valueAdd=false;

	/**
	 * @param payload
	 */
	public void activationPermissionAck(byte[] payload) {
		logger.info("activationPermissionReAck");

		InboundControlMsgForActivationPermissionACK msgNode = new InboundControlMsgForActivationPermissionACK(tid, cid);

		tagMsgQueue.insert(msgNode);


		// ó�� ����
	}

	/**
	 * @���� Tag�� ����͸��� �����ϱ� ���ؼ��� ������ ����� ��û
	 * @param payload
	 * @param inPacket
	 */
	public void activationRequest(byte[] payload, DatagramPacket inPacket) {	
		try{
		logger.info("activationRequest");		
		// 6��° ����Ʈ ���� 11���� ����Ʈ ����

		// index�� 0���� ����
		tid 		= inboundUtil.extractedTID(payload, data_start_index);

		cid 		= new String(payload, 10, 21);// stx:1, type:1, tid:8+ cid:11		

		notifyMonitor(dateFormat.format(System.currentTimeMillis())+" activationRequest "+tid);


		logger.info("hit test:"+hit);
		
		//inboundDataMsgQueueForSEG.append(new InboundMsgForData(tid, cid, year, month, day, hour, minute, latlng[0], latlng[1], temperature, humidity, hit[0], hit[1], hit[2], door));
		
		tagMsgQueue.append(new InboundControlMsgForActivationRequest(tid, cid, tagIPaddr));
			
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			//System.err.println(latlng[0]+","+latlng[1]);
		}

		// Control MsgQuee save
		
	}

	public void activationRequestReAck(byte[] payload) {
		
		logger.info("activationRequestReAck");
		
		tid 		= inboundUtil.extractedTID(payload, data_start_index);
		
		notifyMonitor(dateFormat.format(System.currentTimeMillis())+" activationRequest re ack "+tid);

		// MsgQueue ����...



	}
	public void actuatorAck(byte[] payload) {
		logger.info("actuatorAck:"+payload);
	}

	/**
	 * 
	 * �����̳� Ÿ��:dry
	 * ���� ���� ó��
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
			tid 		= inboundUtil.extractedTID(payload, data_start_index);

			/* GPS������ ��ȿ�� üũ : GPS �����͸� ���� �� ���� �������� 'V'��� ǥ�õǸ�, ���� ������ ��� 'A'
			 * gps_valid_index(10) = 2 + 8;
			 */
			int gps_valid_start_index = data_start_index + TID_LENGTH;		 		
			gps_valid 		=  new String(payload, gps_valid_start_index, 1);

			// gps start index(11) = 10 + 1;

			latlng		= inboundUtil.extractedLatLng(payload, gps_valid_start_index+1);


			/* 
			 * state data start index(21) = 10 + 11
			 * temperature(21) : 	1byte
			 * humidity(22) : 		1byte
			 * hit(23~28): 			6byte
			 * door(29) : 			1byte(0x00:����, 0x01:����, 0x02:Ȯ�κҰ�)
			 */
			int state_data_start_index = gps_valid_start_index+GPS_LENGTH;
			temperature	= inboundUtil.extractTemperature(payload, state_data_start_index);
			humidity 	= (byte)payload[state_data_start_index+1];
			hit			= inboundUtil.extractHit(payload,state_data_start_index+2);
			door 		= payload[state_data_start_index+8];

			logger.info("\n��ġ(lat,lng): " + latlng[0]+","+latlng[1]+"\ntemperature: " + temperature+",humidity: " + humidity+
					"\nhitX: " + hit[0] + ",hity: " + hit[1] + ",hitZ: " + hit[2]+",door: " + door+"\n");
			/*notifyMonitor("��ġ(lat,lng): " + latlng[0]+","+latlng[1]+"\n"+"temperature: " + temperature+",humidity: " + humidity+
				"\nhitX: " + hit[0] + ",hity: " + hit[1] + ",hitZ: " + hit[2]+",door: " + door+"\n");*/

			StringBuffer strHit = new StringBuffer();

			for(int i=0;i<hit.length;i++)
			{
				strHit.append(hit[i]+" ");
			}
			notifyMonitor(dateFormat.format(System.currentTimeMillis())+" cqp message[tid:"+tid+"\t, �µ�:" + temperature+", ����:" + humidity+"%"
					+", ����:"+latlng[0]+", �浵:"+latlng[1]+", door:"+door+"]");

			if(ServerControl.valueAdd)
			{
				ResultInfo info = new ResultInfo();
				info.setTid(tid);			
				info.setHumidity(humidity);
				info.setTemperature(temperature);
				String strHit2 = inboundUtil.extractStrHit(payload);

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
	 * �Ÿ� ��û ����
	 * @param payload
	 */
	public void distanceContiditionAck(byte[] payload) {
		logger.debug("distanceContiditionAck:not implementation");
		// ó�� ����
	}


	public int getPort() {
		return port;
	}

	/**
	 * @param payload
	 */
	public void heartbeat(byte[] payload) {

		logger.info("check heartbeat:"+payload);		

		//batteryStartIndex(10) = 2 + 8;
		int batteryStartIndex = data_start_index + TID_LENGTH;
		battery 	= inboundUtil.extractedBattery(payload,batteryStartIndex);


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
		 * #�±� ���̵�
		 * startIndex:2, length: 8
		 */

		tid 		= inboundUtil.extractedTID(payload, data_start_index);

		/* 
		 * #GPS������
		 * 
		 * GPS������ ��ȿ�� üũ : GPS �����͸� ���� �� ���� �������� 'V'��� ǥ�õǸ�, ���� ������ ��� 'A'
		 * gps_valid_index(10) = 2 + 8;
		 */
		int gps_valid_start_index = data_start_index + TID_LENGTH + CID_LENGTH;		 		
		gps_valid 		=  new String(payload, gps_valid_start_index, 1);		
		latlng		= inboundUtil.extractedLatLng(payload, gps_valid_start_index);


		/* 
		 * #���� ������
		 * state data start index(21) = 10 + 11
		 * temperature(21) : 	1byte
		 * humidity(22) : 		1byte
		 * hit(23~28): 			6byte
		 * door(29) : 			1byte(0x00:����, 0x01:����, 0x02:Ȯ�κҰ�)
		 */
		int state_data_start_index = gps_valid_start_index+GPS_LENGTH;
		temperature	= inboundUtil.extractTemperature(payload, state_data_start_index);
		humidity 	= (byte)payload[state_data_start_index+1];
		hit			= inboundUtil.extractHit(payload,state_data_start_index+2);
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



	public boolean isStarted() {
		return isStarted;
	}

	public boolean isStarted2() {
		return isStarted;
	}

	@Override
	public void run() {

		try {

			buf = new byte[256];

			inPacket = new DatagramPacket(buf, buf.length);


			socket = new DatagramSocket(port);

			notifyMonitor("InboundTagInterface("+port+") Starting...");

			logger.info("InboundTagInterface("+port+") Starting...");


			while (isStarted) {

				inPacket.setLength(buf.length);

				socket.receive(inPacket);

				byte[] payload = inPacket.getData();



				if (payload[0] == AIRProtocol.STX) { // STX

					updateCurrentDateTime();

					//tid			= inboundUtil.extractedTID(payload);

					tagIPaddr 	= inboundUtil.extractedTagIP(inPacket);

					cid = "con001";  // ���� �ʿ�


					logger.info("tag access => tid: " + tid+", ip:"+ tagIPaddr);					

					// Type Field 
					switch (payload[1]) { 

					case AIRProtocol.ACTIVATION_REQUEST:  		//0x21
						activationRequest(payload, inPacket);
						break;

					case AIRProtocol.ACTIVATION_REQUEST_RE_ACK: //0x22
						activationRequestReAck(payload);
						break;

					case AIRProtocol.ACTIVATION_PERMISSION_ACK:	//0x23
						activationPermissionAck(payload);
						break;

					case AIRProtocol.SEG:						//0x24
						seg(payload);
						break;

					case AIRProtocol.DISTANCE_CONDITION_ACK:    //0x25
						distanceContiditionAck(payload);
						break;

					case AIRProtocol.CQP:						//0x26

						cqp(payload);
						break;

					case AIRProtocol.HEARTBEAT:					//0x27
						heartbeat(payload);
						break;

					case AIRProtocol.IP_CHANGE:					//0x28
						ipChange(payload, inPacket);
						break;

					case AIRProtocol.ACTUATOR_ACK:				//0x29
						actuatorAck(payload);
						break;
					case AIRProtocol.QUERY_CONDTION_ACK:				//0x30
						queryConditionAck(payload);
						break;						
					default:

						/*notifyMonitor("not support Message type:"+payload[1]+", tid:"+tid);
						logger.error("not support Message type:"+payload[1]+","+payload);*/
						break;
					}
				} else {
					notifyMonitor("not acceptable format:"+AIRProtocol.STX+","+payload[0]);
					logger.error("not acceptable format:"+AIRProtocol.STX+","+payload[0]);
					//break;
					continue;
				}				

			}

		} catch (SocketException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			logger.info("close");
			if(socket!=null)
				socket.close();
			socket = null;
		}
	}



	public void queryConditionAck(byte[] payload) {
		/* 
		 * #GPS ������
		 * GPS������ ��ȿ�� üũ : GPS �����͸� ���� �� ���� �������� 'V'��� ǥ�õǸ�, ���� ������ ��� 'A'
		 * gps_valid_index(21) = 2 + 8 + 11;
		 */
		int gps_start_index = data_start_index + TID_LENGTH + CID_LENGTH;		 		
		gps_valid 		=  new String(payload, gps_start_index, 1);		
		latlng		= inboundUtil.extractedLatLng(payload, gps_start_index+1);

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
		tid 		= inboundUtil.extractedTID(payload, data_start_index);

		// startIndex: 10(2+8), length: 11	

		/* 
		 * #GPS ������
		 * GPS������ ��ȿ�� üũ : GPS �����͸� ���� �� ���� �������� 'V'��� ǥ�õǸ�, ���� ������ ��� 'A'
		 * gps_valid_index(21) = 2 + 8 + 11;
		 */
		int gps_start_index = data_start_index + TID_LENGTH;		 		
		gps_valid 		=  new String(payload, gps_start_index, 1);
		latlng		= inboundUtil.extractedLatLng(payload, gps_start_index+1);

		/* 
		 * #���� ������
		 * state data start index(32) = 21 + 11
		 * temperature(33) : 	1byte
		 * humidity(34) : 		1byte
		 * hit(35~40): 			6byte
		 * door(41) : 			1byte
		 */
		int state_data_start_index = gps_start_index+GPS_LENGTH;
		temperature	= inboundUtil.extractTemperature(payload, state_data_start_index);
		humidity 	= (byte)payload[state_data_start_index+1];
		hit			= inboundUtil.extractHit(payload,state_data_start_index+2);
		door 		= payload[state_data_start_index+8];


		if(ServerControl.valueAdd)
		{
			ResultInfo info = new ResultInfo();
			info.setTid(tid);			
			info.setHumidity(humidity);
			info.setTemperature(temperature);
			String strHit2 = inboundUtil.extractStrHit(payload);

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
		notifyMonitor("�±�ID:"+tid+", �µ�:" + temperature+", ����:" + humidity+"%"
				+", ����:"+latlng[0]+","+", �浵:"+latlng[1]);

		inboundDataMsgQueueForSEG.append(new InboundMsgForData(tid, cid, year, month, day, hour, minute, latlng[0], latlng[1], temperature, humidity, hit[0], hit[1], hit[2], door));


	}

	public void setPort(int port) 
	{
		this.port = port;
	}


	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
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
	public void serverStart() {
		if(thread== null)
		{

			logger.info("io start");
			isStarted=true;
			thread = new Thread(this);  
			thread.start();
		}

	}

	@Override
	public void serverStop() {

		logger.info("io stop");
		isStarted=false;
		thread = null;
		if(socket!=null&&!socket.isClosed())
		{
			socket.close();
			socket = null;
		}

	}

	@Override
	public void cqp(byte[] payload, DatagramPacket inPacket) {
		// TODO Auto-generated method stub
		
	}



}

