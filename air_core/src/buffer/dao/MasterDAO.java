package buffer.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MasterDAO {
	SqlMapClient sqlMap;
	public MasterDAO() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List showTables() throws SQLException
	{
		return sqlMap.queryForList("MASTER.showTables");
	}
	
}
