package lib.rtree;


/**
 * @author archehyun
 *
 */
public class RtreeIndexNode 
{
	public static int MAX_NUMBER_OF_ENTRIES = 5;
	public static int MIN_NUMBER_OF_ENTRIES = 2;

	private long[] children;
	private long children1=-1;
	private long children10=-1;
	private long children11=-1;
	private long children12=-1;
	private long children13=-1;
	private long children14=-1;
	private long children15=-1;
	private long children2=-1;
	private long children3=-1;
	private long children4=-1;
	private long children5=-1;
	private long children6=-1;
	private long children7=-1;
	private long children8=-1;
	private long children9=-1;
	private boolean isLeaf;
	private int isLeafInt=1;
	private long mbr1=-1;	
	private double mbr1_x1=-1;

	private double mbr1_x2=-1;

	private double mbr1_y1=-1;

	private double mbr1_y2=-1;

	private long mbr10=-1;

	private long mbr11=-1;

	private long mbr12=-1;

	private long mbr13=-1;

	private long mbr14=-1;

	private long mbr15=-1;

	private long mbr2=-1;

	private double mbr2_x1=-1;

	private double mbr2_x2=-1;

	private double mbr2_y1=-1;

	private double mbr2_y2=-1;

	private long mbr3=-1;

	private double mbr3_x1=-1;

	private double mbr3_x2=-1;

	private double mbr3_y1=-1;

	private double mbr3_y2=-1;

	private long mbr4=-1;

	private double mbr4_x1=-1;

	private double mbr4_x2=-1;

	private double mbr4_y1=-1;

	private double mbr4_y2=-1;

	private long mbr5=-1;

	private double mbr5_x1=-1;

	private double mbr5_x2=-1;

	private double mbr5_y1=-1;

	private double mbr5_y2=-1;

	private long mbr6=-1;

	private double mbr6_x1=-1;

	private double mbr6_x2=-1;

	private double mbr6_y1=-1;

	private double mbr6_y2=-1;

	private long mbr7=-1;

	private double mbr7_x1=-1;

	private double mbr7_x2=-1;

	private double mbr7_y1=-1;

	private double mbr7_y2=-1;
	private long mbr8=-1;
	private double mbr8_x1=-1;
	
	private double mbr8_x2=-1;
	private double mbr8_y1=-1;
	private double mbr8_y2=-1;
	private long mbr9=-1;
	private MBR[] mbrs;
	public void setMbrs(MBR[] mbrs) {
		this.mbrs = mbrs;
	}
	private int nEntry;
	private long nodeID;
	private short parentEntryID;
	private long parentNodeID;
	public RtreeIndexNode()
	{
		nodeID = -1;
		parentNodeID = -1;
		parentEntryID = -1;
		isLeaf = true;
		nEntry = 0;
		
		mbrs = new MBR[MAX_NUMBER_OF_ENTRIES + 1];		
		children = new long[MAX_NUMBER_OF_ENTRIES + 1];
		
		for(int i=0;i<children.length;++i)
		{
			children[i] =-1;
		}
	}
	public RtreeIndexNode(long nodeID, long parentNodeID, short parentEntryID, boolean isLeaf, short nEntry, MBR[] mbrs, long[] children)
	{
		this.nodeID = nodeID;
		this.parentNodeID = parentNodeID;
		this.parentEntryID = parentEntryID;
		this.isLeaf = isLeaf;
		this.nEntry = nEntry;
		this.mbrs = mbrs;
		this.children = children;
	}
	// 수정 2014-11-20
	public short add(MBR newMBR, long newChildID)
	{
		mbrs[nEntry] = newMBR;
		
		switch (nEntry) {
		case 0:
			this.setMbr1_x1(newMBR.getUpperRightX());
			this.setMbr1_y1(newMBR.getUpperRightY());
			this.setMbr1_x2(newMBR.getLowerLeftX());
			this.setMbr1_y2(newMBR.getLowerLeftY());
			break;
		case 1:
			this.setMbr2_x1(newMBR.getUpperRightX());
			this.setMbr2_y1(newMBR.getUpperRightY());
			this.setMbr2_x2(newMBR.getLowerLeftX());
			this.setMbr2_y2(newMBR.getLowerLeftY());
			break;
		case 2:
			this.setMbr3_x1(newMBR.getUpperRightX());
			this.setMbr3_y1(newMBR.getUpperRightY());
			this.setMbr3_x2(newMBR.getLowerLeftX());
			this.setMbr3_y2(newMBR.getLowerLeftY());
			break;
		case 3:
			this.setMbr4_x1(newMBR.getUpperRightX());
			this.setMbr4_y1(newMBR.getUpperRightY());
			this.setMbr4_x2(newMBR.getLowerLeftX());
			this.setMbr4_y2(newMBR.getLowerLeftY());
			break;
		case 4:
			this.setMbr5_x1(newMBR.getUpperRightX());
			this.setMbr5_y1(newMBR.getUpperRightY());
			this.setMbr5_x2(newMBR.getLowerLeftX());
			this.setMbr5_y2(newMBR.getLowerLeftY());
			break;
		case 5:
			this.setMbr6_x1(newMBR.getUpperRightX());
			this.setMbr6_y1(newMBR.getUpperRightY());
			this.setMbr6_x2(newMBR.getLowerLeftX());
			this.setMbr6_y2(newMBR.getLowerLeftY());
			break;
		case 6:
			this.setMbr7_x1(newMBR.getUpperRightX());
			this.setMbr7_y1(newMBR.getUpperRightY());
			this.setMbr7_x2(newMBR.getLowerLeftX());
			this.setMbr7_y2(newMBR.getLowerLeftY());
			break;	
		case 7:
			this.setMbr8_x1(newMBR.getUpperRightX());
			this.setMbr8_y1(newMBR.getUpperRightY());
			this.setMbr8_x2(newMBR.getLowerLeftX());
			this.setMbr8_y2(newMBR.getLowerLeftY());
			break;				
			
		default:
			break;
		}
		children[nEntry] = newChildID;
		nEntry++;
		return (short)(nEntry - 1);
	}
	/*public void add(MBR newMBR, long newChildID)
	{
		mbrs[nEntry] = newMBR;
		children[nEntry] = newChildID;
		nEntry++;
	}*/
	public long getChild(int i) {
		return children[i];
	}
	public long[] getChildren()
	{
		return children;
	}
	public long getChildren1() {
		return children[0];
	}
	public long getChildren10() {
		return children10;
	}
	public long getChildren11() {
		return children11;
	}
	public long getChildren12() {
		return children12;
	}
	public long getChildren13() {
		return children13;
	}
	public long getChildren14() {
		return children14;
	}
	public long getChildren15() {
		return children15;
	}	
	public long getChildren2() {
		return children[1];
	}
	public long getChildren3() {
		return children[2];
	}
	public long getChildren4() {
		return children[3];
	}

	public long getChildren5() {
		return children[4];
	}

	public long getChildren6() {
		return children6;
	}

	public long getChildren7() {
		return children7;
	}

	public long getChildren8() {
		return children8;
	}

	public long getChildren9() {
		return children9;
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

	public int getIsLeafInt() {
		return isLeafInt;
	}
	public MBR getMBR(int i) {
		return mbrs[i];
	}
	public long getMbr1() {
		return mbr1;
	}
	public double getMbr1_x1() {
		return mbr1_x1;
	}	
	public double getMbr1_x2() {
		return mbr1_x2;
	}

	public double getMbr1_y1() {
		return mbr1_y1;
	}

	public double getMbr1_y2() {
		return mbr1_y2;
	}

	public long getMbr10() {
		return mbr10;
	}

	public long getMbr11() {
		return mbr11;
	}

	public long getMbr12() {
		return mbr12;
	}

	public long getMbr13() {
		return mbr13;
	}

	public long getMbr14() {
		return mbr14;
	}
	public long getMbr15() {
		return mbr15;
	}
	public long getMbr2() {
		return mbr2;
	}
	public double getMbr2_x1() {
		return mbr2_x1;
	}	
	public double getMbr2_x2() {
		return mbr2_x2;
	}

	public double getMbr2_y1() {
		return mbr2_y1;
	}

	public double getMbr2_y2() {
		return mbr2_y2;
	}

	public long getMbr3() {
		return mbr3;
	}

	public double getMbr3_x1() {
		return mbr3_x1;
	}

	public double getMbr3_x2() {
		return mbr3_x2;
	}

	public double getMbr3_y1() {
		return mbr3_y1;
	}

	public double getMbr3_y2() {
		return mbr3_y2;
	}

	public long getMbr4() {
		return mbr4;
	}

	public double getMbr4_x1() {
		return mbr4_x1;
	}

	public double getMbr4_x2() {
		return mbr4_x2;
	}

	public double getMbr4_y1() {
		return mbr4_y1;
	}

	public double getMbr4_y2() {
		return mbr4_y2;
	}

	public long getMbr5() {
		return mbr5;
	}

	public double getMbr5_x1() {
		return mbr5_x1;
	}

	public double getMbr5_x2() {
		return mbr5_x2;
	}

	public double getMbr5_y1() {
		return mbr5_y1;
	}

	public double getMbr5_y2() {
		return mbr5_y2;
	}

	public long getMbr6() {
		return mbr6;
	}

	public double getMbr6_x1() {
		return mbr6_x1;
	}

	public double getMbr6_x2() {
		return mbr6_x2;
	}

	public double getMbr6_y1() {
		return mbr6_y1;
	}

	public double getMbr6_y2() {
		return mbr6_y2;
	}

	public long getMbr7() {
		return mbr7;
	}

	public double getMbr7_x1() {
		return mbr7_x1;
	}

	public double getMbr7_x2() {
		return mbr7_x2;
	}

	public double getMbr7_y1() {
		return mbr7_y1;
	}

	public double getMbr7_y2() {
		return mbr7_y2;
	}

	public long getMbr8() {
		return mbr8;
	}

	public double getMbr8_x1() {
		return mbr8_x1;
	}

	public double getMbr8_x2() {
		return mbr8_x2;
	}

	public double getMbr8_y1() {
		return mbr8_y1;
	}

	public double getMbr8_y2() {
		return mbr8_y2;
	}

	public long getMbr9() {
		return mbr9;
	}

	public MBR[] getMBRs()
	{		
		return mbrs;
	}

	public int getnEntry() {
		return nEntry;
	}

	public long getNodeID() {
		return nodeID;
	}

	public int getNumberOfEntries() {
		return nEntry;
	}
	public short getParentEntryID() {
		return parentEntryID;
	}
	public long getParentNodeID() {
		return parentNodeID;
	}
	
	// 수정
	public boolean isLeaf() {
		return isLeafInt == 1 ? true : false;
	}

	public void remove(int targetIndex, long targetChildID)
	{
		for(int i = targetIndex; i < (nEntry - 1); i++)
		{
			mbrs[i] = mbrs[i+1];
			children[i] = children[i+1];
		}
		nEntry--;
	}

	public void remove(long targetChildID)
	{
		int targetIndex = -1;
		for(int i = 0; i < nEntry; i++)
		{
			if (children[i] == targetChildID)
			{
				targetIndex = i;
				break;
			}
		}
		for(int i = targetIndex; i < (nEntry - 1); i++)
		{
			mbrs[i] = mbrs[i+1];
			children[i] = children[i+1];
		}
		nEntry--;
	}

	public void setChild(int index, long child) {
		this.children[index] = child;
	}

	public void setChildren(long[] children)
	{
		this.children = children;
	}

	public void setChildren1(long children1) {
		this.children[0] = children1;
	}

	public void setChildren10(long children10) {
		this.children10 = children10;
	}

	public void setChildren11(long children11) {
		this.children11 = children11;
	}

	public void setChildren12(long children12) {
		this.children12 = children12;
	}

	public void setChildren13(long children13) {
		this.children13 = children13;
	}

	public void setChildren14(long children14) {
		this.children14 = children14;
	}

	public void setChildren15(long children15) {
		this.children15 = children15;
	}

	public void setChildren2(long children2) {
		this.children[1] = children2;
	}

	public void setChildren3(long children3) {
		this.children[2] = children3;
	}

	public void setChildren4(long children4) {
		this.children[3] = children4;
	}

	public void setChildren5(long children5) {
		this.children[4] = children5;
	}

	public void setChildren6(long children6) {
		this.children6 = children6;
	}

	public void setChildren7(long children7) {
		this.children7 = children7;
	}

	public void setChildren8(long children8) {
		this.children8 = children8;
	}

	public void setChildren9(long children9) {
		this.children9 = children9;
	}

	public void setIsLeafInt(int isLeafInt) {
		this.isLeafInt = isLeafInt;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeafInt = isLeaf ? 1 : 0;		
	}

	public void setMBR(int i, MBR mbr) {
		this.mbrs[i] = mbr;
	}

	public void setMbr1(long mbr1) {
		this.mbr1 = mbr1;
	}

	public void setMbr1_x1(double mbr1_x1) {
		this.mbr1_x1 = mbr1_x1;
	}

	public void setMbr1_x2(double mbr1_x2) {
		this.mbr1_x2 = mbr1_x2;
	}
	public void setMbr1_y1(double mbr1_y1) {
		this.mbr1_y1 = mbr1_y1;
	}
	public void setMbr1_y2(double mbr1_y2) {
		this.mbr1_y2 = mbr1_y2;
	}
	public void setMbr10(long mbr10) {
		this.mbr10 = mbr10;
	}
	public void setMbr11(long mbr11) {
		this.mbr11 = mbr11;
	}
	public void setMbr12(long mbr12) {
		this.mbr12 = mbr12;
	}
	public void setMbr13(long mbr13) {
		this.mbr13 = mbr13;
	}
	public void setMbr14(long mbr14) {
		this.mbr14 = mbr14;
	}
	public void setMbr15(long mbr15) {
		this.mbr15 = mbr15;
	}
	public void setMbr2(long mbr2) {
		this.mbr2 = mbr2;
	}

	public void setMbr2_x1(double mbr2_x1) {
		this.mbr2_x1 = mbr2_x1;
	}
	public void setMbr2_x2(double mbr2_x2) {
		this.mbr2_x2 = mbr2_x2;
	}
	public void setMbr2_y1(double mbr2_y1) {
		this.mbr2_y1 = mbr2_y1;
	}
	public void setMbr2_y2(double mbr2_y2) {
		this.mbr2_y2 = mbr2_y2;
	}
	public void setMbr3(long mbr3) {
		this.mbr3 = mbr3;
	}
	public void setMbr3_x1(double mbr3_x1) {
		this.mbr3_x1 = mbr3_x1;
	}
	public void setMbr3_x2(double mbr3_x2) {
		this.mbr3_x2 = mbr3_x2;
	}
	
	public void setMbr3_y1(double mbr3_y1) {
		this.mbr3_y1 = mbr3_y1;
	}

	public void setMbr3_y2(double mbr3_y2) {
		this.mbr3_y2 = mbr3_y2;
	}
	public void setMbr4(long mbr4) {
		this.mbr4 = mbr4;
	}
	public void setMbr4_x1(double mbr4_x1) {
		this.mbr4_x1 = mbr4_x1;
	}
	public void setMbr4_x2(double mbr4_x2) {
		this.mbr4_x2 = mbr4_x2;
	}
	public void setMbr4_y1(double mbr4_y1) {
		this.mbr4_y1 = mbr4_y1;
	}

	public void setMbr4_y2(double mbr4_y2) {
		this.mbr4_y2 = mbr4_y2;
	}

	public void setMbr5(long mbr5) {
		this.mbr5 = mbr5;
	}
	public void setMbr5_x1(double mbr5_x1) {
		this.mbr5_x1 = mbr5_x1;
	}
	
	public void setMbr5_x2(double mbr5_x2) {
		this.mbr5_x2 = mbr5_x2;
	}
	
	public void setMbr5_y1(double mbr5_y1) {
		this.mbr5_y1 = mbr5_y1;
	}

	public void setMbr5_y2(double mbr5_y2) {
		this.mbr5_y2 = mbr5_y2;
	}

	public void setMbr6(long mbr6) {
		this.mbr6 = mbr6;
	}
	
	public void setMbr6_x1(double mbr6_x1) {
		this.mbr6_x1 = mbr6_x1;
	}

	public void setMbr6_x2(double mbr6_x2) {
		this.mbr6_x2 = mbr6_x2;
	}
	
	public void setMbr6_y1(double mbr6_y1) {
		this.mbr6_y1 = mbr6_y1;
	}

	public void setMbr6_y2(double mbr6_y2) {
		this.mbr6_y2 = mbr6_y2;
	}

	public void setMbr7(long mbr7) {
		this.mbr7 = mbr7;
	}

	public void setMbr7_x1(double mbr7_x1) {
		this.mbr7_x1 = mbr7_x1;
	}

	public void setMbr7_x2(double mbr7_x2) {
		this.mbr7_x2 = mbr7_x2;
	}
	
	public void setMbr7_y1(double mbr7_y1) {
		this.mbr7_y1 = mbr7_y1;
	}

	public void setMbr7_y2(double mbr7_y2) {
		this.mbr7_y2 = mbr7_y2;
	}
	
	public void setMbr8(long mbr8) {
		this.mbr8 = mbr8;
	}

	public void setMbr8_x1(double mbr8_x1) {
		this.mbr8_x1 = mbr8_x1;
	}
	
	public void setMbr8_x2(double mbr8_x2) {
		this.mbr8_x2 = mbr8_x2;
	}

	public void setMbr8_y1(double mbr8_y1) {
		this.mbr8_y1 = mbr8_y1;
	}
	
	public void setMbr8_y2(double mbr8_y2) {
		this.mbr8_y2 = mbr8_y2;
	}

	public void setMbr9(long mbr9) {
		this.mbr9 = mbr9;
	}

	/*public void setMBRs(MBR[] mbrs) {
		this.mbrs = mbrs;
	}*/
	
	public void setnEntry(int nEntry) {
		this.nEntry = nEntry;
	}
	
	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}
	
	public void setNumberOfEntries(int nEntry) {
		this.nEntry = nEntry;
	}
	
	public void setParentEntryID(short parentEntryID) {
		this.parentEntryID = parentEntryID;
	}
	public void setParentNodeID(long parentNodeID) {
		this.parentNodeID = parentNodeID;
	}
	public String toString()
	{
		return "[nodeID:"+nodeID+",parentNodeID:"+parentNodeID+",parentEntryID:"+parentEntryID+",isLeafInt:"+isLeafInt+",nEntry:"+nEntry+"]";
	
	}
}
