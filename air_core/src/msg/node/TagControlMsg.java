package msg.node;


/**
 * 태그로 보내는 Control Packet을 저장관리하는 Tag Control Message Queue의 노드 
 * 
 * @author		손희목
 * @since       2014-02-06
 * @version     0.1      
 */
public class TagControlMsg extends QueueNode implements OutboundMessage
{
	private String tid; //태그 ID
	private String cid; //컨테이너 ID
	private String tagIPaddr; //태그 IP주소
	
	public TagControlMsg(String tid, String cid, String tagIPaddr) {
		super();
		this.tid = tid;
		this.cid = cid;
		this.tagIPaddr = tagIPaddr;
	}
	
	public String getTid() {
		return tid;
	}
	
	public void setTid(String tid) {
		this.tid = tid;
	}
	
	public String getCid() {
		return cid;
	}
	
	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getTagIPaddr() {
		return tagIPaddr;
	}

	public void setTagIPaddr(String tagIPaddr) {
		this.tagIPaddr = tagIPaddr;
	}
}
