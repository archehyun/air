package msg.node;


/**
 * 태그의 동작 유무를 전송하는 InboundControlMsg 패킷
 * 
 * @author		손희목
 * @since       2014-02-04
 * @version     0.1       
 */
public class InboundControlMsgForHeartbeat extends InboundMsg
{
	private float battery; // 태그 배터리 잔량 (Voltage)

	public InboundControlMsgForHeartbeat(String tid, String cid, float battery) {
		super(tid, cid);
		this.battery = battery;
	}

	public float getBattery() {
		return battery;
	}

	public void setBattery(float battery) {
		this.battery = battery;
	}
}
