package msg.node;

/**
 * 사용자 질의에 기반하여 태그의 데이터 전송 조건을 명시한 Tag Control Message
 * 
 * @author		손희목
 * @since       2014-02-06
 * @version     0.1      
 */
public class TagControlMsgForQueryConditionChange extends TagControlMsg
{
	public static byte DOOR_OPEN=0x01;
	public static byte DOOR_CLOSE=0x00;
	public static byte DOOR_NONE=0x02;
	
	private short lowerBoundTemperature; //정상범위 온도 하한값(섭씨), 이 값보다 내려가면 알려달라
	private short upperBoundTemperature; //정상범위 온도 상한값(섭씨), 이 값보다 올라가면 알려달라
	private short lowerBoundHumidity; //정상범위 습도 하한값(%), 이 값보다 내려가면 알려달라
	private short upperBoundHumidity; //정상범위 습도 상한값(%), 이 값보다 올라가면 알려달라
	private short upperBoundHit; //정상범위 충격 상한값(dB), 이 값보다 크면 알려달라
	private byte door; // 닫힌 상태(0x00), 열린 상태(0x01), 확인 불가(0x02)
	private int noticeInterval; //태그 통신 주기(분)
	private int sensingInterval; //태그 센싱 주기(분)
	private boolean add; // true: ADD, false: REMOVE
	
	
	/**@see 박창현 추가
	 * @param tid
	 * @param cid
	 */
	public TagControlMsgForQueryConditionChange(String tid, String cid)
	{
		super(tid,cid,null);
		
	}
	
	public TagControlMsgForQueryConditionChange(String tid, String cid, String ip)
	{
		super(tid,cid,ip);
		
	}
	public TagControlMsgForQueryConditionChange(String tid, String cid, short lowerBoundTemperature, short upperBoundTemperature,
										short lowerBoundHumidity, short upperBoundHumidity, short upperBoundHit, 
										byte door, int noticeInterval, int sensingInterval, boolean add) {
		super(tid, cid, null);
		this.lowerBoundTemperature = lowerBoundTemperature;
		this.upperBoundTemperature = upperBoundTemperature;
		this.lowerBoundHumidity = lowerBoundHumidity;
		this.upperBoundHumidity = upperBoundHumidity;
		this.upperBoundHit = upperBoundHit;
		this.door = door;
		this.noticeInterval = noticeInterval;
		this.sensingInterval = sensingInterval;
		this.add = add;
	}

	public short getLowerBoundTemperature() {
		return lowerBoundTemperature;
	}

	public void setLowerBoundTemperature(short lowerBoundTemperature) {
		this.lowerBoundTemperature = lowerBoundTemperature;
	}

	public short getUpperBoundTemperature() {
		return upperBoundTemperature;
	}

	public void setUpperBoundTemperature(short upperBoundTemperature) {
		this.upperBoundTemperature = upperBoundTemperature;
	}

	public short getLowerBoundHumidity() {
		return lowerBoundHumidity;
	}

	public void setLowerBoundHumidity(short lowerBoundHumidity) {
		this.lowerBoundHumidity = lowerBoundHumidity;
	}

	public short getUpperBoundHumidity() {
		return upperBoundHumidity;
	}

	public void setUpperBoundHumidity(short upperBoundHumidity) {
		this.upperBoundHumidity = upperBoundHumidity;
	}

	public short getUpperBoundHit() {
		return upperBoundHit;
	}

	public void setUpperBoundHit(short upperBoundHit) {
		this.upperBoundHit = upperBoundHit;
	}

	public byte getDoor() {
		return door;
	}

	public void setDoor(byte door) {
		this.door = door;
	}

	public int getNoticeInterval() {
		return noticeInterval;
	}

	public void setNoticeInterval(int noticeInterval) {
		this.noticeInterval = noticeInterval;
	}

	public int getSensingInterval() {
		return sensingInterval;
	}

	public void setSensingInterval(int sensingInterval) {
		this.sensingInterval = sensingInterval;
	}
	
	public boolean isAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}
}
