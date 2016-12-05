package lib.rtee2;

import java.util.HashMap;

import lib.rtree.MBR;

/**창현 작성
 * @author archehyun
 *
 */
public class RtreeIndexNode2 extends HashMap<Long, MBR>{
	public static int MAX_NUMBER_OF_ENTRIES = 5;
	public static int MIN_NUMBER_OF_ENTRIES = 2;
	private int nEntry; //
	private long parentEntryID; //
	private long nodeID;  //
	private int isLeafInt=1;
	public int getIsLeafInt() {
		return isLeafInt;
	}
	
	public void addMBR(long childID, MBR mbr)
	{
		if(this.containsKey(childID))
		{
			
		}
		else
		{
			this.put(childID, mbr);
		}
			
	}

	public void setIsLeafInt(int isLeafInt) {
		this.isLeafInt = isLeafInt;
	}

	public long getNodeID() {
		return nodeID;
	}

	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}

	public long getParentEntryID() {
		return parentEntryID;
	}

	public void setParentEntryID(long parentEntryID) {
		this.parentEntryID = parentEntryID;
	}

	public long getParentNodeID() {
		return parentNodeID;
	}

	public void setParentNodeID(long parentNodeID) {
		this.parentNodeID = parentNodeID;
	}

	private long parentNodeID;

	public int getnEntry() {
		return nEntry;
	}

	public void setnEntry(int nEntry) {
		this.nEntry = nEntry;
	}

	private MBR[] mbrs;
	private long[] children;
	public boolean isLeaf() {
		return isLeafInt == 1 ? true : false;
	}
	public short add(MBR newMBR, long newChildID)
	{
		mbrs[nEntry] = newMBR;
		children[nEntry] = newChildID;
		nEntry++;
		return (short)(nEntry - 1);
	}
	public String toString()
	{
		return "["+nodeID+","+this.getParentNodeID()+","+this.getParentEntryID()+","+this.isLeaf()+","+nEntry+","+mbrs[0]+","+mbrs[1]+"]";
	}
	public RtreeIndexNode2() {
		nodeID = -1;
		parentNodeID = -1;
		parentEntryID = -1;
		nEntry = 0;
		mbrs = new MBR[MAX_NUMBER_OF_ENTRIES + 1];
		children = new long[MAX_NUMBER_OF_ENTRIES + 1];
		for(int i=0;i<children.length;++i)
		{
			children[i] =-1;
		}
		this.setIsLeafInt(1);
	}
	public long getChild(int i) {
		return children[i];
	}
	public void setMBR(int i, MBR mbr) {
		this.mbrs[i] = mbr;
	}
	public void setChild(int i, long child) {
		this.children[i] = child;
	}
	public MBR getMBR(int i) {
		return mbrs[i];
	}
	public MBR getCoveringMBR()
	{
		double lowerLeftX = -1;
		double lowerLeftY = -1;
		double upperRightX = -1;
		double upperRightY = -1;
		
		for(int i = 0; i < nEntry; i++)
		{
			if (mbrs[i] == null)
				continue;
			
			if (lowerLeftX < 0 || mbrs[i].getLowerLeftX() < lowerLeftX)
			{
				lowerLeftX = mbrs[i].getLowerLeftX();
			}
			
			if (lowerLeftY < 0 || mbrs[i].getLowerLeftY() < lowerLeftY)
			{
				lowerLeftY = mbrs[i].getLowerLeftY();
			}
			
			if (mbrs[i].getUpperRightX() > upperRightX)
			{
				upperRightX = mbrs[i].getUpperRightX();
			}
			
			if (mbrs[i].getUpperRightY() > upperRightY)
			{
				upperRightY = mbrs[i].getUpperRightY();
			}
		}
		
		return new MBR(lowerLeftX, lowerLeftY, upperRightX, upperRightY);
	}

}
