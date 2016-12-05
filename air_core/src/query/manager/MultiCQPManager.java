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
	
	// 받은 메세지 수
	private int messageCount;

	private HashMap<String, TagThread> tagList;

	public HashMap<String, TagThread> getTagList() {
		return tagList;
	}

	/**
	 * 
	 */
	public MultiCQPManager() {
		logger.info("멀티 방식 cqp manager 생성");
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
			logger.debug("처리 메세지 수: "+messageCount);
			
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
		result.append("태그 ID:\t"+message.getTid()+"\n");
		result.append("처리 수:\t"+message.getProcessCount()+"\n");
		result.append("평균 큐 대기 시간:\t"+message.getAvgQueueWaitTime()+"\n");
		//result.append("평균 처리 시간 시간:\t"+String.format("%.2f",message.getStatics().getAverageTagProcessTime())+"\n");
		logger.info("\n<----결과---->\n"+result.toString());
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
		
		private String tid;		// 수신된 태그 아이디

		private List<TagUserInfo> userList;	// 태그를 등록한 사용자 목록
		
		private List<ActionInfo> actionList; //액션 저장 리스트

		private IFConditionChecker checker; //질의 비교 객체
		
		private IFActionExecuter executer; //실행 객체	
		
		private MultiCQPManager manager;

		public TagThread(MultiCQPManager manager,String tid) throws SQLException {
			
			logger.info("tagThread생성:"+tid);
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
				// 처리 시간 기록: 질의 처리 시작 시간
				Long startTime = System.currentTimeMillis();				

				// 태그의 프로세스 카우트를 증가 시킴
				queryStatics.updateTagProcessCount(); 

				// 검색된 질의 아이디 리스트
				String[] qid		=extractQID(message);
				
				StringBuffer qidlist = new StringBuffer();
				
				for(int i=0;i<qid.length;i++)
				{
					qidlist.append(qid[i]+",");
				}
				
				/** 질의 비교
				 **/

				double searchStartTime = 	System.currentTimeMillis();

				int[][] resultArray=checker.IF_QuerySearch(message, qid);// 질의 처리 결과
				
				StringBuffer results = createResult(resultArray);
				
				logger.debug("result:\n"+results.toString());

				searchEndTime =				System.currentTimeMillis()-searchStartTime;

				/** 
				 * 질의 실행
				 * 질의 처리 결과중 질의 조건을 만조하는 결과가 있을 경우에 질의 결과를 전송
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
							//질의 처리 후에 결과를 반환함
							String acitonResult=executer.IF_QueryAction(actionList, resultArray,message,userinfo.getUser_id()); // xml result
							logger.debug("<actionResult>\n"+acitonResult);

							/**
							 * 
							 * 각 사용자에게 각각의 질의 결과 전송
							 */


							MsgForAPI msg = executer.createMsg(userinfo.getUser_id(), 0, acitonResult);
							
							executer.execute(msg);
							
							queryStatics.updateTagTotalProcessTime(System.currentTimeMillis()-startTime);
						}	

						/**
						 * 
						 * 모니터링 메세지 전송
						 */

						//double actionExeucteTimeEnd = System.currentTimeMillis()-actionExeucteTimeStart;

						logger.debug("send message");
						Long endTime = System.currentTimeMillis();// 질의 처리 종료 시간 기록
						
						processTime = endTime-startTime;// 질의 처리 시간 기록
						
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


						/*logger.info("\n<---시간--->\nt-start\tt-end\tt-search\tt-action\tt-process\tt-dbio\n"+
								""+dateFormat.format(new Date(startTime))+"\t"+dateFormat.format(new Date(endTime))+"\t"+searchEndTime+"\t"+actionExeucteTimeEnd+"\t"+(processTime)+"\t"+(dbIOProcessTime));*/

						manager.updateProcessInfo(messages);
						logger.debug("query process end("+processTime+"s)");
					}
				}
			}

			catch (NullAIRMessageException e) {
				System.err.println("메시지 형식이 틀력습니다.");
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
			logger.info("멀티 방식 cqp manager 시작");
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

