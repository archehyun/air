package msg.node;

/**
 * Spatial Event Generator에 의한 태그의 데이터 전송 조건을 명시한 Tag Control Message
 * 
 * @author		박병권
 * @since       2014-01-25
 * @version     0.1       
 */
public class TagControlMsgForDistanceConditionChange extends TagControlMsg
{
	private int movingDistance; //태그가 이 거리(meter)만큼 이동하면 데이터 전송 요청
	
	public TagControlMsgForDistanceConditionChange(String tid, String cid, int movingDistance)
	{
		super(tid, cid, null);
		this.movingDistance = movingDistance;
	}

	public int getMovingDistance()
	{
		return movingDistance;
	}

	public void setMovingDistance(int movingDistance)
	{
		this.movingDistance = movingDistance;
	}
}
