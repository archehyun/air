package query;

import java.sql.SQLException;

import msg.node.InboundMsgForData;

public interface IFConditionChecker 
{
	public static final String NOMAL = "0";
	public static final String RELATIVE = "1";

	/**	�����ֱ⸦ �����ϴ� ����� ��ȸ
	 * �ش� ������� ���� ��ȸ
	 * @param data
	 * @param qidList
	 * @return
	 * @throws SQLException 
	 */
	public int[][] IF_QuerySearch(InboundMsgForData data, String[] qidList) throws SQLException;

	
	
	

}
