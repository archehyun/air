package msg.queue;

import msg.node.QueueNode;


/**
 * Spatial Event Generator�� ���� Inbound Data Message Queue�� �����ϴ� Ŭ����
 * 
 * @author		�ں���
 * @since       2014-01-29
 * @version     0.1       
 */
public class InboundDataMsgQueueForSEG extends MsgQueue
{
	private static InboundDataMsgQueueForSEG inboundDataMsgQueueForSEG; //Spatial Event Generator�� ���� Inbound Data Message Queue
	
	/**
	 * Class constructor
	 * Inbound Data Message Queue Instance�� ���⼭ ������
	 */
	static
	{
		inboundDataMsgQueueForSEG = new InboundDataMsgQueueForSEG();
	}
	
	/*
	 * Inbound Data Message Queue Instance�� ȹ���� �� �ִ� Ŭ���� �޼���
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
