package io;

import java.net.DatagramPacket;

/**
 * @author archehyun
 *
 */
public interface IFInbound {
	
	/**
	 * @param payload
	 */
	public void activationPermissionAck(byte[] payload);
	/**
	 * @param payload
	 * @param inPacket
	 */
	public void activationRequest(byte[] payload, DatagramPacket inPacket);
	/**
	 * @param payload
	 */
	public void activationRequestReAck(byte[] payload);
	/**
	 * @param payload
	 */
	public void actuatorAck(byte[] payload);
	/**
	 * @param payload
	 */
	public void cqp(byte[] payload);
	public void cqp(byte[] payload,DatagramPacket inPacket);
	/**
	 * @param payload
	 */
	public void distanceContiditionAck(byte[] payload);
	/**
	 * @param payload
	 */
	public void heartbeat(byte[] payload);
	/**
	 * @param payload
	 * @param inPacket
	 */
	public void ipChange(byte[] payload, DatagramPacket inPacket);
	/**
	 * @param payload
	 */
	public void queryConditionAck(byte[] payload);
	/**
	 * @param payload
	 */
	public void seg(byte[] payload);
	

}
