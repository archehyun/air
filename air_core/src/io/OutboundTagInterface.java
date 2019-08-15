package io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import msg.node.InboundControlMsgForActivationPermissionACK;
import msg.node.InboundControlMsgForActivationRequest;
import msg.node.InboundControlMsgForHeartbeat;
import msg.node.InboundMsgForData;
import msg.node.InboundMsgForIPChange;
import msg.node.OutboundMessage;
import msg.node.QueueNode;
import msg.node.TagControlMsgForActivationPermission;
import msg.node.TagControlMsgForActuatorControl;
import msg.node.TagControlMsgForDistanceConditionChange;
import msg.node.TagControlMsgForQueryConditionChange;
import msg.queue.OutboundMsgQueue;
import server.AIRThread;

public class OutboundTagInterface extends AIRThread implements Runnable{
	
	protected Logger 			logger = Logger.getLogger(getClass());
	SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
	private static OutboundTagInterface outboundTagInterface;

	static {
		outboundTagInterface = new OutboundTagInterface();
	}
	private final byte STX						 	 =	(byte)0x8F;
	private final byte ACTIVATION_REQUEST_ACK		 =	(byte)0x41;
	private final byte ACTIVATION_PERMISSION		 =	(byte)0x42; //사용안함
	private final byte ACTIVATION_PERMISSION_RE_ACK	 =	(byte)0x43;	//사용안함
	private final byte DISTANCE_CONDITION			 = 	(byte)0x44;	//거리 요청
	private final byte QUERY_CONDITION 				 =	(byte)0x45; // 질의 조건
	private final byte QUERY_CONDITION_ACK 			 =	(byte)0x46; // CQP에 대한 ACK
	private final byte HEARTBEAT_ACK				 = 	(byte)0x47; //
	private final byte IP_CHANGE_ACK				 = 	(byte)0x48;
	private final byte ACTUATOR						 = 	(byte)0x49;
	
	private final byte ETX1						 	 =	(byte)0x2D;
	
	private final byte ETX2						 	 =	(byte)0x2A;
	

	private int outbound_port; // UDP port:default:10002
	
	public int getOutbound_port() {
		return outbound_port;
	}

	public void setOutbound_port(int outbound_port) {
		this.outbound_port = outbound_port;
	}
	public String getTest()
	{
		return "test_version20160405";
	}
	public OutboundTagInterface() {
		
	}
	TagUtil tagUtil = new TagUtil();

	@Override
	public void run() {
		logger.info("OutboundTagInterface1("+outbound_port+") Starting...");
		
		notifyMonitor("OutboundTagInterface1("+outbound_port+") Starting...");
		while (isStarted) {
			try {

				QueueNode node = OutboundMsgQueue.getInstance().poll();

				messageProcess(node);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private byte[] message;
	InetAddress addr;
	private DatagramPacket packet;
	private DatagramSocket dsocket;
    private String tid,ip;

	/**
	 * @메시지 타입별 처리 메소드
	 * @param node
	 * @throws IOException
	 */
	private void messageProcess(QueueNode node) throws IOException {

		logger.debug("outbound message process..");

		OutboundMessage msg =(OutboundMessage) node;
		try{
			
			String tagIP =msg.getTagIPaddr();
			logger.info("tag ip:"+tagIP);
			if(tagIP.contains(":"))
			{
			addr = InetAddress.getByName(tagIP.substring(0, tagIP.indexOf(":")));
			
			}else
			{
				addr = InetAddress.getByName(tagIP);
			}
			
		}catch(NullPointerException ee)
		{
			logger.error("IP is null:"+msg.getTagIPaddr());
			//ee.printStackTrace();
			
			return;
		}

		if(node instanceof InboundControlMsgForActivationRequest) //0x41
		{
			
			logger.info("SEND ACTIVATION_REQUEST_ACK");
			
			InboundControlMsgForActivationRequest item = (InboundControlMsgForActivationRequest) node;
			notifyMonitor(dateFormat.format(System.currentTimeMillis())+" "+item.getTagIPaddr()+" ACTIVATION REQUEST ACK "+item.getTid());
			
			int messageIndex=0;
			message = new byte[23];
			message[messageIndex++]= STX;
			message[messageIndex++]= ACTIVATION_REQUEST_ACK;


			tid = item.getTid();
			byte tid[] = this.createTIDByte(item.getTid());

			
			for(int tidIndex=0;tidIndex<8;messageIndex++, tidIndex++ )
			{
				message[messageIndex] = tid[tidIndex];
			}
			ip = item.getTagIPaddr();

			// cid
			byte[] cid=item.getCid().getBytes();
			
			for(int cidIndex=0;cidIndex<11;messageIndex++, cidIndex++ )
			{
				message[messageIndex] = cid[cidIndex];
			}

			//ETX
			message[messageIndex++] = ETX1;
			message[messageIndex++] = ETX2;
			
			StringBuffer buffer = new StringBuffer();
			for(int i=0;i<message.length;i++)
			{
				buffer.append(toHex(message[i])+" ");
				
			}
			//notifyMonitor(dateFormat.format(System.currentTimeMillis())+" activation packet2=>"+buffer.toString()+",ip:"+addr+":"+outbound_port);
			
			logger.info(dateFormat.format(System.currentTimeMillis())+" activation packet2=>"+buffer.toString());

		}
		else if(node instanceof TagControlMsgForActivationPermission) //0x42
		{
			logger.debug("ACTIVATION_PERMISSION");
			
			TagControlMsgForActivationPermission item = (TagControlMsgForActivationPermission) node;
			
			notifyMonitor("ACTIVATION_PERMISSION "+item.getTid());

			message = new byte[14];
			
			message[0] = STX;
			
			message[1] = ACTIVATION_PERMISSION;
			
			byte tid[] = this.createTIDByte(item.getTid());
			
			message[2] = tid[0];			
			message[3] = tid[1];			
			message[4] = tid[2];						
			message[5] = tid[3];
			message[6] = tid[4];
			message[7] = tid[5];
			message[8] = tid[6];
			message[9] = tid[7];

			//Heartbeat
			byte[] heartbeat=changeIntToByte(item.getheartbeatPeriod());
			
			message[10] = heartbeat[0];			
			message[11] = heartbeat[1];

			//ETX
			message[12] = (byte)0x2D;			
			message[13] = (byte)0x2A;


		}
		else if(node instanceof InboundControlMsgForActivationPermissionACK) //0x43
		{			
			logger.info("ACTIVATION_PERMISSION_RE_ACK");
			
			InboundControlMsgForActivationPermissionACK item = (InboundControlMsgForActivationPermissionACK) node;

			message = createACKMessage(ACTIVATION_PERMISSION_RE_ACK,item.getTid());


		}
		else if(node instanceof TagControlMsgForDistanceConditionChange) //0x44
		{
			logger.info("DISTANCE_CONDITION");

			TagControlMsgForDistanceConditionChange item = ( TagControlMsgForDistanceConditionChange) node;
			
			

			message = new byte[14];
			
			message[0] = STX;			
			message[1] = DISTANCE_CONDITION;

			//tid 8byte 변경

			byte tid[] = this.createTIDByte(item.getTid());
			
			message[2] = tid[0];			
			message[3] = tid[1];			
			message[4] = tid[2];			
			message[5] = tid[3];
			message[6] = tid[4];
			message[7] = tid[5];
			message[8] = tid[6];
			message[9] = tid[7];

			

			//distance
			// TODO distance 값 생성
			byte[] distance=changeIntToByte(item.getMovingDistance());
			
			message[10] = distance[0];
			
			message[11] = distance[1];

			//ETX
			message[12] = ETX1;
			
			message[13] = ETX2;
			notifyMonitor(dateFormat.format(System.currentTimeMillis())+" "+item.getTagIPaddr()+" DISTANCE_CNODITION     "+item.getTid()+" "+item.getMovingDistance());

		}
		else if(node instanceof TagControlMsgForQueryConditionChange) //0x45
		{	
			logger.info("QUERY_CONDITION");
			
			TagControlMsgForQueryConditionChange item = (TagControlMsgForQueryConditionChange) node;
			notifyMonitor(dateFormat.format(System.currentTimeMillis())+" "+item.getTagIPaddr()+" QUERY_CONDITION        "+ item.getTid());
			


			message = new byte[22];
			message[0] = STX;
			message[1] = QUERY_CONDITION;

			//tid
			byte tid[] = this.createTIDByte(item.getTid());
			message[2] = tid[0];			
			message[3] = tid[1];			
			message[4] = tid[2];			
			message[5] = tid[3];
			message[6] = tid[4];
			message[7] = tid[5];
			message[8] = tid[6];
			message[9] = tid[7];

			item.getLowerBoundHumidity();

			// condition			
			message[10] 	= (byte)(item.getLowerBoundTemperature() & 0xFF); // temp_lower_bound
			
			message[11] 	= (byte)(item.getUpperBoundTemperature() & 0xFF); // temp_upper_bound

			message[12] 	= (byte)(item.getLowerBoundHumidity() & 0xFF); // humid_lower_bound
			
			message[13]	= (byte)(item.getUpperBoundHumidity() & 0xFF); // humid_upper_bound	

			message[14] = (byte)(item.getUpperBoundHit() & 0xFF); // hit_lower_bound

			message[15] = item.getDoor(); // door
			
			byte[] notice_interval=changeIntToByte(item.getNoticeInterval());
			
			message[16] = notice_interval[0]; // notice_interval1
			
			message[17] = notice_interval[1]; // notice_interval2
			
			byte[] sensing_interval=changeIntToByte(item.getSensingInterval());
			
			message[18] = sensing_interval[0]; // sensing_interval1
			
			message[19] = sensing_interval[0]; // sensing_interval2

			//ETX
			message[20] = ETX1;
			message[21] = ETX2;


		}
		else if(node instanceof InboundMsgForData) //0x46
		{
			logger.info("CQP_ACK");
			
			InboundMsgForData item = (InboundMsgForData) node;
			
			notifyMonitor(dateFormat.format(System.currentTimeMillis())+" CQP_ACK "+item.getTid()+" "+item.getTagIPaddr());
			
			message = createACKMessage(QUERY_CONDITION_ACK,item.getTid());


		}
		else if(node instanceof InboundControlMsgForHeartbeat) //0x47
		{
			logger.debug("HEARTBEAT_ACK");
			notifyMonitor(dateFormat.format(System.currentTimeMillis())+" HEARTBEAT_ACK");
			
			
			InboundControlMsgForHeartbeat item = (InboundControlMsgForHeartbeat) node;
			message = createACKMessage(HEARTBEAT_ACK,item.getTid());

		}
		else if(node instanceof InboundMsgForIPChange) //0x48
		{
			logger.debug("IP_CHANGE_ACK");

			//notifyMonitors("IP_CHANGE_ACK");
			
			InboundMsgForIPChange item = (InboundMsgForIPChange) node;
			message = createACKMessage(IP_CHANGE_ACK,item.getTid());


		}		
		else if(node instanceof TagControlMsgForActuatorControl) //0x49
		{
			logger.debug("ACTUATOR");
		//	notifyMonitors("ACTUATOR");
			TagControlMsgForActuatorControl item = (TagControlMsgForActuatorControl) node;

			item.getSwitchNo();

			message = new byte[14];
			message[0] = STX;
			message[1]= ACTUATOR;

			//tid
			byte tid[] = this.createTIDByte(item.getTid());
			message[2] = tid[0];
			message[3] = tid[1];
			message[4] = tid[2];
			message[5] = tid[3];
			message[6] = tid[4];
			message[7] = tid[5];
			message[8] = tid[6];
			message[9] = tid[7];

			// TODO switch no 생성
			//switch no
			message[10] = (byte)item.getSwitchNo();

			//switch on/off
			message[11] = item.isOn()==true?(byte)0x01:(byte)0x02;			

			//ETX
			message[12] = ETX1;
			message[13] = ETX2;
		}


		else
		{
			logger.debug("else");
		}		
		//monitor.notifyObserver("outbound", "STX:"+message[0]+",Type Field:"+message[1]);
		//notifyMonitors("STX:"+message[0]+",Type Field:"+message[1]);
		
		packet = new DatagramPacket(
				message, 
				message.length,
				addr,   
				outbound_port);
		dsocket = new DatagramSocket();
		dsocket.send(packet);
		dsocket.close();
		//notifyMonitor("outbund i/f=>SEND ACTIVATION_REQUEST_ACK "+tid+",ip:"+ip+",port:"+outbound_port);
		

	}
	private String toHex(byte data)
	{
		//바이트는 8비트니까  16진수면 2자리로 표현해야겠지
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString((data&0xF0)>>4,16));

		//뒤자리(4비트)표현

		sb.append(Integer.toString((data&0x0F),16));
		return sb.toString();

	}
	
	private byte[] changeIntToByte(int interval)
	{
		byte tidByte[] = new byte[2];

		tidByte[0] = (byte)((interval & 0xFF00) >> 8);

		tidByte[1] = (byte)(interval & 0xFF);

		return tidByte;
	}
	private byte[] createACKMessage(byte messageType,String tid)
	{
		byte[] message = new byte[12];
		
		message[0]= STX;
		
		message[1]= messageType;
		
		//tid 8byte 추가
		
		byte tidByte[] = createTIDByte(tid);
		
		message[2] = tidByte[0];
		message[3] = tidByte[1];
		message[4] = tidByte[2];
		message[5] = tidByte[3];
		message[6] = tidByte[4];
		message[7] = tidByte[5];
		message[8] = tidByte[6];
		message[9] = tidByte[7];		
		
		message[10] = ETX1;
		
		message[11] = ETX2;
		
		return message;
	}

	public static OutboundTagInterface getInstance() {
		return outboundTagInterface;
	}
	private byte[] createTIDByte_temp(String tid)
	{

		byte tidByte[] = new byte[4];

		int index = Integer.parseInt(tid.substring(1));

		tidByte[0] = (byte) tid.charAt(0);		

		tidByte[1] = (byte)((index & 0xFF0000) >> 16);

		tidByte[2] = (byte)((index & 0xFF00) >> 8);

		tidByte[3] = (byte)(index & 0xFF);

		return tidByte;
	}
	/**
	 * 문자형 태그 ID를 byte 배열로 변환
	 * @param tid
	 * @return
	 */
	private byte[] createTIDByte(String tid)
	{

		byte strTidByte[] = new byte[8];
		
		for(int i=0;i<8;i++)
		{
			strTidByte[i]=Integer.valueOf(tid.substring(2*i,2*i+2),16).byteValue();
		}
		
		return  strTidByte;
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
