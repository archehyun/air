package lib.rtree;

import lib.geometry.Point;

public class MBR 
{
	private double lowerLeftX; // x of Lower Left Corner of MBR
	private double lowerLeftY; // y of Lower Left Corner of MBR
	private double upperRightX; // x of Upper Right Corner of MBR
	private double upperRightY; // y of Upper Right Corner of MBR
	
	public MBR(double lowerLeftX, double lowerLeftY, double upperRightX, double upperRightY)
	{
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.upperRightX = upperRightX;
		this.upperRightY = upperRightY;
	}
	public String toString()
	{
		return "["+lowerLeftX+","+lowerLeftY+","+upperRightX+","+upperRightY+"]";
	}
	
	public double getLowerLeftX()
	{
		return lowerLeftX;
	}
	
	public double getLowerLeftY()
	{
		return lowerLeftY;
	}
	
	public double getUpperRightX()
	{
		return upperRightX;
	}
	
	public double getUpperRightY()
	{
		return upperRightY;
	}
	
	public double getArea()
	{
		return (upperRightX - lowerLeftX) * (upperRightY - lowerLeftY);
	}
	// »Æ¿Â
	public double getEnlargement(MBR newMBR)
	{
		double x1 = newMBR.getLowerLeftX();
		double y1 = newMBR.getLowerLeftY();
		double x2 = newMBR.getUpperRightX();
		double y2 = newMBR.getUpperRightY();
		
		double newX1 = x1 < lowerLeftX ? x1 : lowerLeftX;
		double newY1 = y1 < lowerLeftY ? y1 : lowerLeftY;
		double newX2 = x2 > upperRightX ? x2 : upperRightX;
		double newY2 = y2 > upperRightY ? y2 : upperRightY;
		
		double currentArea = (upperRightX - lowerLeftX) * (upperRightY - lowerLeftY);
		double newArea = (newX2 - newX1) * (newY2 - newY1);
		return (newArea - currentArea);
	}
	
	public boolean contain(Point p)
	{
		boolean result = false;
		if (lowerLeftX <= p.getX() && p.getX() <= upperRightX && lowerLeftY <= p.getY() && p.getY() <= upperRightY)
		{
			result = true;
		}
		return result;
	}
	
	public boolean contain(MBR mbr)
	{
		boolean result = false;
		if (lowerLeftX <= mbr.getLowerLeftX() && upperRightX >= mbr.getUpperRightX() &&
			lowerLeftY <= mbr.getLowerLeftY() && upperRightY >= mbr.getUpperRightY())
		{
			result = true;
		}
		return result;
	}
	
	public boolean overlap(MBR mbr)
	{
		boolean result = false;
		if (lowerLeftX > mbr.getUpperRightX() || upperRightX < mbr.getLowerLeftX() ||
			lowerLeftY > mbr.getUpperRightY() || upperRightY < mbr.getLowerLeftY())
		{
			result = false;
		}
		return result;
	}
	
	public double shortestDistanceTo(Point point)
	{	
		double d1, d2, d3, d4;
		d1 = Math.sqrt((point.getX() - ((lowerLeftX + upperRightX)/2.0))*(point.getX() - ((lowerLeftX + upperRightX)/2.0)) + (point.getY() - lowerLeftY)*(point.getY() - lowerLeftY));
		d2 = Math.sqrt((point.getX() - upperRightX)*(point.getX() - upperRightX) + (point.getY() - ((lowerLeftY + upperRightY)/2.0))*(point.getY() - ((lowerLeftY + upperRightY)/2.0)));
		d3 = Math.sqrt((point.getX() - ((upperRightX + lowerLeftX)/2.0))*(point.getX() - ((upperRightX + lowerLeftX)/2.0)) + (point.getY() - upperRightY)*(point.getY() - upperRightY));
		d4 = Math.sqrt((point.getX() - lowerLeftX)*(point.getX() - lowerLeftX) + (point.getY() - ((upperRightY + lowerLeftY)/2.0))*(point.getY() - ((upperRightY + lowerLeftY)/2.0)));
		
		return Math.min(d1, Math.min(d2, Math.min(d3, d4)));
	}
}
