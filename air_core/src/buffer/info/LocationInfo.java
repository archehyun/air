package buffer.info;

import lib.rtree.MBR;

public class LocationInfo extends AIRTable{
	private String direction;
	private String location_code;
	private String gate_code;
	public String getGate_code() {
		return gate_code;
	}
	public void setGate_code(String gate_code) {
		this.gate_code = gate_code;
	}
	public String getGate_name() {
		return gate_name;
	}
	public void setGate_name(String gate_name) {
		this.gate_name = gate_name;
	}
	private String location_name;
	private String gate_name;
	private int gate;
	public int getGate() {
		return gate;
	}
	public void setGate(int gate) {
		this.gate = gate;
	}
	private double x1;
	private double x2;
	private double x3;
	private double x4;
	private double y1;
	private double y2;
	private double y3;
	private double y4;

	public String getDirection() {
		return direction;
	}
	public String getLocation_code() {
		return location_code;
	}
	public String getLocation_name() {
		return location_name;
	}
	public double getX1() {
		return x1;
	}
	public double getX2() {
		return x2;
	}
	public double getX3() {
		return x3;
	}
	public double getX4() {
		return x4;
	}
	public double getY1() {
		return y1;
	}
	public double getY2() {
		return y2;
	}
	public double getY3() {
		return y3;
	}
	public double getY4() {
		return y4;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void setLocation_code(String location_code) {
		this.location_code = location_code;
	}
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	public void setX1(double x1) {
		this.x1 = x1;
	}
	public void setX2(double x2) {
		this.x2 = x2;
	}
	public void setX3(double x3) {
		this.x3 = x3;
	}
	public void setX4(double x4) {
		this.x4 = x4;
	}
	public void setY1(double y1) {
		this.y1 = y1;
	}
	public void setY2(double y2) {
		this.y2 = y2;
	}
	public void setY3(double y3) {
		this.y3 = y3;
	}
	public void setY4(double y4) {
		this.y4 = y4;
	}
	public MBR getMBR()
	{
		return new MBR(getX1(), getY1(), getX3(), getY3());
	}


}
