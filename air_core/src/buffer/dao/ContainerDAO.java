package buffer.dao;

import java.sql.SQLException;
import java.util.List;

import buffer.info.ContainerInfo;

public class ContainerDAO extends AIRDAO{
	public ContainerDAO() {
		super();
	}
	public Object insert(ContainerInfo parameter) throws SQLException {
		return sqlMap.insert("tb_container.insert", parameter);
	}
	public int delete(ContainerInfo parameter) throws SQLException
	{
		return sqlMap.delete("tb_container.delete",parameter);
	}
	public void selectInfo()
	{
		
	}

	public List selectList(ContainerInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_container.select", parameter);
	}
	
	

}
