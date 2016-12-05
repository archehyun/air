package buffer.info;

/**
 * Container Condition Table의 메모리 객체 클래스
 * 
 * @author		박병권
 * @since       2014-01-30
 * @version     0.1       
 */
public class TableContainerCondition 
{
	private int queryID;
	private int tid;
	private int cid;
	
	public TableContainerCondition()
	{
		queryID = 0;
		tid = 0;
		cid = 0;
	}
	
	public TableContainerCondition(int queryID, int tid, int cid)
	{
		this.queryID = queryID;
		this.tid = tid;
		this.cid = cid;
	}
	
	public int getQueryID() {
		return queryID;
	}

	public void setQueryID(int queryID) {
		this.queryID = queryID;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}
}
