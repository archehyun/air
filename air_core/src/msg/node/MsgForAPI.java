package msg.node;


/**
 * 태그 데이터에 대해 조건을 만족하는 사용자 질의의 처리 결과를 XML로 표현하여 사용자에게 전달하기 위한 Message
 * 
 * @author		손희목
 * @since       2014-02-06
 * @version     0.1      
 */
public class MsgForAPI extends QueueNode
{
	private String userID;
	private int queryID;
	private String resultXML; //태그 데이터에 대해 조건을 만족하는 사용자 질의의 처리 결과 데이터(XML로 표현)
	
	public MsgForAPI(String userID, int queryID, String resultXML)
	{
		super();
		this.userID = userID;
		this.queryID = queryID;
		this.resultXML = resultXML;
	}

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	
	public int getQueryID()
	{
		return queryID;
	}

	public void setQueryID(int queryID)
	{
		this.queryID = queryID;
	}

	public String getResultXML()
	{
		return resultXML;
	}

	public void setResultXML(String resultXML)
	{
		this.resultXML = resultXML;
	}
}
