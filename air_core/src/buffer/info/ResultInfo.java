package buffer.info;

import java.util.Date;


public class ResultInfo {
	private String tb_tid;
	
	private String tid;
	private double lat;
	private double lng;
	private short temperature;
	private int door;
	public int getDoor() {
		return door;
	}
	public void setDoor(int door) {
		this.door = door;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public short getTemperature() {
		return temperature;
	}
	public void setTemperature(short temperature) {
		this.temperature = temperature;
	}
	public short getHumidity() {
		return humidity;
	}
	public void setHumidity(short humidity) {
		this.humidity = humidity;
	}
	private short humidity=0;
	private int tb_id;
	public int getTb_id() {
		return tb_id;
	}
	public void setTb_id(int tb_id) {
		this.tb_id = tb_id;
	}
	public String getTb_tid() {
		return tb_tid;
	}
	public void setTb_tid(String tb_tid) {
		this.tb_tid = tb_tid;
	}
	private Date tb_time;
	private String tb_result;

	private String hit;

	public String getHit() {
		return hit;
	}
	public Date getTb_time() {
		return tb_time;
	}
	public void setTb_time(Date tb_time) {
		this.tb_time = tb_time;
	}
	public String getTb_result() {
		return tb_result;
	}
	public void setTb_result(String tb_result) {
		this.tb_result = tb_result;
	}
	public void setHit(String hit) {
		this.hit = hit;
		
	}

}
