package buffer.dao;

import java.sql.SQLException;
import java.util.List;

import buffer.info.TagInfo;
import buffer.info.TagUserInfo;

public class TagDAO extends AIRDAO{
	public TagDAO() {
		super();
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object update(TagInfo parameter) throws SQLException
	{
		return sqlMap.update("tb_tag.update", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object updateTagUser(TagUserInfo parameter) throws SQLException
	{
		return sqlMap.update("tb_tag_user.update", parameter);
	}	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object select(TagInfo parameter) throws SQLException
	{
		return sqlMap.queryForObject("tb_tag.select", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object selectTagUser(TagUserInfo parameter) throws SQLException
	{
		return sqlMap.queryForObject("tb_tag_user.select", parameter);
	}	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectList(TagInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_tag.select", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectTagUserList(TagUserInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_tag_user.select", parameter);
	}
	
	public List selectList(String tid) throws SQLException
	{
		return sqlMap.queryForList("tb_tag.selectByID", tid);
	}
	public List selectTagUserList(String tid) throws SQLException
	{
		return sqlMap.queryForList("tb_tag_user.selectByID", tid);
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectTagListGroupBy() throws SQLException
	{
		return sqlMap.queryForList("tb_tag.selectGroupBy");
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectTagUserListGroupBy() throws SQLException
	{
		return sqlMap.queryForList("tb_tag_user.selectGroupBy");
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insert(TagInfo parameter) throws SQLException
	{
		return sqlMap.insert("tb_tag.insert", parameter);
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insertTagUser(TagUserInfo parameter) throws SQLException
	{
		return sqlMap.insert("tb_tag_user.insert", parameter);
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int delete(TagInfo parameter) throws SQLException
	{
		return sqlMap.delete("tb_tag.delete", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int deleteTagUser(TagUserInfo parameter) throws SQLException
	{
		return sqlMap.delete("tb_tag_user.delete", parameter);
	}

}
