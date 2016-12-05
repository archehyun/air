package lib.rtree;

import java.sql.SQLException;
import java.util.List;

import lib.geometry.Point;
import spatial.LogisticsArea;
import buffer.dao.TableBufferManager;
import buffer.info.LocationInfo;

public class TempRtree implements IFRTree{
	
	private static TempRtree instance;
	
	TableBufferManager bufferManager = TableBufferManager.getInstance();
	private TempRtree() {
		
	}
	
	public LogisticsArea getNearestLogisticsArea(Point p) throws NullPointerException, SQLException
	{
		
		List li=bufferManager.selectListLocationInfo(new LocationInfo());
		
		LocationInfo first=(LocationInfo) li.get(0);
		for(int i=0;i<li.size();i++)
		{
			LocationInfo  second = (LocationInfo) li.get(i);
			if(first.getMBR().shortestDistanceTo(p)>second.getMBR().shortestDistanceTo(p))
			{
				first=second;
			}
		}
		
		LogisticsArea area = new LogisticsArea(first);
		return area;
	}

	public static TempRtree getInstance() {
		if(instance== null)
			instance = new TempRtree();
		return instance;
	}

}
