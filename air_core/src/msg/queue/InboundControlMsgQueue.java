package msg.queue;

import msg.node.QueueNode;


/**
 * Inbound Control Message Queue를 관리하는 클래스
 * 
 * @author		박병권
 * @since       2014-01-29
 * @version     0.1       
 */
public class InboundControlMsgQueue extends MsgQueue
{
	private static InboundControlMsgQueue inboundControlMsgQueue; //Inbound Control Message Queue
	
	/**
	 * Class constructor
	 * Message Queue Instance를 여기서 생성함
	 */
	static
	{
		inboundControlMsgQueue = new InboundControlMsgQueue();
	}
	
	/*
	 * Inbound Control Message Queue 객체를 획득할 수 있는 클래스 메서드
	 */
	public static InboundControlMsgQueue getInstance()
	{
		return inboundControlMsgQueue;
	}
	
	public InboundControlMsgQueue()
	{
		super();
	}

	public int size() {
		return 0;
	}
	public synchronized boolean append(QueueNode msgNode)
	{		
		if (super.append(msgNode))
		{
			notifyAll();
		}
		
		return true;
	}

	/* 박창현 추가
	 * (non-Javadoc)
	 * @see msg.queue.MsgQueue#append(msg.node.MsgNode)
	 */
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
