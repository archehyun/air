package buffer.info;


/**
 * �������� Location�� �����ϴ� DB ���̺� ��ü
 * 
 * @author		�ں���
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
	 * Spatial Event Table �ν��Ͻ��� ȹ���� �� �ִ� Ŭ���� �޼���
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
