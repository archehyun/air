package query.checker;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import query.IFConditionChecker;
import buffer.dao.TableBufferManager;
import buffer.info.ConditionInfo;

/**
 * 질의를 검색 및 구분하는 클래스
 * 
 * @author		박창현
 * @since       2014-01-29
 * @version     0.1       
 */

public abstract class DefaultConditionChecker implements IFConditionChecker{


	protected TableBufferManager bufferManager = TableBufferManager.getInstance();
	protected ConditionInfo conditionList[][]; // 질의 조건 비교용 저장 배열
	protected ConditionMapManager conditionMapList[][]; // 질의 조건 비교용 저장 배열
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
	 * 질의 조건 초기화
	 * @param qidList 질의 아이디 목록
	 * @throws SQLException 
	 */
	protected void initConditionMap2(String[] qidList) throws SQLException
	{
		/* 질의 처리를 위한 구조체 생성
		 * ConditionInfo 클래스  배열형태(행:조건수, 열:질의수)로 메모리 상에 생성 
		 * 각 배열의 요소는 질의 조건 정의한 ConditionInfo 클래스의 인스턴스를 생성하여 할당
		 *  ConditionInfo 인스턴스는 bufferManager를 통해서 DB에서 조회하여 생성함
		 *  해당 조건을 없을 경우에는 배열의 요소에 null을 할당
		 */
		double start = System.currentTimeMillis();
		conditionList = new ConditionInfo[5][qidList.length];

		// 검색 조건에 대한 순서 지정: 컨테이너, 온도, 습도, 충격, 일자 순으로 검색 진행

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
