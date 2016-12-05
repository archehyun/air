package server;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * @author archehyun
 *
 */
public abstract class AIRThread {
	protected boolean isStarted=false;
	protected Thread thread;
	Vector<Monitor> monitorList;
	double time;
	MonitorMessage monitorMessage;
	public MonitorMessage getMonitorMessage() {
		return monitorMessage;
	}

	public void setMonitorMessage(MonitorMessage monitorMessage) {
		this.monitorMessage = monitorMessage;
	}
	public abstract void serverStart();
	public abstract void serverStop();
	

	public double getProcessTime() {
		return time;
	}

	public void setProcessTime(double time) {
		this.time = time;
	}
	protected Logger 			logger = Logger.getLogger(getClass());
	
	public AIRThread() {
		monitorList = new Vector<Monitor>();
	}
	private String message;
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void addMonitor(Monitor monitor)
	{
		monitorList.add(monitor);
		
	}
	public void notifyMonitor() 
	{
		Iterator<Monitor> iter = monitorList.iterator();
		while(iter.hasNext())
		{
			Monitor item = iter.next();
			
			item.update(this);
		}
	}
	public void notifyMonitor(String message) 
	{
		this.setMessage(message);
		Iterator<Monitor> iter = monitorList.iterator();
		while(iter.hasNext())
		{
			Monitor item = iter.next();			
			item.update(this);
		}
	}
	public void notifyMonitors(MonitorMessage message) 
	{
		
		this.setMonitorMessage(message);
		Iterator<Monitor> iter = monitorList.iterator();
		while(iter.hasNext())
		{
			Monitor item = iter.next();
			item.update(this);
		}
	}
	
	

}
