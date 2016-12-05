package buffer.dao;

import java.sql.SQLException;
import java.util.List;

import buffer.info.UserInfo;

public class UserInfoDAO extends AIRDAO{
	public UserInfoDAO() {
		super();
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object update(UserInfo parameter) throws SQLException
	{
		return sqlMap.update("tb_user.update", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insert(UserInfo parameter) throws SQLException
	{
		return sqlMap.insert("tb_user.insert", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object select(UserInfo parameter) throws SQLException
	{
		return sqlMap.queryForObject("tb_user.select", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectList(UserInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_user.select", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int delete(UserInfo parameter) throws SQLException
	{
		return sqlMap.delete("tb_user.delete", parameter);
	}

}
