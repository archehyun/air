package api;

import java.io.IOException;
import java.sql.SQLException;

import msg.node.QueueNode;

public interface IFInboundAPI {
	
	/**
	 * @param raw
	 * @throws IOException
	 * @throws SQLException
	 */
	public void login(byte[] raw) throws IOException, SQLException;
	/**
	 * @param raw
	 * @throws IOException
	 * @throws SQLException
	 */
	public void logout(byte[] raw) throws IOException, SQLException;
	/**
	 * @param raw
	 * @param queryConditionChange
	 * @throws IOException
	 */
	public void registerQuery(byte[] raw, QueueNode queryConditionChange) throws IOException ;
	/**
	 * @param raw
	 * @throws SQLException
	 * @throws IOException
	 */
	public void yu(byte[] raw) throws SQLException, IOException;

}
