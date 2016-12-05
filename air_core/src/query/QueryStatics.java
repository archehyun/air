package query;

public class QueryStatics {
	 public QueryStatics(String tid) {
		 this.tid = tid;
	}
	
	private String tid;
	public String getTid() {
		return tid;
	}

	private float avgDBIO=0,maxDBIO=0,minDBIO=0,totalDBIO=0;
	private Long processTime=0l,dbIOProcessTime=0l;
	public void setDbIOProcessTime(Long dbIOProcessTime) {
		this.dbIOProcessTime = dbIOProcessTime;
	}
	private float tagTotalProcessTime=0;
	private double totalProcessTime=0;
	
	
	public long getDbIOProcessTime() {
		return dbIOProcessTime;
	}

	public void setDbIOProcessTime(long dbIOProcessTime) {
		this.dbIOProcessTime = dbIOProcessTime;
	}

	public double getTotalProcessTime() {
		return totalProcessTime;
	}

	public void setTotalProcessTime(double totalProcessTime) {
		this.totalProcessTime = totalProcessTime;
	}
	private int tagProcessCount=1;
	
	public int getTagProcessCount() {
		return tagProcessCount;
	}

	public float getTagTotalProcessTime() {
		return tagTotalProcessTime;
	}

	public Long getProcessTime() {
		return processTime;
	}
	public void setProcessTime(Long processTime) {
		this.processTime = processTime;
	}
	public float getTotalDBIO() {
		return totalDBIO;
	}
	public void setTotalDBIO(float totalDBIO) {
		this.totalDBIO = totalDBIO;
	}
	public float getAvgDBIO() {
		
		return totalDBIO/this.getNumDBIO();
	}
	public void setAvgDBIO(float avgDBIO) {
		this.avgDBIO = avgDBIO;
	}
	public float getMaxDBIO() {
		return maxDBIO;
	}
	public void setMaxDBIO(float maxDBIO) {
		this.maxDBIO = maxDBIO;
	}
	public float getMinDBIO() {
		return minDBIO;
	}
	public void setMinDBIO(float minDBIO) {
		this.minDBIO = minDBIO;
	}
	public int getNumDBIO() {
		return numDBIO;
	}
	public void setNumDBIO(int numDBIO) {
		this.numDBIO = numDBIO;
	}
	private int numDBIO;

	public void updateTagProcessCount() {
		tagProcessCount++;
		
	}

	public void updateTagTotalProcessTime(double processTime) {
		tagTotalProcessTime+=processTime;
		tagProcessCount++;
	}
	public long getAverageTagProcessTime()
	{
		return (long) (tagTotalProcessTime/tagProcessCount);
	}

	public void updateTotalProcessTime(double processTime2) {
	
		
	}

}
