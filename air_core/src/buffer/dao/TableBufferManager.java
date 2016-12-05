package buffer.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import lib.rtree.RtreeIndexNode;

import org.apache.log4j.Logger;

import spatial.LogisticsArea;
import buffer.info.AIRTable;
import buffer.info.ActionInfo;
import buffer.info.ConditionInfo;
import buffer.info.ContainerInfo;
import buffer.info.LocationInfo;
import buffer.info.ResultInfo;
import buffer.info.SpatialEventInfo;
import buffer.info.TagInfo;
import buffer.info.TagUserInfo;
import buffer.info.UserInfo;

import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * 서비스플랫폼 DB에 저장된 모든 테이블의 메모리 버퍼를 관리하는 클래스
 * 
 * @author		박창현
 * @since       1.0
 *        
 */

public class TableBufferManager {

	/**
	 * 
	 */
	private static TableBufferManager bufferManager; // 버퍼매니저 인스턴스
	
	/**
	 * 
	 */
	private static HashMap<String, List> workingMemory;

	public static TableBufferManager getInstance()
	{		
		if(bufferManager==null)
			bufferManager = new TableBufferManager();
		return bufferManager;
	}
	private ConditionDAO conditionDAO;
	
	protected Logger	logger = Logger.getLogger(getClass()); // 로그 생성 객체
	
	/**
	 * 
	 */
	private LogisticsAreaDAO logisticsAreaDao;
	
	/**
	 * 
	 */
	private MasterDAO masterDAO;// 태이블 정보 접근 객체 

	/**
	 * 
	 */
	private HashMap<String, List>masterTable;
	
	/**
	 * sql 저장 객체
	 */
	private SqlMapClient sqlMap;
	
	/**
	 * 태그 정보 관리
	 */
	private TagDAO tagDAO;
	
	/**
	 * 사용자 정보 관리
	 */
	private UserInfoDAO userInfoDAO;
	
	/**
	 * 컨테이너 정보 관리
	 */
	private ContainerDAO containerDAO;
	
	/**
	 * 거점 정보 관리
	 */
	private SpatialEventDAO spatialEventDAO;
	
	/**
	 * 
	 */
	private RtreeIndexDAO rtreeIndexDAO = RtreeIndexDAO.getInstance();


	private TableBufferManager ()
	{

		try {
			logger.debug("create BufferManager");

			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		userInfoDAO = new UserInfoDAO();
		
		logisticsAreaDao = new LogisticsAreaDAO();
		
		tagDAO = new TagDAO();
		
		conditionDAO = new ConditionDAO();
		
		containerDAO = new ContainerDAO();
		
		workingMemory = new HashMap<String, List>(); // 워킹 메모리 초기화		
		
		masterDAO = new MasterDAO(); // 인 메모리 기반 을 위한 db 접근 객체
		
		rtreeIndexDAO = new RtreeIndexDAO();
		
		spatialEventDAO = new SpatialEventDAO();


/*		try {
			// 인메모리용 메모리 저장 객체 생성
			masterTable = new HashMap<String, List>();
			List<String> tableList=masterDAO.showTables();
			for(int i=0;i<tableList.size();i++)
			{
				masterTable.put(tableList.get(i),this.selectList(tableList.get(i)) );				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}*/
	}


	/*
	 * Condition Table SQL
	 */

	private Object addWorkingMemory(String id, Object obj)
	{
		if(workingMemory.containsKey(id))
		{
			List<Object> list = workingMemory.get(id);

			list.add(obj);
			if(list.size()>10)
			{
				list.remove(list.get(0));
				logger.debug(id+":remove list");
			}else
			{
				logger.debug(id+":update list:"+list.size());	
			}
		}else
		{
			List<Object> list = new Vector<Object>();
			list.add(obj);
			workingMemory.put(id, list);
			logger.debug("crate new list:"+id);
		}
		return obj;
	}
	/**@deprecated
	 * @param op
	 * @return
	 */
	private String createNameSpace(AIRTable op)
	{
		if(op instanceof ConditionInfo)
		{
			String namespace= "tb_condition";
			switch (op.getTableType()) {
			case ConditionInfo.CONTAINER:
				namespace+="_container";
				break;
			case ConditionInfo.TEMPERATURE:
				namespace+="_temperature";
				break;
			case ConditionInfo.HUMIDITY:
				namespace+="_humidity";
				break;	
			case ConditionInfo.HIT:
				namespace+="_hit";
				break;
			case ConditionInfo.DATE:
				namespace+="_date";				
				break;
			case ConditionInfo.ACTION:
				namespace="tb_action";				
				break;
			case ConditionInfo.TIME:
				namespace+="_time";
				break;	
			default:
				break;
			}
			return namespace;
		}
		else if(op instanceof ContainerInfo)
		{
			return "tb_container";
		}
		else if(op instanceof UserInfo)
		{
			return "tb_user";
		}
		if(op instanceof LocationInfo)
		{
			return "tb_location";
		}
		else if(op instanceof TagInfo)
		{
			return "tb_tag";
		}
		else if(op instanceof ActionInfo)
		{
			return "tb_action";
		}
		return null;

	}	


	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int deleteActionInfo(ActionInfo parameter) throws SQLException
	{
		return conditionDAO.delete( parameter);
	}
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public int deleteConditionInfo(ConditionInfo parameter) throws SQLException
	{	
		return conditionDAO.delete(parameter);
	}	

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int deleteLocationInfo(LocationInfo parameter) throws SQLException
	{
		return logisticsAreaDao.delete( parameter);
	}
	//================================================================================
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int deleteTagInfo(TagInfo parameter) throws SQLException
	{
		return tagDAO.delete(parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public int deleteUserInfo(UserInfo parameter) throws SQLException
	{
		return userInfoDAO.delete(parameter);
	}
	public int getMaxQueryID() throws SQLException
	{
		return conditionDAO.getMaxQueryID();
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insertActionInfo(ActionInfo parameter) throws SQLException
	{
		return conditionDAO.insert(parameter);
	}
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public Object insertConditionInfo(ConditionInfo parameter) throws SQLException
	{
		return conditionDAO.insert(parameter);
	}


	/*
	 * Tag Table
	 */

	public Object insertContainerInfo(ContainerInfo parameter) throws SQLException {
		return containerDAO.insert(parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insertLocationInfo(LocationInfo parameter) throws SQLException
	{			
		return logisticsAreaDao.insert(parameter);
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insertTagInfo(TagInfo parameter) throws SQLException
	{
		return tagDAO.insert(parameter);
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insertUserInfo(UserInfo parameter) throws SQLException
	{
		return userInfoDAO.insert(parameter);
	}


	/*
	 * User Table
	 */

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public ActionInfo selectActionInfo(ActionInfo parameter) throws SQLException
	{
		return conditionDAO.select(parameter);
	}
	
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public Object selectConditionInfo(ConditionInfo parameter) throws SQLException
	{	
		return conditionDAO.select(parameter);
	}
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */

	public List selectConditionInfoList(ConditionInfo parameter) throws SQLException
	{	
		return conditionDAO.selectList(parameter);
	}
	
	public List selectConditionInfoListByTagID(ConditionInfo parameter) throws SQLException
	{	
		return conditionDAO.selectListByTagID(parameter);
	}

	

	public List<ActionInfo> selectListActionInfo() throws SQLException
	{
		return conditionDAO.selectList();
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List<ActionInfo> selectListActionInfo(ActionInfo parameter) throws SQLException
	{
		return conditionDAO.selectList(parameter);
	}

	/**@deprecated
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectListConditionInfo(ActionInfo parameter) throws SQLException
	{
		return sqlMap.queryForList("tb_condition.select", parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectListContainerInfo(ContainerInfo parameter) throws SQLException
	{
		return containerDAO.selectList(parameter);
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectListLocationInfo(LocationInfo parameter) throws SQLException
	{
		return logisticsAreaDao.selectList(parameter);
	}	

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectListTagInfo(TagInfo parameter) throws SQLException
	{
		return tagDAO.selectList(parameter);
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectListTagUserInfo(TagUserInfo parameter) throws SQLException
	{
		return tagDAO.selectTagUserList(parameter);
	}
	
	public List selectListTagInfo(String tid) throws SQLException
	{
		return tagDAO.selectList(tid);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectTagListGroupBy() throws SQLException
	{
		return tagDAO.selectTagListGroupBy();
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List selectListUserInfo(UserInfo parameter) throws SQLException
	{
		return userInfoDAO.selectList(parameter);
	}


	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public LocationInfo selectLocationInfo(LocationInfo parameter) throws SQLException
	{
		return (LocationInfo) logisticsAreaDao.select(parameter);
	}


	public List selectQueryIDList(ActionInfo parameter) throws SQLException
	{
		return conditionDAO.selectQueryIDList(parameter);
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object selectTagInfo(TagInfo parameter) throws SQLException
	{
		return tagDAO.select(parameter);
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object selectUserInfo(UserInfo parameter) throws SQLException
	{
		return userInfoDAO.select(parameter);
	}


	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object updateActionInfo(ActionInfo parameter) throws SQLException
	{
		return conditionDAO.update(parameter);
	}
	/**
	 * @param parameter
	 * @param condition_type
	 * @return
	 * @throws SQLException
	 */
	public Object updateConditionInfo(ConditionInfo parameter) throws SQLException
	{		
		return conditionDAO.update(parameter);
	}



	/*
	 * Location Table
	 */
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object updateLocationInfo(LocationInfo parameter) throws SQLException
	{
		return logisticsAreaDao.update(parameter);
	}

	/**
	 * @param parameter
	 * @throws SQLException 
	 */





	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object updateTagInfo(TagInfo parameter) throws SQLException
	{
		return tagDAO.update( parameter);
	}
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object updateUserInfo(UserInfo parameter) throws SQLException
	{
		return userInfoDAO.update(parameter);
	}
	
	
	public List selectListRtreeIndexNode(RtreeIndexNode parameter) throws SQLException
	{
		return rtreeIndexDAO.getRtreeIndexNodes();
	}


	public LogisticsArea selectLogisticAreaInfo(LogisticsArea parameter) throws SQLException {
		return logisticsAreaDao.selectLogistic(parameter);
	}


	public void insertEvent(SpatialEventInfo option)throws SQLException {
		spatialEventDAO.insert(option);
	}


	public Object insertResultInfo(ResultInfo info) throws SQLException {
		return sqlMap.insert("tb_tag.insertResult",info);
		
	}
	public Object insertResultHistoryInfo(ResultInfo info) throws SQLException {
		return sqlMap.insert("tb_tag.insertResultHistory",info);
		
	}
	
	public int deleteResultInfo(ResultInfo info) throws SQLException {
		return sqlMap.delete("tb_tag.deleteResult",info);
		
	}
	public int deleteResultHistoryInfo(ResultInfo info) throws SQLException {
		return sqlMap.delete("tb_tag.deleteResultHistory",info);
		
	}
	public List selectResultInfo(ResultInfo info) throws SQLException {
		return sqlMap.queryForList("tb_tag.selectResult",info);
		
	}
	public List selectResultHistoryInfo(ResultInfo info) throws SQLException {
		return sqlMap.queryForList("tb_tag.selectResultHistory",info);
		
	}
	public List selectResultTIDGroupByTID() throws SQLException {
		return sqlMap.queryForList("tb_tag.selectResultGroupByTID");
		
	}
	public List selectResultHistoryTIDGroupByTID() throws SQLException {
		return sqlMap.queryForList("tb_tag.selectResultHistoryGroupByTID");
		
	}


	public List selectSEQEvent(SpatialEventInfo info) throws SQLException{
		// TODO Auto-generated method stub
		return sqlMap.queryForList("tb_location.selectSEQEvent",info);
	}
	public Object insertSEQEvent(SpatialEventInfo info)throws SQLException
	{
		return sqlMap.insert("tb_location.insertSEQEvent",info);
	}
	public int deleteSEQEvent(SpatialEventInfo info)throws SQLException
	{
		return sqlMap.delete("tb_location.deleteSEQEvent",info);
	}
	
	
	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public List<LocationInfo> selectListGateLocation(LocationInfo parameter) throws SQLException
	{
		return logisticsAreaDao.selectListGate(parameter);
	}
	
	public LocationInfo selectGateLocationInfo(LocationInfo parameter) throws SQLException
	{
		return logisticsAreaDao.selectGateInfo(parameter);
	}


	/** 
	 * @see 태그 사용자 등록
	 * @param parameter
	 * @return
	 * @throws SQLException
	 */
	public Object insertTagUserInfo(TagUserInfo parameter) throws SQLException
	{
		return tagDAO.insertTagUser(parameter);
	}


	/**@설명 태그 사용자 삭제
	 * @param op
	 * @return
	 * @throws SQLException
	 */
	public int deleteTagUserInfo(TagUserInfo op) throws SQLException {
		// TODO Auto-generated method stub
		return tagDAO.deleteTagUser(op);
	}

	public List selectContainerInfoList(ContainerInfo parameter) throws SQLException
	{
		return containerDAO.selectList(parameter);
	}
	
	public int deleteContainerInfo(ContainerInfo parameter) throws SQLException
	{
		return containerDAO.delete(parameter);
	}

}
