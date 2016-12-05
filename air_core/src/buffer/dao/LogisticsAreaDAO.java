package buffer.dao;

import java.sql.SQLException;
import java.util.List;

import lib.rtree.MBR;
import lib.rtree.Rtree;
import spatial.LogisticsArea;
import buffer.info.LocationInfo;

public class LogisticsAreaDAO extends AIRDAO{
	public LogisticsAreaDAO() {
		super();
	}


	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object update(LocationInfo parameter) throws SQLException
	{
		return sqlMap.update("tb_location.update", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object select(LocationInfo parameter) throws SQLException
	{
		return sqlMap.queryForObject("tb_location.select", parameter);
	}
	
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List<LocationInfo> selectListGate(LocationInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_location.selectListGate", parameter);
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List<LocationInfo> selectList(LocationInfo parameter) throws SQLException
	{

		return sqlMap.queryForList("tb_location.select", parameter);
	}

	public Object insert(LocationInfo parameter) throws SQLException,NullPointerException
	{	

		Rtree rtree = Rtree.getInstance();		
		MBR newMBR = new MBR(parameter.getX1(), parameter.getY1(), parameter.getX3(), parameter.getY3());
		logger.debug("insert tree:"+newMBR);

		//rtree.insert(newMBR, Long.parseLong(parameter.getLocation_code()));

		return sqlMap.insert("tb_location.insert", parameter);
	}
	public int delete(LocationInfo parameter) throws SQLException
	{
		return sqlMap.delete("tb_location.delete", parameter);
	}


	public LogisticsArea selectLogistic(LogisticsArea parameter) throws SQLException {
		
		return (LogisticsArea) sqlMap.queryForObject("tb_location.selectLogistic", parameter);
	}


	public LocationInfo selectGateInfo(LocationInfo parameter) throws SQLException {
		// TODO Auto-generated method stub
		return (LocationInfo) sqlMap.queryForObject("tb_location.selectGate", parameter);
	}

}
