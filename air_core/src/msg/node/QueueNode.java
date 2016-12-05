package msg.node;

/**
 * 모든 Message Queue의 노드 객체의 슈퍼클래스
 * 
 * @author		박병권
 * @since       2014-01-25
 * @version     0.1       
 */
public class QueueNode
{
	public static final short NULL = -9999;
	
	private QueueNode prev; //doubly linked list
	private QueueNode next; //doubly linked list
	
	public QueueNode()
	{
		prev = null;
		next = null;
	}

	public QueueNode getPrev()
	{
		return prev;
	}

	public void setPrev(QueueNode prev)
	{
		this.prev = prev;
	}

	public QueueNode getNext()
	{
		return next;
	}

	public void setNext(QueueNode next)
	{
		this.next = next;
	}
}
