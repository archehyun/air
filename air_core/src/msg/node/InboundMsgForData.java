package msg.node;


/**
 * 태그로부터 받은 Data Packet을 저장관리하는 Inbound Data Message Queue의 노드
 * 
 * @author		박병권
 * @since       2014-01-25
 * @version     0.1       
 */
public class InboundMsgForData extends InboundMsg
{
	private short year; //태그 데이터 수신 년
	
	private byte month; //태그 데이터 수신 월
	
	private byte day; //태그 데이터 수신 일
	
	private byte hour; //태그 데이터 수신 시
	
	private byte minute; //태그 데이터 수신 분
	
	private double latitude; //태그의 현재 위도
	
	private double longitude; //태그의 현재 경도
	
	private short temperature; //태그의 현재 온도(섭도)
	
	private short humidity; //태그의 현재 습도(%)
	
	private String hitX; //태그가 받은 X축 충격치(dB)
	
	private String hitY; //태그가 받은 Y축 충격치(dB)
	
	private String hitZ; //태그가 받은 Z축 충격치(dB)
	
	private byte door; //닫힌 상태(0x00), 열린 상태(0x01), 확인 불가(0x02)

	private long currentTime;
	
	public InboundMsgForData(String tid, String cid)
	{
		super(tid, cid);
	}
	public InboundMsgForData(String tid, String cid,String tagIPaddr)
	{
		super(tid, cid,tagIPaddr);
	}

	public InboundMsgForData(String tid, String cid, short year, byte month, byte day, byte hour, byte minute, double latitude, double longitude,
							short temperature, short humidity, String hitX, String hitY, String hitZ, byte door) {
		super(tid, cid);
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.latitude = latitude;
		this.longitude = longitude;
		this.temperature = temperature;
		this.humidity = humidity;
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
		this.door = door;
	}

	public short getYear() {
		return year;
	}

	public void setYear(short year) {
		this.year = year;
	}

	public byte getMonth() {
		return month;
	}

	public void setMonth(byte month) {
		this.month = month;
	}

	public byte getDay() {
		return day;
	}

	public void setDay(byte day) {
		this.day = day;
	}

	public byte getHour() {
		return hour;
	}

	public void setHour(byte hour) {
		this.hour = hour;
	}

	public byte getMinute() {
		return minute;
	}

	public void setMinute(byte minute) {
		this.minute = minute;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public short getTemperature() {
		return temperature;
	}

	public void setTemporature(short temperature) {
		this.temperature = temperature;
	}

	public short getHumidity() {
		return humidity;
	}

	public void setHumidity(short humidity) {
		this.humidity = humidity;
	}

	public String getHitX() {
		return hitX;
	}

	public void setHitX(String hitX) {
		this.hitX = hitX;
	}

	public String getHitY() {
		return hitY;
	}

	public void setHitY(String hitY) {
		this.hitY = hitY;
	}

	public String getHitZ() {
		return hitZ;
	}

	public void setHitZ(String hitZ) {
		this.hitZ = hitZ;
	}

	public byte getDoor() {
		return door;
	}

	public void setDoor(byte door) {
		this.door = door;
	}
	
	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTimeMillis) {
		this.currentTime = currentTimeMillis;
		
	}

}
