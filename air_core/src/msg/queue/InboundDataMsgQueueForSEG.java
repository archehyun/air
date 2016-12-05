package msg.queue;

import msg.node.QueueNode;


/**
 * Spatial Event Generator를 위한 Inbound Data Message Queue를 관리하는 클래스
 * 
 * @author		박병권
 * @since       2014-01-29
 * @version     0.1       
 */
public class InboundDataMsgQueueForSEG extends MsgQueue
{
	private static InboundDataMsgQueueForSEG inboundDataMsgQueueForSEG; //Spatial Event Generator를 위한 Inbound Data Message Queue
	
	/**
	 * Class constructor
	 * Inbound Data Message Queue Instance를 여기서 생성함
	 */
	static
	{
		inboundDataMsgQueueForSEG = new InboundDataMsgQueueForSEG();
	}
	
	/*
	 * Inbound Data Message Queue Instance를 획득할 수 있는 클래스 메서드
	 */
	public static InboundDataMsgQueueForSEG getInstance()
	{
		return inboundDataMsgQueueForSEG;
	}
	
	public InboundDataMsgQueueForSEG()
	{
		super();
	}
	
	public synchronized boolean append(QueueNode msgNode)
	{		
		if (super.append(msgNode))
		{
			notifyAll();
		}
		
		return true;
	}
	
	public synchronized QueueNode poll() 
	{
		QueueNode node = null;
		
		while((node = super.poll()) == null)
		{
			try 
			{
				wait();
			} 
			catch (InterruptedException e) 
			{
				//e.printStackTrace();
			}
		}
		
		return node;
	}
}
