package spatial;

import lib.geometry.Point;
import lib.geometry.Quadrangle;
import lib.geometry.SpatialOperator;
import buffer.info.LocationInfo;

/**
 * �������� ��ü
 * 
 * @author		�ں���
 * @since       2014-02-03
 * @version     0.1       
 */

public class LogisticsArea extends LocationInfo
{
	public static byte EAST = 1;
	public static byte WEST = 2;
	public static byte SOUTH = 3;
	public static byte NORTH = 4;
	private int gate;
	public int getGate() {
		return gate;
	}

	public void setGate(int gate) {
		this.gate = gate;
	}

	private long areaID; //��������ID
	private String areaName; //������
	private Quadrangle boundary; //������ �ѷ��� ��Ÿ���� �簢��
	private boolean isGate; //���� ��ü�� ����Ʈ�̸� true
	private boolean hasGate; //���� ������ ����Ʈ�� ���� �ϸ� true
	private int nGate; //# of gates
	private Quadrangle[] gateInfo; //������ �� ����Ʈ �ѷ��� ��Ÿ���� �簢����
	private byte[] directionOfIN; //����Ʈ�� ���� �������� ���� ����: East, West, South, North

	public LogisticsArea(long areaID, String areaName, Quadrangle boundary, boolean isGate, boolean hasGate, Quadrangle[] gate, byte[] direction)
	{
		this.areaID = areaID;
		this.areaName = areaName;
		this.boundary = boundary;
		this.isGate = isGate;
		this.hasGate = hasGate;
		this.gateInfo = gate;
		this.directionOfIN = direction;
		
	}

	public LogisticsArea() {
		
	}
	public LogisticsArea(LocationInfo info) {
		this.setLocation_code(info.getLocation_code());
		this.areaName = info.getLocation_name();
		this.
		boundary = new Quadrangle(info.getX1(), info.getY1(), info.getX2(), info.getY2(), info.getX3(), info.getY3(), info.getX4(), info.getY4());
		/*
		 * -1: �ڽ��� ����Ʈ
		 * 0: ����Ʈ �ƴ�
		 * 1~4: ����Ʈ ��
		 */
		this.isGate =(info.getGate()==-1)?true:false;
		this.setLocation_name(info.getLocation_name());
		
		this.gate = info.getGate();
		this.hasGate =(info.getGate()>0)?true:false;
	}

	/**
	 * @return
	 */
	public String getLogisticsAreaID() {
		return this.getLocation_code();
	}

	/**
	 * @param areaID
	 */
	public void setLogisticsAreaID(long areaID) {
		this.areaID = areaID;
	}

	/**
	 * @return
	 */
	public String getLogisticsAreaName() {
		return areaName;
	}

	public void setLogisticsAreaName(String areaName) {
		this.areaName = areaName;
	}

	/**
	 * @return
	 */
	public Quadrangle getBoundary() {
		
		//�߰� �ҽ�
		if(boundary==null)
			return new Quadrangle(this.getX1(), this.getY1(), this.getX2(), this.getY2(), this.getX3(), this.getY3(), this.getX4(), this.getY4());
		
		return boundary;
	}

	public void setBoundary(Quadrangle boundary) {
		this.boundary = boundary;
	}

	public boolean isGate() {
		return isGate;
	}

	public boolean hasGate() {
		return hasGate;
	}
	
	public Quadrangle[] getGateInfo() {
		return gateInfo;
	}
	
	public void setGate(Quadrangle[] gateInfo) {
		
		
		this.gateInfo = gateInfo;
		this.setNumberOfGate(gateInfo.length);
	}
	
	public byte[] getDirectionOfIN() {
		return directionOfIN;
	}

	public void setDirectionOfIN(byte[] directionOfIN) {
		this.directionOfIN = directionOfIN;
	}

	public int getNumberOfGate() {
		return nGate;
	}

	/**
	 * @param nGate
	 */
	public void setNumberOfGate(int nGate) {
		this.nGate = nGate;
	}
	/**
	 * @param point
	 * @return
	 */
	public double shortestDistanceTo(Point point)
	{
		if (hasGate)
		{
			double minDistance = -1;
			for (int i = 0; i < nGate; i++)
			{
				double d = SpatialOperator.longestDistance(point, gateInfo[i]);
				if (minDistance < 0 || d < minDistance)
				{
					minDistance = d;
				}
			}
			return minDistance;
		}
		else
		{
			//return SpatialOperator.shortestDistance(point, boundary);
			//����
			return SpatialOperator.shortestDistance(point, this.getBoundary());
		}
	}
}
