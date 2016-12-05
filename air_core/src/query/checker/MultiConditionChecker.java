package query.checker;

import java.sql.SQLException;
import java.util.List;

import msg.node.InboundMsgForData;
import buffer.dao.TableBufferManager;
import buffer.info.ActionInfo;
import buffer.info.ConditionInfo;

/**
 * @author archehyun
 *
 */
public class MultiConditionChecker extends DefaultConditionChecker{

	TableBufferManager bufferManager = TableBufferManager.getInstance();
	private ResultMap resultArrays[];
	public MultiConditionChecker() {
		
		try {
			
			int indexList[]={	ConditionInfo.CONTAINER,
					ConditionInfo.TEMPERATURE,
					ConditionInfo.HUMIDITY,							
					ConditionInfo.HIT,
					ConditionInfo.DATE
			};
			List<ActionInfo> actionList=bufferManager.selectListActionInfo();
			conditionList = new ConditionInfo[5][actionList.size()];
			
			ConditionInfo op = new ConditionInfo();
			for(int i=0;i<actionList.size();i++)
			{
				ActionInfo info  = actionList.get(i);
				op.setQuery_number(info.getQuery_number());
				for(int j=0;j<indexList.length;j++)
				{
					op.setTableType(indexList[j]);
					
					conditionList[j][i]=(ConditionInfo) bufferManager.selectConditionInfo(op);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public int[][] IF_QuerySearch(InboundMsgForData data, String[] qidList)
			throws SQLException {
		
		Long startTime = System.currentTimeMillis();		
		//initConditionMap(qidList);		
		resultArrays = new ResultMap[qidList.length];		
		for(int i=0;i<resultArrays.length;i++)
		{
			resultArrays[i] = new ResultMap();
			resultArrays[i].setQid(qidList[i]);// 각 처리결과 객체에 식별을위한 질의 아이디 할당
		}
		SubChecker ch[] = new SubChecker[qidList.length];
		for(int i=0;i<ch.length;i++)
		{
			ch[i] = new SubChecker(i,data);
			ch[i].start();
		}
		
		int result[][] = new int[2][resultArrays.length];
		for(int i=0;i<resultArrays.length;i++)
		{
			result[0][i]= Integer.parseInt(resultArrays[i].getQid());
			result[1][i]= resultArrays[i].isResult()?1:0;
		}
		Long endTime = System.currentTimeMillis();
		
		Long processTime = endTime - startTime;
		logger.info("search query end("+processTime+"ms)");
		return result;
	}
	
	
	
	/**@설명 멀티처리를 위한 객체
	 * @author archehyun
	 *
	 */
	class SubChecker extends Thread
	{
		int queryIndex;// 질의 인덱스
		InboundMsgForData data;
		boolean result=true;
		public SubChecker(int queryIndex,InboundMsgForData data) {
			this.queryIndex =queryIndex;
			this.data =data;
		}
		public void run()
		{		
			if(!checkContainerID(conditionList[0][queryIndex], data))
				return;
			
			logger.info(conditionList[0][queryIndex].getCid()+","+data.getCid()+", "+conditionList[0][queryIndex].getCid().equals(data.getCid()));
			
			
			// 구조체에서 비교할 질의 조건을 추출
			boolean result;
			if(!(result=checkTemperature(conditionList[ConditionInfo.TEMPERATURE][queryIndex],data)))
				return;
			// 질의 조건과 수신된 데이터를 비교함
			resultArrays[queryIndex].setResult(result);
			if(conditionList[ConditionInfo.TEMPERATURE][queryIndex]!=null)
			logger.info("temperture: "+data.getTemperature()+",lowerbound:"+conditionList[ConditionInfo.TEMPERATURE][queryIndex].getMin()+",upperbound:"+conditionList[ConditionInfo.TEMPERATURE][queryIndex].getMax()+","+result);
			
			if(!(result=checkHumidity(conditionList[ConditionInfo.HUMIDITY][queryIndex],data)))
				return;
			
			if(conditionList[ConditionInfo.HUMIDITY][queryIndex]!=null)
			logger.debug("Humidity: lowerbound:"+conditionList[ConditionInfo.HUMIDITY][queryIndex].getMin()+",upperbound:"+conditionList[ConditionInfo.HUMIDITY][queryIndex].getMax()+","+result);
			resultArrays[queryIndex].setResult(result);
				
		}
		private boolean checkContainerID(ConditionInfo queryCondition,InboundMsgForData data)
		{
			if(queryCondition==null)
				return true;
			return queryCondition.getCid().equals(data.getCid());
		}
		private boolean checkTemperature(ConditionInfo queryCondition,InboundMsgForData data)
		{
			if(queryCondition==null)
				return true;
			
			return 	queryCondition.getMax()<data.getTemperature()||
					queryCondition.getMin()>data.getTemperature()
			?true:false;
		}
		private boolean checkHumidity(ConditionInfo queryCondition,InboundMsgForData data)
		{
			if(queryCondition==null)
				return true;
			
			return queryCondition.getMax()<data.getHumidity()||
			queryCondition.getMin()>data.getTemperature()
			?true:false;
		}
		
	}
}
