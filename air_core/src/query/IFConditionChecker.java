package query;

import java.sql.SQLException;

import msg.node.InboundMsgForData;

public interface IFConditionChecker 
{
	public static final String NOMAL = "0";
	public static final String RELATIVE = "1";

	/**	전송주기를 만족하는 사용자 조회
	 * 해당 사용자의 질의 조회
	 * @param data
	 * @param qidList
	 * @return
	 * @throws SQLException 
	 */
	public int[][] IF_QuerySearch(InboundMsgForData data, String[] qidList) throws SQLException;

	
	
	

}
