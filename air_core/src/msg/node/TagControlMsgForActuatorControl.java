package msg.node;

/**
 * Query Processor에 의하여 태그의 actuator control을 명시한 Tag Control Message
 * 
 * @author		박병권
 * @since       2014-01-25
 * @version     0.1       
 */
public class TagControlMsgForActuatorControl extends TagControlMsg
{
	private short switchNo; //태그에 연결된 actuator의 스위치 번호
	private boolean on; // true: 스위치 ON, false: 스위치 OFF
	
	public TagControlMsgForActuatorControl(String tid, String cid, short switchNo, boolean on)
	{
		super(tid, cid, null);
		this.switchNo = switchNo;
		this.on = on;
	}

	public short getSwitchNo()
	{
		return switchNo;
	}

	public void setSwitchNo(short switchNo)
	{
		this.switchNo = switchNo;
	}

	public boolean isOn()
	{
		return on;
	}

	public void setOn(boolean on)
	{
		this.on = on;
	}
}
