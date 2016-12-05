package lib.geometry;

public class SpatialOperator 
{
	/**
	 * 공간 연산 'IN'
	 * Point가 Quadrangle 내에 있으면 true, 없으면 false
	 * 
	 * @param point		Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean in(Point point, Quadrangle quad)
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
		
		
		// min(X1,X2,X3,X4) <= P <max(X1,X2,X3,X4)
		
		boolean condition1=(min(x1,x2,x3,x4)<=p&&p<=max(x1,x2,x3,x4));
		boolean condition2=(min(y1,y2,y3,y4)<=q&&q<=max(y1,y2,y3,y4));
		boolean condition3=(q>=a1*p +b1); 
		boolean condition4=(p<=1/a2*(q-b2));
		boolean condition5=(q<=a3*p+b3);
		boolean condition6=(p>=1/a4*(q-b4));	
		
		return condition1&&condition2&&condition3&&condition4&&condition5&&condition6;
	}
	public static void main(String[] args) {
		
	}
	private static double min(double x1,double x2,double x3,double x4)
	{
		
		double temp1=Math.min(x1,x2);
		double temp2 =Math.min(temp1, x3);
		double temp3 =Math.min(temp2, x4);
		
		return temp3;
	}
	private static double max(double x1,double x2,double x3,double x4)
	{
		double temp1=Math.max(x1,x2);
		double temp2 =Math.max(temp1, x3);
		double temp3 =Math.max(temp2, x4);
		
		return temp3;
	}
	public static boolean in2(Point point, Quadrangle quad)
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
		
		System.out.println("p>x1:"+p+","+x1+","+(p>x1));
		System.out.println("p<x3:"+p+","+x3+","+(p<x3));
		System.out.println("q>y2:"+q+","+y2+","+(q>y2));
		System.out.println("q<y4:"+q+","+y4+","+(q<y4));
		
		if (p > x1 && p < x3 && q > y2 && q < y4)
		{
			System.out.println("step1");
			result = true;
			
			if (p < x2 && q < y1)
			{
				System.out.println("step1-1");
				System.out.println("p<x2:"+p+","+x2+","+(p<x2));
				System.out.println("q<y1:"+q+","+y1+","+(q<y1));
				
				if (q < (a1*p + b1))
				{
					result = false;
				}
			}
			else if (p > x2 && q < y3)
			{
				System.out.println("step1-2");
				if (q < (a2*p + b2))
				{
					result = false;
				}
			}
			else if (p > x4 && q > y3)
			{
				System.out.println("step1-3");
				if (q > (a3*p + b3))
				{
					result = false;
				}
			}
			else if (p < x4 && q > y1)
			{
				System.out.println("step1-4");
				if (q > (a4*p + b4))
				{
					result = false;
				}
			}
			//시뮬레이션을 위한 임시 추가
			result = true;
		}
		
		return result;
	}
	
	/**
	 * 공간 연산 'OUT'
	 * Point가 Quadrangle 내에 없으면 true, 있으면 false
	 * 
	 * @param point		Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean out(Point point, Quadrangle quad)
	{
		return !in(point, quad);
	}
	
	/**
	 * 공간 연산 'into'
	 * 
	 * @param point1	Previous Point
	 * @param point2	Current Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean into(Point point1, Point point2, Quadrangle quad)
	{
		return out(point1, quad) && in(point2, quad);
	}
	
	/**
	 * 공간 연산 'outof'
	 * 
	 * @param point1	Previous Point
	 * @param point2	Current Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean outof(Point point1, Point point2, Quadrangle quad)
	{
		return in(point1, quad) && out(point2, quad);
	}
	
	/**
	 * 공간 연산 'THROUGH'
	 * 
	 * @param point1	Left Point
	 * @param point2	Right Point
	 * @param quad		Quadrangle
	 * @return			true if succeed, otherwise false          
	 */
	public static boolean through(Point point1, Point point2, Quadrangle quad)
	{
		return throughHorizontally(point1, point2, quad) || throughVertically(point1, point2, quad);
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
	public static boolean throughHorizontally(Point point1, Point point2, Quadrangle quad)
	{
		double a = (point2.y - point1.y) / (point2.x - point1.x);
		double b = point1.y - a * point1.x;
		
		boolean result = false;
		
		if (!in(point1, quad) && !in(point2, quad))
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
	public static boolean throughVertically(Point point1, Point point2, Quadrangle quad)
	{
		double a = (point2.y - point1.y) / (point2.x - point1.x);
		double b = point1.y - a * point1.x;
		
		boolean result = false;
		
		if (!in(point1, quad) && !in(point2, quad))
		{
			if (((quad.y1 - b)/a - quad.x1)*((quad.y3 - b)/a - quad.x3) < 0)
			{
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * 공간 연산 'longestDistance'
	 * 한 Point에서 주어진 Quadrangle까지의 최장거리
	 * (Point에서 Quadrangle 각 변의 중점까지 거리중 가장 긴 것)
	 * 
	 * @param point		Point
	 * @param quad		Quadrangle
	 * @return			longest distance between point and quadrangle          
	 */
	public static int longestDistance2(Point point, Quadrangle quad)
	{	
		double d1, d2, d3, d4;
		d1 = Math.sqrt((point.x - ((quad.x1 + quad.x2)/2.0))*(point.x - ((quad.x1 + quad.x2)/2.0)) + (point.y - ((quad.y1 + quad.y2)/2.0))*(point.y - ((quad.y1 + quad.y2)/2.0)));
		d2 = Math.sqrt((point.x - ((quad.x2 + quad.x3)/2.0))*(point.x - ((quad.x2 + quad.x3)/2.0)) + (point.y - ((quad.y2 + quad.y3)/2.0))*(point.y - ((quad.y2 + quad.y3)/2.0)));
		d3 = Math.sqrt((point.x - ((quad.x3 + quad.x4)/2.0))*(point.x - ((quad.x3 + quad.x4)/2.0)) + (point.y - ((quad.y3 + quad.y4)/2.0))*(point.y - ((quad.y3 + quad.y4)/2.0)));
		d4 = Math.sqrt((point.x - ((quad.x4 + quad.x1)/2.0))*(point.x - ((quad.x4 + quad.x1)/2.0)) + (point.y - ((quad.y4 + quad.y1)/2.0))*(point.y - ((quad.y4 + quad.y1)/2.0)));
		return (int)Math.max(d1, Math.max(d2, Math.max(d3, d4)));
	}
	
	public static double longestDistance(Point point, Quadrangle quad)
	{	
		double d1, d2, d3, d4;
		d1 = Math.sqrt((point.x - ((quad.x1 + quad.x2)/2.0))*(point.x - ((quad.x1 + quad.x2)/2.0)) + (point.y - ((quad.y1 + quad.y2)/2.0))*(point.y - ((quad.y1 + quad.y2)/2.0)));
		d2 = Math.sqrt((point.x - ((quad.x2 + quad.x3)/2.0))*(point.x - ((quad.x2 + quad.x3)/2.0)) + (point.y - ((quad.y2 + quad.y3)/2.0))*(point.y - ((quad.y2 + quad.y3)/2.0)));
		d3 = Math.sqrt((point.x - ((quad.x3 + quad.x4)/2.0))*(point.x - ((quad.x3 + quad.x4)/2.0)) + (point.y - ((quad.y3 + quad.y4)/2.0))*(point.y - ((quad.y3 + quad.y4)/2.0)));
		d4 = Math.sqrt((point.x - ((quad.x4 + quad.x1)/2.0))*(point.x - ((quad.x4 + quad.x1)/2.0)) + (point.y - ((quad.y4 + quad.y1)/2.0))*(point.y - ((quad.y4 + quad.y1)/2.0)));
		
		return Math.max(d1, Math.max(d2, Math.max(d3, d4)));
	}
	
	/**
	 * 공간 연산 'shortestDistance'
	 * 한 Point에서 주어진 Quadrangle까지의 최단거리
	 * (Point에서 Quadrangle 각 변의 중점까지 거리중 가장 짧은 것)
	 * 
	 * @param point		Point
	 * @param quad		Quadrangle
	 * @return			shortest distance between point and quadrangle          
	 */
	public static int shortestDistance(Point point, Quadrangle quad)
	{	
		double d1, d2, d3, d4;
		d1 = Math.sqrt((point.x - ((quad.x1 + quad.x2)/2.0))*(point.x - ((quad.x1 + quad.x2)/2.0)) + (point.y - ((quad.y1 + quad.y2)/2.0))*(point.y - ((quad.y1 + quad.y2)/2.0)));
		d2 = Math.sqrt((point.x - ((quad.x2 + quad.x3)/2.0))*(point.x - ((quad.x2 + quad.x3)/2.0)) + (point.y - ((quad.y2 + quad.y3)/2.0))*(point.y - ((quad.y2 + quad.y3)/2.0)));
		d3 = Math.sqrt((point.x - ((quad.x3 + quad.x4)/2.0))*(point.x - ((quad.x3 + quad.x4)/2.0)) + (point.y - ((quad.y3 + quad.y4)/2.0))*(point.y - ((quad.y3 + quad.y4)/2.0)));
		d4 = Math.sqrt((point.x - ((quad.x4 + quad.x1)/2.0))*(point.x - ((quad.x4 + quad.x1)/2.0)) + (point.y - ((quad.y4 + quad.y1)/2.0))*(point.y - ((quad.y4 + quad.y1)/2.0)));
		
		return (int)Math.min(d1, Math.min(d2, Math.min(d3, d4)));
	}
	
	/*public static void main(String[] args) {
		
		//35.164889	128.899822	35.165204	128.899951	35.16424	128.90553	35.163976	128.905508
		//128.899822	35.164889	128.905508	35.163976	128.90553	35.16424	128.899951	35.165204

		Point point = new Point(128.902869, 35.164151);
		Quadrangle quad = new Quadrangle(35.164889, 128.899822, 35.165204, 128.899951, 35.16424, 128.90553, 35.163976, 128.905508);
		Quadrangle quad2 = new Quadrangle(128.899822,35.164889,128.899951,  35.165204, 128.90553, 35.16424, 128.905508, 35.163976 );
		Quadrangle quad3 = new Quadrangle(128.899822,35.164889,128.905508,  35.163976, 128.90553, 35.16424, 128.899951, 335.165204 );
		System.out.println(SpatialOperator.in(point, quad3));
	}*/
}
