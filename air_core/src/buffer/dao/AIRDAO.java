package buffer.dao;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;

public class AIRDAO {
	protected Logger logger = Logger.getLogger(getClass());
	protected SqlMapClient sqlMap;
	public AIRDAO() {
		try {
			
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
