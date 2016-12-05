package lib.rtee2;

import lib.rtree.MBR;

import org.apache.log4j.Logger;


public class RtreeNew {
	public RtreeNew() {
		initRoot();
	}
	protected Logger	logger = Logger.getLogger(getClass());
	private RtreeIndexNode2 root;
	public void insert(MBR newMBR, long newLogisticsAreaID)
	{
		RtreeIndexNode2 leafNode=chooseLeaf(root,newMBR);
		if (leafNode.getnEntry() < RtreeIndexNode2.MAX_NUMBER_OF_ENTRIES)
		{
			
		}
	}
	
	/**
	 * @설명 root노드 탐색
	 * @param node
	 * @param newMBR
	 * @return
	 */
	private RtreeIndexNode2 chooseLeaf(RtreeIndexNode2 node,MBR newMBR)
	{
		logger.info("start");
		RtreeIndexNode2 leafNode = null;
		if (node.isLeaf())
		{			
			//root
			leafNode = node;
			logger.info("root:"+leafNode);
		}
		else
		{
			
			double minEnlargement = -1;
			int minIndex = 0;
			int nEntry = node.getnEntry();
			for(int i = 0; i < nEntry; i++)
			{
				MBR mbr =node.getMBR(i);
				double e = mbr.getEnlargement(newMBR);
				if (minEnlargement < 0)
				{
					minEnlargement = e;
					minIndex = i;
				}
				else if (e < minEnlargement)
				{
					minEnlargement = e;
					minIndex = i;
				}
				else if (e == minEnlargement && node.getMBR(i).getArea() < node.getMBR(minIndex).getArea())
				{
					minEnlargement = e;
					minIndex = i;
				}
				logger.info("minIndex:"+minIndex);
				leafNode = chooseLeaf(rtreeTable.getRtreeIndexNode(node.getChild(minIndex)), newMBR);	
			}
			
			
		}
		logger.info("end");
		return leafNode;
	}
	public static int ROOT_NODE_ID = 0;
	private RtreeIndexTable rtreeTable;
	private void initRoot()
	{
		
		rtreeTable = RtreeIndexTable.getInstance();
		root = rtreeTable.getRtreeIndexNode(ROOT_NODE_ID);
		logger.info("초기화:"+root);
	}
	public static void main(String[] args) {
		RtreeNew rtree = new RtreeNew();
		MBR mbr1 = new MBR(126.123, 35.231, 126.112, 35.321);
		rtree.insert(mbr1, 1);
	}
}
