package msg.queue;

import msg.node.MsgForAPI;
import msg.node.QueueNode;

/**
 * API Message Queue를 관리하는 클래스
 * 
 * @author		박병권
 * @since       2014-01-29
 * @version     0.1       
 */
public class MsgQueueForAPI extends MsgQueue
{
	private static MsgQueueForAPI apiMsgQueue; //API Module을 위한 Message Queue
	
	/**
	 * Class constructor
	 * API Message Queue Instance를 여기서 생성함
	 */
	static
	{
		apiMsgQueue = new MsgQueueForAPI();
	}
	
	/*
	 * API Module을 위한 Message Queue Instance를 획득할 수 있는 클래스 메서드
	 */
	public static MsgQueueForAPI getInstance()
	{
		return apiMsgQueue;
	}
	
	public MsgQueueForAPI()
	{
		super();
	}
	
	/**
	 * 해당하는 API Message를 뽑아서 return함
	 * 
	 * @param userID	해당 질의를 등록한 user의 ID
	 * @param queryID	처리 결과를 얻고자 하는 query의 ID
	 * @return			API Message if it exist, otherwise null          
	 */
	public synchronized MsgForAPI poll(String userID, int queryID)
	{
		MsgForAPI retMsg = null;
		if (first != null)
		{
			MsgForAPI m = (MsgForAPI)first;
			while(m != null)
			{
				QueueNode n = m.getNext();
				
				// 수정
				if (m.getUserID().equals(userID) && m.getQueryID() == queryID)
				{
					retMsg = m;
					
					if (m == first)
					{
						first = n;
					}
					else
					{
						m.getPrev().setNext(n);
					}
					
					if (m == last)
					{
						last = null;
					}
					else
					{
						n.setPrev(m.getPrev());
					}
					
					break;
				}
				else
				{
					m = (MsgForAPI)n;
				}
				try 
				{
					wait();
				} 
				catch (InterruptedException e) 
				{
					//e.printStackTrace();
				}
			}
		}
		else
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
		
		return retMsg;
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
