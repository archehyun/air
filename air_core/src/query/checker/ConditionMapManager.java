package query.checker;

import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import buffer.dao.TableBufferManager;
import buffer.info.ConditionInfo;

/**
 * 
 * db�� �̿��� ����� �� ����
 * @author archehyun
 *
 */
public class ConditionMapManager extends HashMap<String, ConditionInfo[][]>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected TableBufferManager bufferManager = TableBufferManager.getInstance();
	int indexList[]={	ConditionInfo.CONTAINER,
			ConditionInfo.TEMPERATURE,
			ConditionInfo.HUMIDITY,							
			ConditionInfo.HIT,
			ConditionInfo.DATE
	};

	protected Logger 			logger = Logger.getLogger(getClass());
	public ConditionMapManager() {
		// TODO Auto-generated constructor stub
	}

	public ConditionInfo[][] getConditionMap(String tid, String[] qidList) throws SQLException {
		if(this.containsKey(tid))
		{
			logger.info("���� ����� �� ��ȯ");
			// ������ �±׸� �ؽ��ʿ��� ��ȸ
			return get(tid);
		}
		else
		{
			logger.info("�ű� ����� �� ����");
			// �ű� �±׸� ���ο� �� ����
			ConditionInfo[][] map=createCondtionMap(qidList);
			this.put(tid, map);
			return map;
		}

	}

	/**
	 * @param qidList
	 * @return
	 * @throws SQLException
	 */
	private synchronized ConditionInfo[][] createCondtionMap(String[] qidList) throws SQLException {
		ConditionInfo op = new ConditionInfo();	
		ConditionInfo conditionMapList[][] = new ConditionInfo[5][qidList.length];
		for(int i=0;i<conditionMapList.length;i++)
		{
			op.setTableType(indexList[i]);
			
			for(int j=0;j<qidList.length;j++)
			{	
				op.setQuery_number(qidList[j]);

				conditionMapList[i][j]=(ConditionInfo) bufferManager.selectConditionInfo(op);
			}
			
		}
		return conditionMapList;

	}

}
