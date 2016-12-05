package info;




public class ConditionInfo extends AIRTable {
	
	

	public static final int CONTAINER=0;
	public static final int TEMPERATURE=1;
	public static final int HUMIDITY=2;
	public static final int HIT=3;
	public static final int DATE=4;
	public static final int TIME=5;
	public static final int ACTION=6;
	
	
	private String cid;
	private String conditionType;
	
	private String date;
	private String hit;

	private String isRelative;
	private String location;
	private int max;
	private int min;
	private String query_number;
	private String temperature;
	private String tid;
	private String user_id;
	private String[] qidList;
	public String[] getQidList() {
		return qidList;
	}
	public void setQidList(String[] qidList) {
		this.qidList = qidList;
	}
	public ConditionInfo() {
	}
	public ConditionInfo(int tableType) {
		
		super(tableType);
		
	}
	
	public ConditionInfo(String conditionType) {
		this.conditionType = conditionType;
	}
	public String getCid() {
		return cid;
	}
	public String getConditionType() {
		return conditionType;
	}
	public String getDate() {
		return date;
	}
	public String getHit() {
		return hit;
	}
	public String getIsRelative() {
		return isRelative;
	}
	public String getLocation() {
		return location;
	}
	public int getMax() {
		return max;
	}


	public int getMin() {
		return min;
	}
	public String getOper() {
		return null;
	}
	public String getQuery_number() {
		return query_number;
	}
	public String getTemperature() {
		return temperature;
	}
	public String getTid() {
		return tid;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setHit(String hit) {
		this.hit = hit;
	}
	public void setIsRelative(String isRelative) {
		this.isRelative = isRelative;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public void setQuery_number(String query_number) {
		this.query_number = query_number;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String toString()
	{
		return this.getQuery_number();
	}
	

}
