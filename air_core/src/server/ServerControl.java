package server;


import java.sql.SQLException;

import org.apache.log4j.Logger;

import api.InboundAPIListener;
import buffer.dao.TableBufferManager;
import buffer.info.TagInfo;
import io.InboundTagListener;
import io.OutboundTagInterface;
import msg.node.TagControlMsgForActivationPermission;
import msg.queue.TagMsgQueue;
import query.CQPManager;
import spatial.SpatialEventGenerator;
import tagmgr.TagManager;

public class ServerControl {
	protected Logger 			logger = Logger.getLogger(getClass());
	private static ServerControl instance= new ServerControl();
	private InboundAPIListener apiServerListener;	
	//private InboundTagInterface inboundTagInterface;
	private InboundTagListener inboundTagInterface;
	private InboundTagListener inboundListener;
	
	private OutboundTagInterface outboundTagInterface;
	private CQPManager cqpManager;
	private TagManager tagManager;
	public static boolean valueAdd=true;
	
	private SpatialEventGenerator spatialEventGenerator;
	private TableBufferManager bufferManager = TableBufferManager.getInstance();
	private ServerControl() {
		logger.info("server control init");
		apiServerListener 		= InboundAPIListener.getInstance();	
		inboundTagInterface 	= InboundTagListener.getInstance();
		outboundTagInterface 	= OutboundTagInterface.getInstance();
		cqpManager 						= CQPManager.getInstance(CQPManager.MULTI);
		tagManager 						= TagManager.getInstance();
		spatialEventGenerator = SpatialEventGenerator.getInstance();
		
		inboundListener = InboundTagListener.getInstance();
		
		
		TagInfo parameter = new TagInfo();
		
		try {
			logger.info("tag info INIT");
			parameter.setIs_activate("0");
			bufferManager.updateTagInfo(parameter);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static ServerControl getInstance()
	{
		if(instance == null)
			instance = new ServerControl();
		return instance;
	}
	public String getTest()
	{
		return "test";
	}
	

	public void setUp(int tagInboundPort, int clientPort, int tagOutboundPort)
	{
		InboundAPIListener apiServerListener 		= InboundAPIListener.getInstance();
		
		//InboundTagInterface inboundTagInterface 	= InboundTagInterface.getInstance();
		
		
		apiServerListener.setPort(clientPort);
		
		//inboundTagInterface.setPort(tagInboundPort);
		
		inboundListener.setPort(tagInboundPort);
		
		OutboundTagInterface.getInstance().setOutbound_port(tagOutboundPort);
		
		
		ServerPort.CLIENT_PORT = clientPort;
		ServerPort.INBOUND_PORT = tagInboundPort;
		ServerPort.OUTBOUND_PORT = tagOutboundPort;
		
		logger.info("set port=>tagPort:"+tagInboundPort+", clientPort:"+clientPort+", tagOutboundPort:"+tagOutboundPort);
		
	}
	public void start()
	{
		logger.info("server starts");
		
		apiServerListener.serverStart();
		
		inboundListener.serverStart();
		//inboundTagInterface.serverStart();
		outboundTagInterface.serverStart();
		
		tagManager.serverStart();
		cqpManager.serverStart();
		spatialEventGenerator.serverStart();
	}
	public void shotDown()
	{
		logger.info("server ends");
		
		apiServerListener.serverStop();
		inboundTagInterface.serverStop();
		outboundTagInterface.serverStop();
		
		tagManager.serverStop();
		cqpManager.serverStop();		
		spatialEventGenerator.serverStop();
		
	}
	public void addInboundMonitor(Monitor monitor)
	{
		inboundTagInterface.addMonitor(monitor);
	}
	public void addOutboundMonitor(Monitor monitor)
	{
		outboundTagInterface.addMonitor(monitor);
	}
	public void addAPIServerMonitor(Monitor monitor)
	{
		
	}
	public void addSEQMonitor(Monitor monitor)
	{
		spatialEventGenerator.addMonitor(monitor);
	}
	public void addCQPMonitor(Monitor monitor)
	{
		cqpManager.addMonitor(monitor);
	}
	public void tagActivation(String tid)
	{
		TagControlMsgForActivationPermission mag 
		= new TagControlMsgForActivationPermission(tid, "", 1);
		TagMsgQueue.getInstance().append(mag);
		logger.info("tag activation:"+tid);
	}

}
