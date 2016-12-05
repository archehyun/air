package io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import server.AIRThread;

public abstract class InboundListener extends AIRThread{
	protected Logger 			logger = Logger.getLogger(getClass());

	protected DatagramSocket socket; // UDP 통신 소켓
	protected DatagramPacket inPacket;
	protected  int port;
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
