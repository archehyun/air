package query.checker;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import query.IFConditionChecker;
import buffer.dao.TableBufferManager;
import buffer.info.ConditionInfo;

/**
 * ���Ǹ� �˻� �� �����ϴ� Ŭ����
 * 
 * @author		��â��
 * @since       2014-01-29
 * @version     0.1       
 */

public abstract class DefaultConditionChecker implements IFConditionChecker{


	protected TableBufferManager bufferManager = TableBufferManager.getInstance();
	protected ConditionInfo conditionList[][]; // ���� ���� �񱳿� ���� �迭
	protected ConditionMapManager conditionMapList[][]; // ���� ���� �񱳿� ���� �迭
	protected Logger 			logger = Logger.getLogger(getClass());
	int indexList[]={	ConditionInfo.CONTAINER,
			ConditionInfo.TEMPERATURE,
			ConditionInfo.HUMIDITY,							
			ConditionInfo.HIT,
			ConditionInfo.DATE
	};
	ConditionMapManager conditionMap = new ConditionMapManager();

	protected void initConditionMap(String tid,String[] qidList) throws SQLException {

		conditionList=conditionMap.getConditionMap(tid, qidList);

		StringBuffer buffer = new StringBuffer();		

		for(int i=0;i<conditionList.length;i++)
		{
			for(int j=0;j<conditionList[i].length;j++)
			{				
				buffer.append("["+conditionList[i][j]+"]");
			}
			buffer.append("\n");
		}
		logger.info("condition map init:\n"+buffer.toString());
	}
	/**
	 * ���� ���� �ʱ�ȭ
	 * @param qidList ���� ���̵� ���
	 * @throws SQLException 
	 */
	protected void initConditionMap2(String[] qidList) throws SQLException
	{
		/* ���� ó���� ���� ����ü ����
		 * ConditionInfo Ŭ����  �迭����(��:���Ǽ�, ��:���Ǽ�)�� �޸� �� ���� 
		 * �� �迭�� ��Ҵ� ���� ���� ������ ConditionInfo Ŭ������ �ν��Ͻ��� �����Ͽ� �Ҵ�
		 *  ConditionInfo �ν��Ͻ��� bufferManager�� ���ؼ� DB���� ��ȸ�Ͽ� ������
		 *  �ش� ������ ���� ��쿡�� �迭�� ��ҿ� null�� �Ҵ�
		 */
		double start = System.currentTimeMillis();
		conditionList = new ConditionInfo[5][qidList.length];

		// �˻� ���ǿ� ���� ���� ����: �����̳�, �µ�, ����, ���, ���� ������ �˻� ����

		ConditionInfo op = new ConditionInfo();
		int count=0;
		for(int i=0;i<qidList.length;i++)
		{
			op.setQuery_number(qidList[i]);
			for(int j=0;j<indexList.length;j++)
			{
				op.setTableType(indexList[j]);

				conditionList[j][i]=(ConditionInfo) bufferManager.selectConditionInfo(op);
				count++;
			}
		}
		logger.info(qidList);
		logger.info("init map("+(System.currentTimeMillis()-start)+"ms),count:"+count);
	}
}
