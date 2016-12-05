package msg.queue;

import msg.node.InboundMsg;
import msg.node.QueueNode;
import msg.node.TagControlMsg;

public class TagMsgQueue extends MsgQueue{

	private static TagMsgQueue tagMsgQueue;
	static
	{
		tagMsgQueue = new TagMsgQueue();
	}

	/*
	 * Inbound Control Message Queue °´Ã¼¸¦ È¹µæÇÒ ¼ö ÀÖ´Â Å¬·¡½º ¸Þ¼­µå
	 */
	public static TagMsgQueue  getInstance()
	{
		return tagMsgQueue;
	}

	public TagMsgQueue ()
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
	public synchronized boolean insert(InboundMsg msgNode)
	{	

		if (first == null || last instanceof InboundMsg)
		{
			return this.append(msgNode);
		}

		else if (first instanceof TagControlMsg)
		{
			msgNode.setNext(first);
			first.setPrev(msgNode);
			first =msgNode;
		}
		else
		{			
			QueueNode p = first.getNext();
			QueueNode q = first; 
			while(p!=null&&!(p instanceof TagControlMsg))
			{	
				q =p;
				p=p.getNext();
				
			}
			msgNode.setNext(p);
			q.setNext(msgNode);
		}		


		notifyAll();


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

