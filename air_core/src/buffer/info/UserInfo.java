package buffer.info;


/**����� ����
 * @author archehyun
 *
 */
public class UserInfo extends AIRTable{
	
	
	
	private String user_id;		// ����� ���̵�
	private String user_pw;		// ����� �н�����
	private String user_name;	// ����� �̸�
	private String position;	// ����� ����
	private String company;		// ȸ���
	
	public String getUser_pw() {
		return user_pw;
	}
	public void setUser_pw(String user_pw) {
		this.user_pw = user_pw;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}


}
