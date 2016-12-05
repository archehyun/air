package msg.node;


/**
 * 태그의 변경된 IP 정보를 전송하는 InboundControlMsg 패킷 노드
 * 
 * @author		손희목
 * @since       2014-02-04
 * @version     0.1       
 */
public class InboundMsgForIPChange extends InboundMsgForData
{
	public InboundMsgForIPChange(String tid, String cid, String tagIPaddr, 
			float latitude, double longitude, short temperature, short humidity, 
			String hitX, String hitY, String hitZ, byte door) {
		super(tid, cid,tagIPaddr);
		
		this.setLatitude(latitude);
		
		this.setLongitude((float) longitude);
		
		this.setTemporature(temperature);
		
		this.setHumidity(humidity);
		
		this.setHitX(hitX);
		
		this.setHitY(hitY);
		
		this.setHitZ(hitZ);
		
		this.setDoor(door);

	}
}
