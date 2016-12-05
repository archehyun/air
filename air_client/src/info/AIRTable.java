package info;


public class AIRTable {
	
	public static final int CONTAINER=0;
	public static final int TEMPERATURE=1;
	public static final int HUMIDITY=2;
	public static final int HIT=3;
	public static final int DATE=4;
	public static final int TIME=5;
	public static final int ACTION=6;
	
	public AIRTable() {
	}
	
	public AIRTable(int tableType) {
		this.setTableType(tableType);
	}
	public int tableType;

	public int getTableType() {
		return tableType;
	}

	public void setTableType(int tableType) {
		this.tableType = tableType;
	}
}
