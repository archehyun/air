package msg.queue;

import msg.node.QueueNode;

/**
 * 모든 Message Queue의 슈퍼 클래스
 * 
 * @author		박병권
 * @since       2014-01-29
 * @version     0.1       
 */
public class MsgQueue
{
	protected QueueNode first; //pointer to the first node of Linked List
	protected QueueNode last; //pointer to the last node of Linked List
	
	public MsgQueue()
	{
		first = null;
		last = null;
	}
	
	/**
	 * 새로운 Message를 큐 끝에 추가
	 * 
	 * @param newMsg	큐에 추가할 Control Message
	 * @return			true if succeed, otherwise false          
	 */
	public synchronized boolean append(QueueNode newMsg)
	{
		if (first == null)
		{
			first = newMsg;
			last = newMsg;
		}
		else
		{
			newMsg.setPrev(last);
			last.setNext(newMsg);
			last = newMsg;
		}
		
		return true;
	}
	
	/**
	 * 큐의 첫번째 Message를 뽑아서 return 함
	 * 
	 * @param 			없음
	 * @return			Message if it exist, otherwise null          
	 */
	public synchronized QueueNode poll()
	{
		QueueNode retMsg = null;
		
		if (first == null)
		{
			retMsg = null;
		}
		else if (first == last)
		{
			retMsg = first;
			first = null;
			last = null;
		}
		else
		{
			retMsg = first; 
			first = first.getNext();;
			first.setPrev(null);
		}
		
		return retMsg;
	}
}
