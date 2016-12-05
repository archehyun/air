package query.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import msg.node.InboundMsgForData;
import msg.node.MsgForAPI;
import msg.queue.CQPTagQueue;
import query.CQPManager;
import query.DBWorker;
import query.IFActionExecuter;
import query.IFConditionChecker;
import query.NullAIRMessageException;
import query.QueryStatics;
import query.checker.SingleConditionChecker;
import query.executer.ActionExecuter;
import server.MonitorMessage;
import buffer.info.ActionInfo;
import buffer.info.TagInfo;
import buffer.info.TagUserInfo;

/**
 * @author archehyun
 *
 */

public class MultiCQPManager extends CQPManager implements Runnable{
	
	// ���� �޼��� ��
	private int messageCount;

	private HashMap<String, TagThread> tagList;

	public HashMap<String, TagThread> getTagList() {
		return tagList;
	}

	/**
	 * 
	 */
	public MultiCQPManager() {
		logger.info("��Ƽ ��� cqp manager ����");
		avgQueueList = new AvgQueueList(100);
		
		messageCount = 0;
	}

	public AvgQueueList avgQueueList;	

	public void cqpStop()
	{
		isStarted=false;
	}
	public void run()
	{
		tagList = new HashMap<String, TagThread>();
		
		while(isStarted)
		{
			InboundMsgForData message = (InboundMsgForData) mqCQP.poll();
			messageCount++;
			logger.debug("ó�� �޼��� ��: "+messageCount);
			
			String tid=message.getTid();
			logger.info("tag id:"+tid);
			
			if(tagList.containsKey(message.getTid()))
			{
				TagThread tag =tagList.get(message.getTid());			
				tag.append(message);
			}
			else
			{
				try{
					TagThread tagThread = new TagThread(this,message.getTid());
					tagThread.start();

					tagList.put(message.getTid(), tagThread);
					tagThread.append(message);
				}catch(Exception ee)
				{
					ee.printStackTrace();
				}
			}
		}
	}
	
	public synchronized void updateProcessInfo(MonitorMessage message)
	{
		StringBuffer result=new StringBuffer();
		result.append("�±� ID:\t"+message.getTid()+"\n");
		result.append("ó�� ��:\t"+message.getProcessCount()+"\n");
		result.append("��� ť ��� �ð�:\t"+message.getAvgQueueWaitTime()+"\n");
		//result.append("��� ó�� �ð� �ð�:\t"+String.format("%.2f",message.getStatics().getAverageTagProcessTime())+"\n");
		logger.info("\n<----���---->\n"+result.toString());
		notifyMonitors(message);
	}

	/**
	 * @author archehyun
	 *
	 */
	class TagThread extends Thread
	{
		private QueryStatics queryStatics;
		
		public QueryStatics getQueryStatics() {
			return queryStatics;
		}

		private CQPTagQueue queue;//

		private DBWorker worker;//

		private StringBuffer strResultarray = new StringBuffer();
		
		private String tid;		// ���ŵ� �±� ���̵�

		private List<TagUserInfo> userList;	// �±׸� ����� ����� ���
		
		private List<ActionInfo> actionList; //�׼� ���� ����Ʈ

		private IFConditionChecker checker; //���� �� ��ü
		
		private IFActionExecuter executer; //���� ��ü	
		
		private MultiCQPManager manager;

		public TagThread(MultiCQPManager manager,String tid) throws SQLException {
			
			logger.info("tagThread����:"+tid);
			this.setName(tid);
			this.setTid(tid);

			queryStatics = new QueryStatics(tid);		

			this.manager = manager;
			checker = MultiCQPManager.this.createConditionChecker();
			executer = MultiCQPManager.this.createActionExecuter();
			processTimeQueue = new LinkedList<>();
			queue = new CQPTagQueue();

			worker = new DBWorker();

		}
		public void run()
		{
			while (flag)
			{
				InboundMsgForData message = (InboundMsgForData) queue.poll();
				try {
					
					logger.info("message: "+message.getTid()+","+message.getTemperature());
					process(message);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		/**
		 * @return
		 */
		public String getTid() {
			return tid;
		}

		/**
		 * @param tid
		 */
		public void setTid(String tid) {
			this.tid = tid;
		}

		public synchronized void append(InboundMsgForData message)
		{
			queue.append(message);
		}

		/**
		 * @param message
		 * @return
		 * @throws SQLException
		 */
		private String[] extractQID(InboundMsgForData message) throws SQLException
		{	
			Long startTime = System.currentTimeMillis();		

			userList = worker.matchingUserList(message);
			
			
			
			String qid[]=worker.extractQID(message.getTid());
			
			System.out.println("matching user:"+userList+", "+"qid:"+qid.length);

			queryStatics.setDbIOProcessTime(System.currentTimeMillis()-startTime);


			return qid;
		}

		/**
		 * @param message
		 * @throws SQLException 
		 */
		private void process(InboundMsgForData message) throws SQLException
		{
			try{							

				logger.debug("query process start");
				// ó�� �ð� ���: ���� ó�� ���� �ð�
				Long startTime = System.currentTimeMillis();				

				// �±��� ���μ��� ī��Ʈ�� ���� ��Ŵ
				queryStatics.updateTagProcessCount(); 

				// �˻��� ���� ���̵� ����Ʈ
				String[] qid		=extractQID(message);
				
				StringBuffer qidlist = new StringBuffer();
				
				for(int i=0;i<qid.length;i++)
				{
					qidlist.append(qid[i]+",");
				}
				
				/** ���� ��
				 **/

				double searchStartTime = 	System.currentTimeMillis();

				int[][] resultArray=checker.IF_QuerySearch(message, qid);// ���� ó�� ���
				
				StringBuffer results = createResult(resultArray);
				
				logger.debug("result:\n"+results.toString());

				searchEndTime =				System.currentTimeMillis()-searchStartTime;

				/** 
				 * ���� ����
				 * ���� ó�� ����� ���� ������ �����ϴ� ����� ���� ��쿡 ���� ����� ����
				 * 
				 **/
				{

					{

						
						actionList =worker.getActionList();

						strResultarray.delete(0, strResultarray.length());
						
						for(int i=0;i<resultArray.length;i++)
						{
							for(int j=0;j<resultArray[i].length;j++)
							{
								strResultarray.append(resultArray[i][j]+",");
							}	
							strResultarray.append("\n");
						}
						
						for(int i=0;i<userList.size();i++)
						{
							TagInfo  userinfo = (TagInfo) userList.get(i);
							//���� ó�� �Ŀ� ����� ��ȯ��
							String acitonResult=executer.IF_QueryAction(actionList, resultArray,message,userinfo.getUser_id()); // xml result
							logger.debug("<actionResult>\n"+acitonResult);

							/**
							 * 
							 * �� ����ڿ��� ������ ���� ��� ����
							 */


							MsgForAPI msg = executer.createMsg(userinfo.getUser_id(), 0, acitonResult);
							
							executer.execute(msg);
							
							queryStatics.updateTagTotalProcessTime(System.currentTimeMillis()-startTime);
						}	

						/**
						 * 
						 * ����͸� �޼��� ����
						 */

						//double actionExeucteTimeEnd = System.currentTimeMillis()-actionExeucteTimeStart;

						logger.debug("send message");
						Long endTime = System.currentTimeMillis();// ���� ó�� ���� �ð� ���
						
						processTime = endTime-startTime;// ���� ó�� �ð� ���
						
						queryStatics.updateTotalProcessTime(processTime);
						
						queryStatics.updateTagTotalProcessTime(processTime);

						messages = new MonitorMessage();
						
						messages.setStatics(queryStatics);
						
						messages.setTemperature(message.getTemperature());
						messages.setHumidity(message.getHumidity());
						messages.setLat(message.getLatitude());
						messages.setLng(message.getLongitude());

						messages.setQueueSize(queueSize);
						messages.setAvgProcessTime(totalProcessTime/processCount);	
						messages.setAvgQueueWaitTime(avgQueueList.getAverage());
						messages.setProcessCount(queryStatics.getTagProcessCount());
						messages.setThreadCount(tagList.size());
						messages.setTid(getTid());
						messages.setTagListInfo(getTagListInfo());		
						messages.setDoor(message.getDoor());


						/*logger.info("\n<---�ð�--->\nt-start\tt-end\tt-search\tt-action\tt-process\tt-dbio\n"+
								""+dateFormat.format(new Date(startTime))+"\t"+dateFormat.format(new Date(endTime))+"\t"+searchEndTime+"\t"+actionExeucteTimeEnd+"\t"+(processTime)+"\t"+(dbIOProcessTime));*/

						manager.updateProcessInfo(messages);
						logger.debug("query process end("+processTime+"s)");
					}
				}
			}

			catch (NullAIRMessageException e) {
				System.err.println("�޽��� ������ Ʋ�½��ϴ�.");
				System.exit(1);
			}
		}
		ArrayList<QueryStatics> list=null;
		private ArrayList<QueryStatics> getTagListInfo() {
			ArrayList<QueryStatics> list = new ArrayList<QueryStatics>();
			Iterator iter=tagList.keySet().iterator();
			while(iter.hasNext())
			{
				String tid = (String) iter.next();
				TagThread thread = tagList.get(tid);
				list.add(thread.getQueryStatics());

			}		

			return list;
		}
		private StringBuffer createResult(int[][] resultArray) {
			StringBuffer results = new StringBuffer();
			String temp="";
			for(int i=0;i<resultArray.length;i++)
			{
				for(int j=0;j<resultArray[i].length;j++)
				{
					temp+=resultArray[i][j]+",";
				}
				results.append(temp+"\n");
				temp="";

			}
			return results;
		}


	}




	public IFConditionChecker createConditionChecker() {
		// TODO Auto-generated method stub
		return new SingleConditionChecker();
	}

	public IFActionExecuter createActionExecuter() {
		// TODO Auto-generated method stub
		return new ActionExecuter();
	}



	/**
	 * @author archehyun
	 *
	 */
	class AvgQueueList extends Vector<Long>
	{
		int n;
		/**
		 * @param n
		 */
		public AvgQueueList(int n) {
			super(n);
			this.n=n;
		}
		public void put(Long a)
		{
			this.add(a);
			if(this.size()>n)
				this.remove(0);
		}
		public Long getAverage()
		{
			Long result=0l;
			for(int i=0;i<this.size();i++)
			{
				result+=this.get(i);
			}
			return result/n;
		}
	}
	
	@Override
	public void serverStart() {
		if(thread== null)
		{
			logger.info("��Ƽ ��� cqp manager ����");
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

