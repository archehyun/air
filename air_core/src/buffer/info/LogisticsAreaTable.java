package buffer.info;


/**
 * 물류거점 Location을 관리하는 DB 테이블 객체
 * 
 * @author		박병권
 * @since       2014-07-03
 * @version     0.1       
 */
public class LogisticsAreaTable 
{
	private static LogisticsAreaTable table;
	
	static
	{
		table = new LogisticsAreaTable();
	}
	
	/*
	 * Spatial Event Table 인스턴스를 획득할 수 있는 클래스 메서드
	 */
	public static LogisticsAreaTable getInstance()
	{
		return table;
	}
	
	public LogisticsAreaTable()
	{
	}
/*	
	public LogisticsArea getLogisticsArea(long areaID)
	{
		LogisticsArea area = null;
		
		return area;
	}*/
}
