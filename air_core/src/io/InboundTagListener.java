package io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class InboundTagListener extends InboundListener implements Runnable{
	
	private static InboundTagListener inboundListener;
	
	static {
		inboundListener = new InboundTagListener();
	}
	public static InboundTagListener getInstance() {
		return inboundListener;
	}
	
	
	private IFInbound inboundInterface;
	
	
	private byte[] buf;
	
	
	
	private String tid, cid = null, gps_valid, tagIPaddr;
	
	TagUtil tagUtil;
	
	private InboundTagListener() {
		port=10002; // UDP port:default:10002
		inboundInterface = new InboundTagInterfaceImpl(this);
		tagUtil= new TagUtil();
	}

	@Override
	public void run() {
		try {

			logger.info("port:"+port);
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


					tagIPaddr 	= tagUtil.extractedTagIP(inPacket);

					cid = "con001";  // 수정 필요
					tid = tagUtil.extractedTID(payload, 2);

					logger.info("tag access => tid: " + tid+", ip:"+ tagIPaddr);					

					// Type Field 
					switch (payload[1]) { 

					case AIRProtocol.ACTIVATION_REQUEST:  		//0x21
						inboundInterface.activationRequest(payload, inPacket);
						break;

					case AIRProtocol.ACTIVATION_REQUEST_RE_ACK: //0x22
						inboundInterface.activationRequestReAck(payload);
						break;

					case AIRProtocol.ACTIVATION_PERMISSION_ACK:	//0x23
						inboundInterface.activationPermissionAck(payload);
						break;

					case AIRProtocol.SEG:						//0x24
						inboundInterface.seg(payload);
						break;

					case AIRProtocol.DISTANCE_CONDITION_ACK:    //0x25
						inboundInterface.distanceContiditionAck(payload);
						break;

					case AIRProtocol.CQP:						//0x26
						inboundInterface.cqp(payload,inPacket);
						break;

					case AIRProtocol.HEARTBEAT:					//0x27
						inboundInterface.heartbeat(payload);
						break;

					case AIRProtocol.IP_CHANGE:					//0x28
						inboundInterface.ipChange(payload, inPacket);
						break;

					case AIRProtocol.ACTUATOR_ACK:				//0x29
						inboundInterface.actuatorAck(payload);
						break;
					case AIRProtocol.QUERY_CONDTION_ACK:				//0x30
						inboundInterface.queryConditionAck(payload);
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
	
	public void setPort(int port) 
	{
		this.port = port;
	}
	


	@Override
	public void serverStart() {
		if(thread== null)
		{
			logger.info("inbound listener start");
			isStarted=true;
			thread = new Thread(this);  
			thread.start();
		}

	}

	@Override
	public void serverStop() {

		logger.info("inbound listener stop");
		isStarted=false;
		thread = null;
		if(socket!=null&&!socket.isClosed())
		{
			socket.close();
			socket = null;
		}

	}

}
