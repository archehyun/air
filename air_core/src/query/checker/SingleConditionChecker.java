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
		logger.info("single checker  생성");
		
		
	}
	
	/* (non-Javadoc)
	 * @see air.cqp.IFConditionChecker#IF_QuerySearch(air.mq.AIRMessage, java.lang.String[])
	 */
	@Override
	public int[][] IF_QuerySearch(InboundMsgForData data, String[] qidList)
			throws SQLException {
		
		logger.info("tid:"+data.getTid()+",cid:"+data.getCid());
		initConditionMap(data.getTid(),qidList);// 질의정보를 저장하기 위한 구조체 생성
		
		
		/*질의 처리가 진행 되면서 처리된 결과를 임시적으로 저장하는 구조체
		 * 처리할 질의수를 1차원 배열의 길이로 생성되며 처리 결과를 저장하기 위한 속성과 메소드가 정의 되어 있음 
		 * 각 단계(온도, 습도, 충격,..)에서 처리된 결과를 저장
		 * 초기 생성시 결과는 true로 할당됨
		 */		
		ResultMap resultArray[] = new ResultMap[qidList.length];
		
		for(int i=0;i<resultArray.length;i++)
		{
			resultArray[i] = new ResultMap();
			resultArray[i].setQid(qidList[i]);// 각 처리결과 객체에 식별을위한 질의 아이디 할당
		}
		
		logger.debug("tid:"+data.getTid());
		/*
		 *  앞의 initConditionMap 메소드에서 정의한 순서대로 질의 처리를 진행
		 *  각 단계에서 처리된 결과는 ResultMap에서 저장되고 다음 단계서 이를 활용함
		 *  처리 순서는 컨테이너, 온도, 충격, 습도, 순의로 진행
		 *  각각 조건별로 별도의 메소드를 통해서 절의 처리가 진행
		 */
		checkContainerCondition(data, resultArray);// container Condition
		checkTemperatureCondition(data, resultArray);// Temperature Condition		
		checkHitCondition(data, resultArray);// Hit Condition		
		checkHumidityCondition(data, resultArray);// Humidity Condition

		/*Create ResultArray ==================
		 * 처리된 결과는 결과는 행:2, 열:질의수 인 int형 배열로 생성됨 
		 * 배열 첫번째 행에는 질의 아이디, 두번째 행에는 질의 처리 결과가 0:false, 1:true 형태로 저장
		 */
		
		int result[][] = new int[2][resultArray.length];
		for(int i=0;i<resultArray.length;i++)
		{
			result[0][i]= Integer.parseInt(resultArray[i].getQid());
			result[1][i]= resultArray[i].isResult()?1:0;
		}

		//TODO 워킹 메모리 작업
		logger.debug("end");
		return result;
	}	
	
	/**@설명 컨테이너 조건 비교
	 * @param data 태그 메시지
	 * @param result 비교 결과 값
	 */
	private void checkContainerCondition(InboundMsgForData data, ResultMap[] result) {
		logger.debug("start");
		for(int j=0;j<result.length;j++)
		{			
			if(result[j].isResult())
			{
				if(conditionList[ConditionInfo.CONTAINER][j]==null) // 해당 조건을  없을 때는 통과
				{
					System.out.println("continue");
					continue;
				}
				
				// 구조체에서 비교할 질의 조건을 추출
				String cid=conditionList[ConditionInfo.CONTAINER][j].getCid();// 조건이 있을 경우


				try{
					if(!data.getCid().equals(cid))// 조건에 만족하지 않으면
					{
						result[j].setResult(false);//결과 배열의 초기화시 true 초기화 되어 있기 때문에 다은 턴에서 비교하지 않도록 false로 설정
					}
					logger.info("container resut:"+result[j].getQid()+"-"+result[j].result);
				}catch(NullPointerException e)
				{
					logger.debug("null resut:"+result[j].result);
					result[j].setResult(false);
				}
			}
		}

		// 로그 출력
		logger.debug("end");
	}

	/**@설명 온도 조건 비교
	 * @param message
	 * @param result
	 * @throws SQLException
	 */
	private void checkTemperatureCondition(InboundMsgForData message, ResultMap[] result)
			throws SQLException {
		logger.debug("start");
		for(int j=0;j<result.length;j++)
		{
			
			//해당 질의에 대해서 조건을 true면 질의 비교를 실시, flase면 다은 질의 비교
			if(result[j].isResult())
			{
				if(conditionList[ConditionInfo.TEMPERATURE][j]==null) // 해당 조건을  없을 때는 통과
					continue;

				// 구조체에서 비교할 질의 조건을 추출
				ConditionInfo queryCondition=conditionList[ConditionInfo.TEMPERATURE][j];
				
				// 질의 조건과 수신된 데이터를 비교함
				boolean re = 	queryCondition.getMax()>message.getTemperature()&&
						queryCondition.getMin()<message.getTemperature()
						?false:true;
				logger.info("temperature result:qid:"+result[j].getQid()+" max:"+queryCondition.getMax()+",min:"+queryCondition.getMin()
						+",data:"+message.getTemperature()+",result:"+re);
				
				
				// 비교 결과를 결과 구조체에 저장
				result[j].setResult(re);
			}
		}	
		
		// 로그 출력
		logger.debug("end");
	}

	/**
	 * @설명 충격 조건
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

				
				// 구조체에서 비교할 질의 조건을 추출
				ConditionInfo queryCondition=conditionList[ConditionInfo.HIT][j];
				
				// 질의 조건과 수신된 데이터를 비교함
				/*boolean isAccepted = 	queryCondition.getMax()>message.getHitX()&&
						queryCondition.getMin()<message.getHitX()
						?false:true;*/
				
				boolean isAccepted=true;

				result[j].setResult(isAccepted);
				logger.info("hit result:"+isAccepted);
			}
		}
		// 로그 출력
		logger.debug("end");
	}

	/**
	 * @see 습도 조건 비교
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
				// 구조체에서 비교할 질의 조건을 추출
				ConditionInfo queryCondition=conditionList[ConditionInfo.HUMIDITY][j];
				
				// 질의 조건과 수신된 데이터를 비교함
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
