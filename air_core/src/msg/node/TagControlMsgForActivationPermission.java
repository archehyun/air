package msg.node;

/**
 * 태그의 activation request에 대한 permission을 명시한 Tag Control Message
 * 
 * @author		손희목
 * @since       2014-02-06
 * @version     0.1      
 */
public class TagControlMsgForActivationPermission extends TagControlMsg
{	
	private int heartbeatPeriod; // 태그 동작 유무 주기

	public TagControlMsgForActivationPermission(String tid, String cid, int heartbeatPeriod) {
		super(tid, cid, null);
		this.heartbeatPeriod = heartbeatPeriod;
	}

	public int getheartbeatPeriod() {
		return heartbeatPeriod;
	}

	public void setheartbeatPeriod(int heartbeatPeriod) {
		this.heartbeatPeriod = heartbeatPeriod;
	}
}
