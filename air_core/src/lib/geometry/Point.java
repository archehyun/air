package lib.geometry;

/**
 * 태그의 위치를 표시하는 점 객체
 * 
 * @author		박병권
 * @since       2014-02-03
 * @version     0.1       
 */
public class Point 
{
	double x;
	double y;
	
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
