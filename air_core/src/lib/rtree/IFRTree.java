package lib.rtree;

import java.sql.SQLException;

import lib.geometry.Point;
import spatial.LogisticsArea;

public interface IFRTree {
	public LogisticsArea getNearestLogisticsArea(Point p) throws NullPointerException, SQLException;

}
