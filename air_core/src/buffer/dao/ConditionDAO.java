package buffer.dao;

import java.sql.SQLException;
import java.util.List;

import buffer.info.ActionInfo;
import buffer.info.ConditionInfo;

public class ConditionDAO extends AIRDAO{
	public ConditionDAO() {
		super();
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object update(ActionInfo parameter) throws SQLException
	{
		return sqlMap.update("tb_action.update", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insert(ActionInfo parameter) throws SQLException
	{
		return sqlMap.insert("tb_action.insert", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int delete(ActionInfo parameter) throws SQLException
	{
		return sqlMap.delete("tb_action.delete", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<ActionInfo> selectList(ActionInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_action.select", parameter);
	}
	@SuppressWarnings("unchecked")
	public List<ActionInfo> selectList() throws SQLException
	{
		return sqlMap.queryForList("tb_action.select");
	}

	public List selectQueryIDList(ActionInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_action.selectQueryIDList", parameter);
	}
	public ActionInfo select(ActionInfo parameter) throws SQLException
	{
		return (ActionInfo) sqlMap.queryForObject("tb_action.select", parameter);
	}
	public int getMaxQueryID() throws SQLException
	{
		return (int) sqlMap.queryForObject("tb_action.selectMaxQueryID");
	}
	
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public Object insert(ConditionInfo parameter) throws SQLException
	{
		String namespace=selectConditionTableNamespace(parameter.getTableType());
		return sqlMap.insert(namespace+".insert", parameter);
	}
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public int delete(ConditionInfo parameter) throws SQLException
	{
		String namespace=selectConditionTableNamespace(parameter.getTableType());
		return sqlMap.delete(namespace+".delete", parameter);
	}	
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public Object update(ConditionInfo parameter) throws SQLException
	{
		String namespace=selectConditionTableNamespace(parameter.getTableType());
		return sqlMap.update(namespace+".update", parameter);
	}
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public Object select(ConditionInfo parameter) throws SQLException
	{
		String namespace=selectConditionTableNamespace(parameter.getTableType());
		return sqlMap.queryForObject(namespace+".select", parameter);
	}
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public List selectList(ConditionInfo parameter) throws SQLException
	{
		String namespace=selectConditionTableNamespace(parameter.getTableType());
		return sqlMap.queryForList(namespace+".select", parameter);
	}	
	
	public List selectListByTagID(ConditionInfo parameter) throws SQLException
	{
		String namespace=selectConditionTableNamespace(parameter.getTableType());
		return sqlMap.queryForList(namespace+".selectListByTagID", parameter);
	}
	
	/**
	 * @param condition_type
	 * @return
	 */
	private String selectConditionTableNamespace(int condition_type)
	{
		String namespace=null;
		switch (condition_type) {
		case ConditionInfo.CONTAINER:
			namespace ="tb_condition_container";
			break;
		case ConditionInfo.TEMPERATURE:
			namespace ="tb_condition_temperature";
			break;
		case ConditionInfo.HUMIDITY:
			namespace ="tb_condition_humidity";
			break;
		case ConditionInfo.DATE:
			namespace ="tb_condition_date";
			break;	
		case ConditionInfo.TIME:
			namespace ="tb_condition_time";
			break;
		case ConditionInfo.HIT:
			namespace ="tb_condition_hit";
			break;
		case ConditionInfo.ACTION:
			namespace ="tb_action";
			break;	
		default:
			break;
		}
		return namespace;

	}

}
