package buffer.info;

import java.util.Date;




/**�±� ���� ����
 * @author ��â��
 *
 */
public class TagInfo extends AIRTable{
	
	
	private int battery;		// ���͸� �ܷ�
	private String is_activate;	// ��Ƽ���̼� ����
	private int tag_interval; 	// ���� �ֱ�
	private String tid;			// �±� ���̵�
	private Date update_time;   // ���� �ð�
	public int getBattery() {
		return battery;
	}

	public void setBattery(int battery) {
		this.battery = battery;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public int getTime_interval() {
		return time_interval;
	}

	public void setTime_interval(int time_interval) {
		this.time_interval = time_interval;
	}
	private int time_interval;
	
	public String getIs_activate() {
		return is_activate;
	}
	
	public int getTag_interval() {
		return tag_interval;
	}
	public String getTid() {
		return tid;
	}

	public void setIs_activate(String is_activate) {
		this.is_activate = is_activate;
	}
	public void setTag_interval(int tag_interval) {
		this.tag_interval = tag_interval;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String toString()
	{
		return "["+this.getTid()+","+this.getIs_activate()+"]";
	}
	String user_id;
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_id() {
		// TODO Auto-generated method stub
		return user_id;
	}
	


}
