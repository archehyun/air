package query;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Queue;

import msg.queue.InboundDataMsgQueueForQP;
import query.manager.MultiCQPManager;
import query.manager.SingleCQPManager;
import server.AIRThread;
import server.MonitorMessage;
import buffer.dao.TableBufferManager;
import buffer.info.ActionInfo;

/**
 * 질의 처리 모듈
 * 
 * @author		박창현
 * @since       2014-01-25
 * @version     0.1       
 */
public abstract class CQPManager  extends AIRThread{
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	protected DBWorker worker;
	
	protected IFConditionChecker checker; //질의 비교 객체

	protected IFActionExecuter executer; //실행 객체

	protected List<ActionInfo> actionList; //액션 저장 리스트

	protected InboundDataMsgQueueForQP mqCQP = InboundDataMsgQueueForQP.getInstance(); // 메세지 큐

	protected TableBufferManager bufferManager=TableBufferManager.getInstance(); // 버퍼메니저
	
	protected double totalQueueTime=0;
		
	protected double totalProcessTime=0;
	
	protected int processCount=0;

	protected boolean flag = true; // 스레드 관리 플래그	

	protected double avgProcessTime=0;
	
	protected double minProcessTime=0;
	
	protected double maxProcessTime=0;
	
	protected double searchEndTime=0;

	public static final int MULTI=1; 
	
	public static final int SINGLE=2;
	
	protected Queue<Long> processTimeQueue;// 프로세스 수집을 위한 큐
	private static CQPManager instance =null;
	/**
	 * 생성자 
	 */
	protected CQPManager()
	{
		
		checker = createConditionChecker();
		executer = createActionExecuter();
	}
	public static CQPManager getInstance(int type)
	{
		if(instance==null)
		{			
			switch (type) {
			case CQPManager.MULTI:
				instance = new MultiCQPManager();
				break;
			case CQPManager.SINGLE:
				instance = new SingleCQPManager();
				break;	

			default:
				break;
			}
			
		}
		return instance;
	}

	/**
	 * 질의 처리 종료
	 */
	public void cqpStop()
	{
		flag=false;
	}
	
	
	
	public int queueSize;
	public double avgQueueWaitTime;
	protected double processTime;

	protected MonitorMessage messages;
	
	static class PrintUtil
	{
		private static String conditions[] ={"qid","result","습도","온도","충격","위치"};
		public static  String printConditionMap(int[][] map) {

			StringBuffer buffer = new StringBuffer();
			for(int i=0;i<map.length;i++)
			{

				buffer.append(conditions[i]+"\t|");
				for(int j=0;j<map[i].length;j++)
				{
					buffer.append(map[i][j]+"|");
				}
				buffer.append("\n");
			}
			return buffer.toString();
		}
		public static String printConditionMap(int[] map) {

			StringBuffer buffer = new StringBuffer();
			buffer.append("[");
			for(int i=0;i<map.length;i++)
			{
				buffer.append(map[i]+"\t"+(i<map.length-1?"|":""));
			}
			buffer.append("]");
			return buffer.toString();
		}
	}

	public abstract IFConditionChecker createConditionChecker();

	public abstract IFActionExecuter createActionExecuter();
	
	



}
