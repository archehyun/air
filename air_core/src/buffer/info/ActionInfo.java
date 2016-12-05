package buffer.info;


/**
 * @author archehyun
 *
 */
public class ActionInfo extends AIRTable{
	public static final int TRUE=1;
	public static final int FALSE=0;
	private String query_number;//
	private int date=0;//
	private int tid=0;// 태그 아이디
	private int cid=0;// 컨테이너 아이디
	private int location=0;// 위치
	private int temperature=0;// 온도
	private int humidity=0;//습도
	private int hit=0;//충격
	private String user_id;// 사용자 아이디
	public ActionInfo() {
	}
	public String getQuery_number() {
		return query_number;
	}
	public void setQuery_number(String query_number) {
		this.query_number = query_number;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	public int getHit() {
		return hit;
	}
	public void setHit(int hit) {
		this.hit = hit;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String toString()
	{
		return query_number;
	}
	


}
