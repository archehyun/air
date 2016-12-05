package lib.geometry;

import lib.rtree.MBR;

/**
 * 물류거점을 둘러싸는 사각형 객체
 * 
 * @author		박병권
 * @since       2014-02-03
 * @version     0.1       
 */
public class Quadrangle
{
	double x1; //x of the left most point
	double y1; //y of the left most point
	double x2; //x of the bottom point
	double y2; //y of the bottom point
	double x3; //x of the right most point
	double y3; //y of the right most point
	double x4; //x of the top point
	double y4; //y of the top point
	
	double a1; //Lower Left Line의 기울기
	double b1; //Lower Left Line의 Y 절편
	double a2; //Lower Right Line의 기울기
	double b2; //Lower Right Line의 Y 절편
	double a3; //Upper Right Line의 기울기
	double b3; //Upper Right Line의 Y 절편
	double a4; //Upper Left Line의 기울기
	double b4; //Upper Left Line의 Y 절편

	public Quadrangle(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
		this.x4 = x4;
		this.y4 = y4;
		
		a1 = (y2 - y1) / (x2 - x1);
		b1 = y1 - a1 * x1;
		
		a2 = (y3 - y2) / (x3 - x2);
		b2 = y2 - a2 * x2;
		
		a3 = (y3 - y4) / (x3 - x4);
		b3 = y3 - a3 * x3;
		
		a4 = (y1 - y4) / (x1 - x4);
		b4 = y4 - a4 * x4;
	}

	public double getX1() {
		return x1;
	}

	public double getY1() {
		return y1;
	}

	public double getX2() {
		return x2;
	}

	public double getY2() {
		return y2;
	}

	public double getX3() {
		return x3;
	}

	public double getY3() {
		return y3;
	}

	public double getX4() {
		return x4;
	}

	public double getY4() {
		return y4;
	}
	
	public MBR getMBR()
	{
		return new MBR(getLowerLeftX(), getLowerLeftY(), getUpperRightX(), getUpperRightY());
	}
	
	public double getLowerLeftX()
	{
		return Math.min(x1,  Math.min(x2, Math.min(x3,  x4)));
	}
	
	public double getLowerLeftY()
	{
		return Math.min(y1,  Math.min(y2, Math.min(y3,  y4)));
	}
	
	public double getUpperRightX()
	{
		return Math.max(x1,  Math.max(x2, Math.max(x3,  x4)));
	}
	
	public double getUpperRightY()
	{
		return Math.max(y1,  Math.max(y2, Math.max(y3,  y4)));
	}
	public String toString()
	{
		return "Q["+this.getX1()+","+this.getY1()+","+this.getX3()+","+this.getY3()+"]";
	}
}
