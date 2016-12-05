package msg.node;


/**
 * 태그에서 보내오는 Packet Node의 슈퍼클래스
 * 
 * @author		박창현
 * @since       2014-02-07
 * @version     0.1      
 */
public abstract class InboundMsg extends QueueNode implements OutboundMessage
{
	protected String tid; //태그 ID
	
	protected String cid="c1"; //컨테이너 ID
	
	protected String tagIPaddr; //태그 IP주소
	
	public InboundMsg(String tid, String cid) {
		super();
		this.tid = tid;
		this.cid = cid;
	}
	public InboundMsg(String tid, String cid, String tagIPaddr) {
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
	
	
	public void setTagIPaddr(String tagIPaddr) {
		this.tagIPaddr = tagIPaddr;
	}

	public String getTagIPaddr() {
		return tagIPaddr;
	}
}
