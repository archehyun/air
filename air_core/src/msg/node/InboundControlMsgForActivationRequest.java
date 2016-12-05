package msg.node;


/**
 * 서비스 시작을 위해 태그 활성화를 요청하는 InboundControlMsg 패킷 노트
 * 
 * @author		손희목
 * @since       2014-02-04
 * @version     0.1       
 */
public class InboundControlMsgForActivationRequest extends InboundMsg
{
	
	public InboundControlMsgForActivationRequest(String tid, String cid, String tagIPaddr) {
		super(tid, cid, tagIPaddr);
		
	}	

}
