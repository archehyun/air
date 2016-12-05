package query.checker;

import java.sql.SQLException;

import msg.node.InboundMsgForData;
import buffer.info.ConditionInfo;

/**
 * @author archehyun
 *
 */
public class SingleConditionChecker extends DefaultConditionChecker{

	public SingleConditionChecker() {
		logger.info("single checker  ����");
		
		
	}
	
	/* (non-Javadoc)
	 * @see air.cqp.IFConditionChecker#IF_QuerySearch(air.mq.AIRMessage, java.lang.String[])
	 */
	@Override
	public int[][] IF_QuerySearch(InboundMsgForData data, String[] qidList)
			throws SQLException {
		
		logger.info("tid:"+data.getTid()+",cid:"+data.getCid());
		initConditionMap(data.getTid(),qidList);// ���������� �����ϱ� ���� ����ü ����
		
		
		/*���� ó���� ���� �Ǹ鼭 ó���� ����� �ӽ������� �����ϴ� ����ü
		 * ó���� ���Ǽ��� 1���� �迭�� ���̷� �����Ǹ� ó�� ����� �����ϱ� ���� �Ӽ��� �޼ҵ尡 ���� �Ǿ� ���� 
		 * �� �ܰ�(�µ�, ����, ���,..)���� ó���� ����� ����
		 * �ʱ� ������ ����� true�� �Ҵ��
		 */		
		ResultMap resultArray[] = new ResultMap[qidList.length];
		
		for(int i=0;i<resultArray.length;i++)
		{
			resultArray[i] = new ResultMap();
			resultArray[i].setQid(qidList[i]);// �� ó����� ��ü�� �ĺ������� ���� ���̵� �Ҵ�
		}
		
		logger.debug("tid:"+data.getTid());
		/*
		 *  ���� initConditionMap �޼ҵ忡�� ������ ������� ���� ó���� ����
		 *  �� �ܰ迡�� ó���� ����� ResultMap���� ����ǰ� ���� �ܰ輭 �̸� Ȱ����
		 *  ó�� ������ �����̳�, �µ�, ���, ����, ���Ƿ� ����
		 *  ���� ���Ǻ��� ������ �޼ҵ带 ���ؼ� ���� ó���� ����
		 */
		checkContainerCondition(data, resultArray);// container Condition
		checkTemperatureCondition(data, resultArray);// Temperature Condition		
		checkHitCondition(data, resultArray);// Hit Condition		
		checkHumidityCondition(data, resultArray);// Humidity Condition

		/*Create ResultArray ==================
		 * ó���� ����� ����� ��:2, ��:���Ǽ� �� int�� �迭�� ������ 
		 * �迭 ù��° �࿡�� ���� ���̵�, �ι�° �࿡�� ���� ó�� ����� 0:false, 1:true ���·� ����
		 */
		
		int result[][] = new int[2][resultArray.length];
		for(int i=0;i<resultArray.length;i++)
		{
			result[0][i]= Integer.parseInt(resultArray[i].getQid());
			result[1][i]= resultArray[i].isResult()?1:0;
		}

		//TODO ��ŷ �޸� �۾�
		logger.debug("end");
		return result;
	}	
	
	/**@���� �����̳� ���� ��
	 * @param data �±� �޽���
	 * @param result �� ��� ��
	 */
	private void checkContainerCondition(InboundMsgForData data, ResultMap[] result) {
		logger.debug("start");
		for(int j=0;j<result.length;j++)
		{			
			if(result[j].isResult())
			{
				if(conditionList[ConditionInfo.CONTAINER][j]==null) // �ش� ������  ���� ���� ���
				{
					System.out.println("continue");
					continue;
				}
				
				// ����ü���� ���� ���� ������ ����
				String cid=conditionList[ConditionInfo.CONTAINER][j].getCid();// ������ ���� ���


				try{
					if(!data.getCid().equals(cid))// ���ǿ� �������� ������
					{
						result[j].setResult(false);//��� �迭�� �ʱ�ȭ�� true �ʱ�ȭ �Ǿ� �ֱ� ������ ���� �Ͽ��� ������ �ʵ��� false�� ����
					}
					logger.info("container resut:"+result[j].getQid()+"-"+result[j].result);
				}catch(NullPointerException e)
				{
					logger.debug("null resut:"+result[j].result);
					result[j].setResult(false);
				}
			}
		}

		// �α� ���
		logger.debug("end");
	}

	/**@���� �µ� ���� ��
	 * @param message
	 * @param result
	 * @throws SQLException
	 */
	private void checkTemperatureCondition(InboundMsgForData message, ResultMap[] result)
			throws SQLException {
		logger.debug("start");
		for(int j=0;j<result.length;j++)
		{
			
			//�ش� ���ǿ� ���ؼ� ������ true�� ���� �񱳸� �ǽ�, flase�� ���� ���� ��
			if(result[j].isResult())
			{
				if(conditionList[ConditionInfo.TEMPERATURE][j]==null) // �ش� ������  ���� ���� ���
					continue;

				// ����ü���� ���� ���� ������ ����
				ConditionInfo queryCondition=conditionList[ConditionInfo.TEMPERATURE][j];
				
				// ���� ���ǰ� ���ŵ� �����͸� ����
				boolean re = 	queryCondition.getMax()>message.getTemperature()&&
						queryCondition.getMin()<message.getTemperature()
						?false:true;
				logger.info("temperature result:qid:"+result[j].getQid()+" max:"+queryCondition.getMax()+",min:"+queryCondition.getMin()
						+",data:"+message.getTemperature()+",result:"+re);
				
				
				// �� ����� ��� ����ü�� ����
				result[j].setResult(re);
			}
		}	
		
		// �α� ���
		logger.debug("end");
	}

	/**
	 * @���� ��� ����
	 * @param data
	 * @param result
	 * @throws SQLException
	 */
	private void checkHitCondition(InboundMsgForData message, ResultMap[] result)
			throws SQLException {
		logger.debug("start");
		for(int j=0;j<result.length;j++)
		{
			
			if(result[j].isResult())
			{
				if(conditionList[ConditionInfo.HIT][j]==null)
					continue;

				
				// ����ü���� ���� ���� ������ ����
				ConditionInfo queryCondition=conditionList[ConditionInfo.HIT][j];
				
				// ���� ���ǰ� ���ŵ� �����͸� ����
				/*boolean isAccepted = 	queryCondition.getMax()>message.getHitX()&&
						queryCondition.getMin()<message.getHitX()
						?false:true;*/
				
				boolean isAccepted=true;

				result[j].setResult(isAccepted);
				logger.info("hit result:"+isAccepted);
			}
		}
		// �α� ���
		logger.debug("end");
	}

	/**
	 * @see ���� ���� ��
	 * @param data
	 * @param result
	 * @throws SQLException
	 */
	private void checkHumidityCondition(InboundMsgForData message, ResultMap[] result)
			throws SQLException {
		logger.debug("start");
		for(int j=0;j<result.length;j++)
		{
			if(result[j].isResult())
			{
				if(conditionList[ConditionInfo.HUMIDITY][j]==null)
					continue;
				// ����ü���� ���� ���� ������ ����
				ConditionInfo queryCondition=conditionList[ConditionInfo.HUMIDITY][j];
				
				// ���� ���ǰ� ���ŵ� �����͸� ����
				boolean isAccepted = 	queryCondition.getMax()>message.getHumidity()&&
						queryCondition.getMin()<message.getHumidity()
						?false:true;

				result[j].setResult(isAccepted);
				
				logger.info("humidity result:"+isAccepted);
			}
		}		
		logger.debug("end");
	}
	public static void main(String[] args) {
		SingleConditionChecker checker = new SingleConditionChecker();
		
	//	checker.IF_QuerySearch(data, qidList);
	}

}
