package server;

import java.util.ArrayList;
import java.util.HashMap;

import query.QueryStatics;
import query.manager.TagThread;

public class MonitorMessage {
	
	
	
	private String user_id;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	private String ip;
	private String message;
	private int humidity;
	private QueryStatics statics;
	private byte door;
	
	public byte getDoor() {
		return door;
	}
	public void setDoor(byte door) {
		this.door = door;
	}
	private HashMap<String, TagThread> tagList;
	
	public HashMap<String, TagThread> getTagList() {
		return tagList;
	}
	public void setTagList(HashMap<String, TagThread> tagList) {
		this.tagList = tagList;
	}
	public QueryStatics getStatics() {
		return statics;
	}
	public void setStatics(QueryStatics statics) {
		this.statics = statics;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	private int temperature;
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	private String tid;
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	
	private double lat;
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
	private double lng;
	String actionResult;
	double avgProcessTime=0;
	double avgQueueWaitTime=0;
	double maxProcessTime=0;
	double maxQueueWaitTime=0;
	double minProcessTime=0;
	double minQueueWaitTime=0;
	int processCount;
	public void setProcessCount(int processCount) {
		this.processCount = processCount;
	}
	public int getProcessCount()
	{
		return processCount;
	}
	int queueSize;
	public String getActionResult() {
		return actionResult;
	}
	public double getAvgProcessTime() {
		return avgProcessTime;
	}
	
	public double getAvgQueueWaitTime()
	{
		return avgQueueWaitTime;
	}
	public double getMaxProcessTime() {
		return maxProcessTime;
	}
	public double getMaxQueueWaitTime() {
		return maxQueueWaitTime;
	}
	public double getMinProcessTime() {
		return minProcessTime;
	}
	public double getMinQueueWaitTime() {
		return minQueueWaitTime;
	}
	public int getQueueSize()
	{
		return queueSize;
	}
	public void setActionResult(String actionResult) {
		this.actionResult = actionResult;
	}
	public void setAvgProcessTime(double avgProcessTime) {
		this.avgProcessTime = avgProcessTime;
	}
	public void setAvgQueueWaitTime(double avgQueueWaitTime) {
		this.avgQueueWaitTime = avgQueueWaitTime;
	}
	public void setMaxProcessTime(double maxProcessTime) {
		this.maxProcessTime = maxProcessTime;
	}
	public void setMaxQueueWaitTime(double maxQueueWaitTime) {
		this.maxQueueWaitTime = maxQueueWaitTime;
	}
	public void setMinProcessTime(double minProcessTime) {
		this.minProcessTime = minProcessTime;
	}
	public void setMinQueueWaitTime(double minQueueWaitTime) {
		this.minQueueWaitTime = minQueueWaitTime;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
	int threadCount;
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
		
	}
	public int getThreadCount()
	{
		return threadCount;
	}
	ArrayList<QueryStatics> tagListInfo;
	public ArrayList<QueryStatics> getTagListInfo() {
		return tagListInfo;
	}
	public void setTagListInfo(ArrayList<QueryStatics> tagListInfo) {
		this.tagListInfo = tagListInfo;
		
	}
	

	

}
