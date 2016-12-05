package msg.queue;

import msg.node.QueueNode;


/**
 * 질의 처리기를 위한 Inbound Data Message Queue를 관리하는 클래스
 * 
 * @author		박병권
 * @since       2014-01-29
 * @version     0.1       
 */
public class InboundDataMsgQueueForQP extends MsgQueue
{
	private static InboundDataMsgQueueForQP inboundDataMsgQueueForQP; //Query Processor를 위한 Inbound Data Message Queue
	
	/**
	 * Class constructor
	 * Inbound Data Message Queue Instance를 여기서 생성함
	 */
	static
	{
		inboundDataMsgQueueForQP = new InboundDataMsgQueueForQP();
	}
	
	/*
	 * Query Processor를 위한 Inbound Data Message Queue Instance를 획득할 수 있는 클래스 메서드
	 */
	public static InboundDataMsgQueueForQP getInstance()
	{
		return inboundDataMsgQueueForQP;
	}
	private int length;
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public InboundDataMsgQueueForQP()
	{
		super();
	}
	
	public synchronized boolean append(QueueNode msgNode)
	{		
		if (super.append(msgNode))
		{
			length++;
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
		length--;
		return node;
	}
}
