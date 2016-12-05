package buffer.info;

import java.util.HashMap;

/**
 * Working Memory를 관리하는 DB 테이블 객체
 * 
 * @author		박병권
 * @since       2014-02-03
 * @version     0.1       
 */
public class WorkingMemoryTable 
{
	private static WorkingMemoryTable wm;
	
	private static HashMap<String, Event> workMemoryList = new HashMap<String, WorkingMemoryTable.Event>();
	
	static
	{
		wm = new WorkingMemoryTable();
	}
	float latitude;
	float longitude;
	int currentEvent;
	/*
	 * Working Memory Table 인스턴스를 획득할 수 있는 클래스 메서드
	 */
	public static WorkingMemoryTable getInstance()
	{
		return wm;
	}
	
	public WorkingMemoryTable()
	{
	}
	
	public int getRecentEvent(String tid, String cid) throws NullPointerException
	{
		//int result = 0;
		
		return workMemoryList.get(tid).getEvent();
	}
	
	public String getRecentLogisticsAreaID(String tid, String cid)
	{
		long areaID = 1;
		
		return workMemoryList.get(tid).getAreaID();
	}
	
	public float getRecentLatitude(String tid, String cid)
	{
		//float latitude = 0;
		
		return workMemoryList.get(tid).getLatitude();
	}
	
	public float getRecentLongitude(String tid, String cid)
	{
		//float longitude = 0;
		
		return workMemoryList.get(tid).getLongitude();
	}
	
	public boolean updateRecentEvent(String tid, String cid, float latitude, float longitude, String areaID, int event)
	{
		boolean result = true;
		currentEvent =event;
		this.latitude =latitude;
		this.longitude = longitude;
		Event events = new Event();
		events.setTid(tid);
		events.setCid(cid);
		events.setLatitude(latitude);
		events.setLongitude(longitude);
		events.setAreaID(areaID);
		events.setEvent(event);
		workMemoryList.put(tid, events);
		return result;
	}
	class Event
	{
		private  String tid;
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
		public float getLatitude() {
			return latitude;
		}
		public void setLatitude(float latitude) {
			this.latitude = latitude;
		}
		public float getLongitude() {
			return longitude;
		}
		public void setLongitude(float longitude) {
			this.longitude = longitude;
		}
		public String getAreaID() {
			return areaID;
		}
		public void setAreaID(String areaID) {
			this.areaID = areaID;
		}
		public int getEvent() {
			return event;
		}
		public void setEvent(int event) {
			this.event = event;
		}
		private String cid;
		private float latitude;
		private float longitude;
		private String areaID;
		private int event;
		
		
	}
}
