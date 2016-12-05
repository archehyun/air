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


	private List<ActionInfo> actionList; //�׼� ���� ����Ʈ
	
	private static double totalQueueTime=0;
	private static double totalProcessTime=0;
	private static int processCount=0;


	/**
	 * ������ 
	 */
	public SingleCQPManager()
	{
		super();
		logger.info("�̱� ��� cqp manager ����");
		System.out.println("�̱� ��� cqp manager ����");
	}

	public static double getAvgQueueWaitTime()
	{
		if(processCount==0)
			return 0;
		return totalQueueTime/processCount;
	}

	/**
	 * ���� ó�� ����
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

				/** ��ó�� ����
				 * �ιٿ�� �޼��� ť����  �±� �����͸� ������  
				 *  
				 **/

				InboundMsgForData message = (InboundMsgForData) mqCQP.poll();
				

				Long startTime = System.currentTimeMillis();// ó�� �ð� ���: ���� ó�� ���� �ð�

				totalQueueTime+=(System.currentTimeMillis()-message.getCurrentTime());
				processCount++;
				
				
				queueSize = mqCQP.getLength();
				avgQueueWaitTime =totalQueueTime/processCount;

				System.out.print("size "+mqCQP.getLength()+" queue avg "+totalQueueTime/processCount);


				String[] qid		= worker.extractQID(worker.matchingUserList(message));// �˻��� ���� ���̵� ����Ʈ

				/** ���� ��
				 **/

				int[][] resultArray=checker.IF_QuerySearch(message, qid);// ���� ó�� ���

				// ȭ�鿡 �α׸� ����ϱ� ���� ���
				//notifyMonitors(dateFormat.format(System.currentTimeMillis())+"\n"+PrintUtil.printConditionMap(resultArray));
				//notifyMonitors(dateFormat.format(System.currentTimeMillis())+"\n"+PrintUtil.printConditionMap(resultArray[0]));


				/** ���� ����
				 * ���� ó�� ����� ���� ������ �����ϴ� ����� ���� ��쿡 ���� ����� ����
				 * 
				 **/
				if(resultArray!=null)
				{

					//if(executer.checkResult(resultArray[1]))
					{
						actionList // �׼� ����Ʈ ��ȸ

						= TableBufferManager.getInstance().selectListActionInfo();

						//���� ó�� �Ŀ� ����� ��ȯ��
						String acitonResult=executer.IF_QueryAction(actionList, resultArray,message,"user1"); // xml result

						/**
						 * 
						 * �� ����ڿ��� ������ ���� ��� ����
						 */
						MsgForAPI msg = executer.createMsg("user1", 0, acitonResult);
						executer.execute(msg);

						//logger.debug("save data:\n"+acitonResult);
						Long endTime = System.currentTimeMillis();// ���� ó�� ���� �ð� ���
						processTime = endTime-startTime;// ���� ó�� �ð� ���
						this.setProcessTime(processTime);
						totalProcessTime+=processTime;

						MonitorMessage messages = new MonitorMessage();
						messages.setActionResult(acitonResult);
						messages.setQueueSize(queueSize);
						messages.setAvgProcessTime(totalProcessTime/processCount);	
						messages.setAvgQueueWaitTime(avgQueueWaitTime);
						messages.setProcessCount(processCount);
						
						notifyMonitors(messages);
						logger.info("start time:"+startTime+", end time:"+endTime+",process time:"+(processTime));// ���� ó�� �ð� �α� ���
					}
				}
				System.out.println(" processTime "+(System.currentTimeMillis()-startTime));
				
				logger.debug("<==query process end==>\n");


			} catch (SQLException e) {
				e.printStackTrace();
			}
			catch (NullAIRMessageException e) {
				System.err.println("�޽��� ������ Ʋ�½��ϴ�.");
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
