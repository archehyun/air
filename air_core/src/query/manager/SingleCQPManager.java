package query.manager;

import java.sql.SQLException;
import java.util.List;

import msg.node.InboundMsgForData;
import msg.node.MsgForAPI;
import query.CQPManager;
import query.IFActionExecuter;
import query.IFConditionChecker;
import query.NullAIRMessageException;
import query.checker.SingleConditionChecker;
import query.executer.ActionExecuter;
import server.MonitorMessage;
import buffer.dao.TableBufferManager;
import buffer.info.ActionInfo;

public class SingleCQPManager extends CQPManager implements Runnable{


	private List<ActionInfo> actionList; //액션 저장 리스트
	
	private static double totalQueueTime=0;
	private static double totalProcessTime=0;
	private static int processCount=0;


	/**
	 * 생성자 
	 */
	public SingleCQPManager()
	{
		super();
		logger.info("싱글 방식 cqp manager 생성");
		System.out.println("싱글 방식 cqp manager 생성");
	}

	public static double getAvgQueueWaitTime()
	{
		if(processCount==0)
			return 0;
		return totalQueueTime/processCount;
	}

	/**
	 * 질의 처리 종료
	 */
	public void cqpStop()
	{
		isStarted=false;
	}
	int queueSize;
	double avgQueueWaitTime;
	double processTime;
	public void run()
	{
		logger.info("CQP Start...");

		while(isStarted)
		{
			try {

				/** 전처리 과정
				 * 인바운드 메세지 큐에서  태그 데이터를 가져옴  
				 *  
				 **/

				InboundMsgForData message = (InboundMsgForData) mqCQP.poll();
				

				Long startTime = System.currentTimeMillis();// 처리 시간 기록: 질의 처리 시작 시간

				totalQueueTime+=(System.currentTimeMillis()-message.getCurrentTime());
				processCount++;
				
				
				queueSize = mqCQP.getLength();
				avgQueueWaitTime =totalQueueTime/processCount;

				System.out.print("size "+mqCQP.getLength()+" queue avg "+totalQueueTime/processCount);


				String[] qid		= worker.extractQID(worker.matchingUserList(message));// 검색된 질의 아이디 리스트

				/** 질의 비교
				 **/

				int[][] resultArray=checker.IF_QuerySearch(message, qid);// 질의 처리 결과

				// 화면에 로그를 출력하기 위한 모듈
				//notifyMonitors(dateFormat.format(System.currentTimeMillis())+"\n"+PrintUtil.printConditionMap(resultArray));
				//notifyMonitors(dateFormat.format(System.currentTimeMillis())+"\n"+PrintUtil.printConditionMap(resultArray[0]));


				/** 질의 실행
				 * 질의 처리 결과중 질의 조건을 만조하는 결과가 있을 경우에 질의 결과를 전송
				 * 
				 **/
				if(resultArray!=null)
				{

					//if(executer.checkResult(resultArray[1]))
					{
						actionList // 액션 리스트 조회

						= TableBufferManager.getInstance().selectListActionInfo();

						//질의 처리 후에 결과를 반환함
						String acitonResult=executer.IF_QueryAction(actionList, resultArray,message,"user1"); // xml result

						/**
						 * 
						 * 각 사용자에게 각각의 질의 결과 전송
						 */
						MsgForAPI msg = executer.createMsg("user1", 0, acitonResult);
						executer.execute(msg);

						//logger.debug("save data:\n"+acitonResult);
						Long endTime = System.currentTimeMillis();// 질의 처리 종료 시간 기록
						processTime = endTime-startTime;// 질의 처리 시간 기록
						this.setProcessTime(processTime);
						totalProcessTime+=processTime;

						MonitorMessage messages = new MonitorMessage();
						messages.setActionResult(acitonResult);
						messages.setQueueSize(queueSize);
						messages.setAvgProcessTime(totalProcessTime/processCount);	
						messages.setAvgQueueWaitTime(avgQueueWaitTime);
						messages.setProcessCount(processCount);
						
						notifyMonitors(messages);
						logger.info("start time:"+startTime+", end time:"+endTime+",process time:"+(processTime));// 질의 처리 시간 로그 출력
					}
				}
				System.out.println(" processTime "+(System.currentTimeMillis()-startTime));
				
				logger.debug("<==query process end==>\n");


			} catch (SQLException e) {
				e.printStackTrace();
			}
			catch (NullAIRMessageException e) {
				System.err.println("메시지 형식이 틀력습니다.");
				System.exit(1);
			}
		}
	}

	



	public IFConditionChecker createConditionChecker() {
		return new SingleConditionChecker();
	}

	public IFActionExecuter createActionExecuter() {
		// TODO Auto-generated method stub
		return new ActionExecuter();
	}
	@Override
	public void serverStart() {
		if(thread== null)
		{
			isStarted=true;
			thread = new Thread(this);  
			thread.start();
		}
		
	}

	@Override
	public void serverStop() {
		
		isStarted=false;
		thread = null;
		
	}
}
