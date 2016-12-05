package api;

public class QueryEntity {
	private int humidity;
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	private int temperature;
	private double latitude;
	private double longitude;
	private int door;
	public int getDoor() {
		return door;
	}
	public void setDoor(int door) {
		this.door = door;
	}
	public QueryEntity(String qid, String cid, String tid, String user,
			int temperature,int humidity,  double latitude, double longitude) {
		this.qid =qid;
		this.tid = tid;
		this.user = user;
		this.temperature=temperature;
		this.humidity = humidity;
		this.latitude = latitude;
		this.longitude= longitude;
	}
	public QueryEntity(String qid, String cid, String tid, String user,
			int temperature,int humidity,  double latitude, double longitude, int door) {
		this(qid, cid, tid, user, temperature, humidity, latitude, longitude);
		this.door=door;
	}
	private String qid;
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	private String tid;
	private String user;
	

}
