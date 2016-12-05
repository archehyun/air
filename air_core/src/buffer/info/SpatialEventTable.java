package buffer.info;

import java.sql.SQLException;

import buffer.dao.TableBufferManager;

/**
 * Spatial Event�� �����ϴ� DB ���̺� ��ü
 * 
 * @author		�ں���
 * @since       2014-02-03
 * @version     0.1       
 */
public class SpatialEventTable 
{
	public static final int SPATIAL_EVENT_NONE = 0;
	public static final int SPATIAL_EVENT_INTO = 1;
	public static final int SPATIAL_EVENT_OUTOF = 2;
	public static final int SPATIAL_EVENT_THROUGH = 3;
	TableBufferManager bufferManager = TableBufferManager.getInstance();
	private static SpatialEventTable table;
	
	static
	{
		table = new SpatialEventTable();
	}
	
	/*
	 * Spatial Event Table �ν��Ͻ��� ȹ���� �� �ִ� Ŭ���� �޼���
	 */
	public static SpatialEventTable getInstance()
	{
		return table;
	}
	
	public SpatialEventTable()
	{
	}
	
	public void insertEvent(String tid, String cid, short year, byte month, byte day, byte hour, byte minute, String areaID, int event)
	{
		SpatialEventInfo option = new SpatialEventInfo();
		option.setEvent_type(event);
		option.setTid(tid);
		option.setCid(cid);
		option.setLocation_code(areaID);
		
		try {
			bufferManager.insertSEQEvent(option);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
