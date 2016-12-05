package buffer.info;

import java.util.Date;

public class SpatialEventInfo {
	
	private int event_number;
	public int getEvent_number() {
		return event_number;
	}
	public void setEvent_number(int event_number) {
		this.event_number = event_number;
	}
	public int getEvent_type() {
		return event_type;
	}
	public void setEvent_type(int event_type) {
		this.event_type = event_type;
	}
	public Date getEvent_time() {
		return event_time;
	}
	public void setEvent_time(Date event_time) {
		this.event_time = event_time;
	}
	public String getLocation_code() {
		return location_code;
	}
	public void setLocation_code(String location_code) {
		this.location_code = location_code;
	}
	private String tid;
	private String cid;
	private int event_type;
	private Date event_time;
	private String location_code;
	
	private short year;
	private byte month; 
	private byte day; 
	private byte hour; 
	private byte minute;
	private long areaID;
	private int event;
	
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
	public long getAreaID() {
		return areaID;
	}
	public void setAreaID(long areaID) {
		this.areaID = areaID;
	}
	public int getEvent() {
		return event;
	}
	public void setEvent(int event) {
		this.event = event;
	}

}
