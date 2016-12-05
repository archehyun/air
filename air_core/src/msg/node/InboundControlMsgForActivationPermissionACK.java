package msg.node;

/**
 * 태그의 activation request에 대한 permission을 명시한 Tag Control Message
 * 
 * @author		손희목
 * @since       2014-02-06
 * @version     0.1      
 */
public class InboundControlMsgForActivationPermissionACK extends InboundMsg
{	

	public InboundControlMsgForActivationPermissionACK(String tid, String cid) {
		super(tid, cid);
	}
}
