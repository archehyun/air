package info;




public class ActionInfo extends AIRTable{
	private String query_number;
	private String date;
	private String tid;
	private String cid;
	private String location;
	private String temperature;
	private String humidity;
	private String hit;
	private String user_id;
	public ActionInfo() {
	}
	public String getQuery_number() {
		return query_number;
	}
	public void setQuery_number(String query_number) {
		this.query_number = query_number;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getHit() {
		return hit;
	}
	public void setHit(String hit) {
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
