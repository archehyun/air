package lib.geometry;

public class SpatialOperation 
{
	/**
	 * 공간 연산 'IN'
	 * Point가 Quadrangle 내에 있으면 true, 없으면 false
	 * 
	 * @param point		Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean isIn(Point point, Quadrangle quad)
	{
		double p = point.x;
		double q = point.y;
		
		double x1 = quad.x1; //x of the left most point
		double y1 = quad.y1; //y of the left most point
		double x2 = quad.x2; //x of the bottom point
		double y2 = quad.y2; //y of the bottom point
		double x3 = quad.x3; //x of the right most point
		double y3 = quad.y3; //y of the right most point
		double x4 = quad.x4; //x of the top point
		double y4 = quad.y4; //y of the top point
		
		double a1 = quad.a1; //Lower Left Line의 기울기
		double b1 = quad.b1; //Lower Left Line의 Y 절편
		double a2 = quad.a2; //Lower Right Line의 기울기
		double b2 = quad.b2; //Lower Right Line의 Y 절편
		double a3 = quad.a3; //Upper Right Line의 기울기
		double b3 = quad.b3; //Upper Right Line의 Y 절편
		double a4 = quad.a4; //Upper Left Line의 기울기
		double b4 = quad.b4; //Upper Left Line의 Y 절편
		
		boolean result = false;
		
		if (p > x1 && p < x3 && q > y2 && q < y4)
		{
			result = true;
			
			if (p < x2 && q < y1)
			{
				if (q < (a1*p + b1))
				{
					result = false;
				}
			}
			else if (p > x2 && q < y3)
			{
				if (q < (a2*p + b2))
				{
					result = false;
				}
			}
			else if (p > x4 && q > y3)
			{
				if (q > (a3*p + b3))
				{
					result = false;
				}
			}
			else if (p < x4 && q > y1)
			{
				if (q > (a4*p + b4))
				{
					result = false;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 공간 연산 'THROUGH'
	 * 두 개의 Point를 연결한 직선이 Quadrangle을 수평으로 가로지르면 true
	 * 
	 * @param point1	Left Point
	 * @param point2	Right Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean goThroughHorizontally(Point point1, Point point2, Quadrangle quad)
	{
		double a = (point2.y - point1.y) / (point2.x - point1.x);
		double b = point1.y - a * point1.x;
		
		boolean result = false;
		
		if (!isIn(point1, quad) && !isIn(point2, quad))
		{
			if (((a*quad.x2 + b) - quad.y2)*((a*quad.x4 + b) - quad.y4) < 0)
			{
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * 공간 연산 'THROUGH'
	 * 두 개의 Point를 연결한 직선이 Quadrangle을 수직으로 가로지르면 true
	 * 
	 * @param point1	Upper Point
	 * @param point2	Lower Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean goThroughVertically(Point point1, Point point2, Quadrangle quad)
	{
		double a = (point2.y - point1.y) / (point2.x - point1.x);
		double b = point1.y - a * point1.x;
		
		boolean result = false;
		
		if (!isIn(point1, quad) && !isIn(point2, quad))
		{
			if (((quad.y1 - b)/a - quad.x1)*((quad.y3 - b)/a - quad.x3) < 0)
			{
				result = true;
			}
		}
		
		return result;
	}
}
