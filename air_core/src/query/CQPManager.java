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
 * ���� ó�� ���
 * 
 * @author		��â��
 * @since       2014-01-25
 * @version     0.1       
 */
public abstract class CQPManager  extends AIRThread{
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	protected DBWorker worker;
	
	protected IFConditionChecker checker; //���� �� ��ü

	protected IFActionExecuter executer; //���� ��ü

	protected List<ActionInfo> actionList; //�׼� ���� ����Ʈ

	protected InboundDataMsgQueueForQP mqCQP = InboundDataMsgQueueForQP.getInstance(); // �޼��� ť

	protected TableBufferManager bufferManager=TableBufferManager.getInstance(); // ���۸޴���
	
	protected double totalQueueTime=0;
		
	protected double totalProcessTime=0;
	
	protected int processCount=0;

	protected boolean flag = true; // ������ ���� �÷���	

	protected double avgProcessTime=0;
	
	protected double minProcessTime=0;
	
	protected double maxProcessTime=0;
	
	protected double searchEndTime=0;

	public static final int MULTI=1; 
	
	public static final int SINGLE=2;
	
	protected Queue<Long> processTimeQueue;// ���μ��� ������ ���� ť
	private static CQPManager instance =null;
	/**
	 * ������ 
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
	 * ���� ó�� ����
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
		private static String conditions[] ={"qid","result","����","�µ�","���","��ġ"};
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
