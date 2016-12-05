package buffer.info;

/**
 * 태그-유저 맵핑 정보
 * @author archehyun
 *
 */
public class TagUserInfo extends TagInfo{
	/**
	 * 
	 */
	private String user_id;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String toString()
	{
		return user_id+","+this.getTid();
	}

}
