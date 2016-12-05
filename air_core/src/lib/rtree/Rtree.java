package lib.rtree;

import java.sql.SQLException;

import lib.geometry.Point;

import org.apache.log4j.Logger;

import spatial.LogisticsArea;
import buffer.dao.RtreeIndexDAO;
import buffer.dao.TableBufferManager;

/**
 * 공간 인덱스 R-tree를 관리하는 클래스
 * 
 * @author		박병권
 * @since       2014-02-04
 * @version     0.1       
 */
public class Rtree implements IFRTree 
{
	public static int ROOT_NODE_ID = 0;
	private static Rtree rtree;
	protected Logger	logger = Logger.getLogger(getClass()); // 로그 생성 객체
	private RtreeIndexDAO rtreeTable = RtreeIndexDAO.getInstance();
	private TableBufferManager bufferManager = TableBufferManager.getInstance(); 

	private long lastNodeID;

	private RtreeIndexNode root;

	/**
	 * Class constructor
	 * R-tree Instance를 여기서 생성함
	 */
	static
	{
		rtree = new Rtree();
	}

	/*
	 * R-tree 객체를 획득할 수 있는 클래스 메서드
	 */
	public static Rtree getInstance()
	{
		return rtree;
	}

	public Rtree()
	{	
		lastNodeID = 0;
	}
	/**
	 * @작성일 2015-05-06
	 * @param newMBR
	 * @param newLogisticsAreaID
	 * @throws SQLException 
	 */
	public void insert(MBR newMBR, long newLogisticsAreaID) throws SQLException
	{
		RtreeIndexNode leafNode = chooseLeaf(root, newMBR);
		insert(leafNode, newMBR, newLogisticsAreaID);
	}

	/**
	 * @작성일 2015-05-06
	 * @param node
	 * @param newMBR
	 * @param newLogisticsAreaID
	 * @throws SQLException 
	 */
	private void insert(RtreeIndexNode node, MBR newMBR, long newLogisticsAreaID) throws SQLException
	{
		logger.info("start");
		if (node.getNumberOfEntries() < RtreeIndexNode.MAX_NUMBER_OF_ENTRIES)
		{
			logger.info("nomal insert:"+newLogisticsAreaID);
			node.add(newMBR, newLogisticsAreaID);
			rtreeTable.updateRtreeIndexNode(node);
			adjustParent(node);
		}
		else
		{
			logger.info("split insert:"+newLogisticsAreaID);
			splitNreassign(node, newMBR, newLogisticsAreaID);
		}
		logger.info("end\n");
	}

	public void delete(MBR targetMBR, long targetLogisticsAreaID)
	{
		RtreeIndexNode leafNode = findLeaf(root, targetMBR);
		if (leafNode == null) return;

		leafNode.remove(targetLogisticsAreaID);

		if (leafNode.getNumberOfEntries() > 0)
		{
			rtreeTable.updateRtreeIndexNode(leafNode);
		}
		else
		{
			rtreeTable.deleteRtreeIndexNode(leafNode);
		}
		condenseParentNode(leafNode);
	}


	public LogisticsArea getNearestLogisticsArea(Point p) throws NullPointerException, SQLException
	{
		
		double minDistance1 = -1;
		double minDistance2 = -1;
		
		LogisticsArea minArea1 = getNearestArea(root, p);
		if (minArea1 != null)
		{
			minDistance1 = minArea1.shortestDistanceTo(p);
		}
		
		LogisticsArea minArea2 = getNearestArea(root, p, (int)minDistance1);
		if (minArea2 != null)
		{
			minDistance2 = minArea2.shortestDistanceTo(p);
		}

		if (minDistance1 < 0)
		{
			return minArea2;
		}
		else if (minDistance2 < 0)
		{
			return minArea1;
		}
		else if (minDistance1 < minDistance2)
		{
			return minArea1;
		}
		else
		{
			return minArea2;
		}
	}

	private LogisticsArea getNearestArea(RtreeIndexNode node, Point p) throws NullPointerException,SQLException
	{	
		int nEntry = node.getNumberOfEntries();
		MBR[] mbrs = node.getMBRs();
		long[] children = node.getChildren();
		int minDistance = -1;
		LogisticsArea minArea = null;
		// 리프 노드 이면
		if (node.isLeaf())
		{
			for(int i = 0; i < nEntry; i++)
			{
				LogisticsArea param = new LogisticsArea();
				param.setLocation_code(new Long(children[i]).toString());

				LogisticsArea area = (LogisticsArea) bufferManager.selectLogisticAreaInfo(param);

				int d = (int)area.shortestDistanceTo(p);
				if (minDistance < 0 || d < minDistance)
				{
					minDistance = d;
					minArea = area;
				}
			}
		}
		else // 리프 노드가 아니면
		{
			for(int i = 0; i < nEntry; i++)
			{
				if (mbrs[i].contain(p))
				{
					LogisticsArea area = getNearestArea(rtreeTable.getRtreeIndexNode(children[i]), p);
					int d = (int)area.shortestDistanceTo(p);
					if (minDistance < 0 || d < minDistance)
					{
						minDistance = d;
						minArea = area;
					}
				}
			}
		}
		return minArea;
	}

	private LogisticsArea getNearestArea(RtreeIndexNode node, Point p, int upperBoundDistance)
	{
		int nEntry = node.getNumberOfEntries();
		MBR[] mbrs = node.getMBRs();
		long[] children = node.getChildren();
		int minDistance = upperBoundDistance;
		LogisticsArea minArea = null;

		if (node.isLeaf())
		{
			for(int i = 0; i < nEntry; i++)
			{
				LogisticsArea parameter =new LogisticsArea();
				parameter.setLogisticsAreaID(children[i]);


				try {
					LogisticsArea area = bufferManager.selectLogisticAreaInfo(parameter);

					int d = (int)area.shortestDistanceTo(p);
					if (minDistance < 0 || d < minDistance)
					{
						minDistance = d;
						minArea = area;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			int minIndex = -1;
			for(int i = 0; i < nEntry; i++)
			{
				if (!mbrs[i].contain(p))
				{ 
					double d = mbrs[i].shortestDistanceTo(p);
					if (minDistance < 0 || d < minDistance)
					{
						minDistance = (int)d;
						minIndex = i;
					}
				}
			}
			minArea = getNearestArea(rtreeTable.getRtreeIndexNode(children[minIndex]), p, upperBoundDistance);
		}
		return minArea;
	}

	private RtreeIndexNode chooseLeaf(RtreeIndexNode node, MBR newMBR)
	{	
		logger.info("start");
		RtreeIndexNode leafNode = null;

		if (node.isLeaf())
		{			
			leafNode = node;
		}
		else
		{
			double minEnlargement = -1;
			int minIndex = 0;
			int nEntry = node.getNumberOfEntries();
			try{
				for(int i = 0; i < nEntry; i++)
				{
					if(node.getChild(i)==-1)
						continue;
					
					MBR mbr =node.getMBR(i);
					logger.info("node id:"+node.getNodeID()+", mbr:"+mbr);
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
				}

				// 재귀적 용법으로 리프 노드 탐색
				RtreeIndexNode searchNode =rtreeTable.getRtreeIndexNode(node.getChild(minIndex));			
				leafNode = chooseLeaf(searchNode, newMBR);
			}catch(NullPointerException ee)
			{
				ee.printStackTrace();
			}
		}
		logger.info("end:"+leafNode);
		return leafNode;
	}

	/**
	 * 
	 * (2015-05-06) 수정
	 * @param node
	 * @param newMBR
	 * @param newChildID
	 * @throws SQLException 
	 */
	private void splitNreassign(RtreeIndexNode node, MBR newMBR, long newChildID) throws SQLException
	{
		logger.info("start");
		// node의 값을 초기화 함
		int nEntry 			= node.getNumberOfEntries();
		long[] children		= node.getChildren();
		MBR[] mbrs			= node.getMBRs();
		mbrs[nEntry] 		= newMBR;
		children[nEntry] 	= newChildID;
		nEntry++;
		node.setNumberOfEntries(0);
		long lastNodeID = getLastNodeID();
		if (node.getNodeID() == ROOT_NODE_ID)
		{
			lastNodeID++;
			MBR oldMbr[] = root.getMBRs();
			double mbr1_x1=root.getMbr1_x1();
			double mbr1_y1=root.getMbr1_y1();
			double mbr1_x2=root.getMbr1_x2();
			double mbr1_y2=root.getMbr1_y2();

			double mbr2_x1=root.getMbr2_x1();
			double mbr2_y1=root.getMbr2_y1();
			double mbr2_x2=root.getMbr2_x2();
			double mbr2_y2=root.getMbr2_y2();

			double mbr3_x1=root.getMbr3_x1();
			double mbr3_y1=root.getMbr3_y1();
			double mbr3_x2=root.getMbr3_x2();
			double mbr3_y2=root.getMbr3_y2();

			double mbr4_x1=root.getMbr4_x1();
			double mbr4_y1=root.getMbr4_y1();
			double mbr4_x2=root.getMbr4_x2();
			double mbr4_y2=root.getMbr4_y2();

			double mbr5_x1=root.getMbr5_x1();
			double mbr5_y1=root.getMbr5_y1();
			double mbr5_x2=root.getMbr5_x2();
			double mbr5_y2=root.getMbr5_y2();

			long child1 = root.getChildren1();
			long child2 = root.getChildren2();
			long child3 = root.getChildren3();
			long child4 = root.getChildren4();
			long child5 = root.getChildren5();

			root = new RtreeIndexNode();
			root.setNodeID(ROOT_NODE_ID);
			root.setParentNodeID(-1);
			root.setParentEntryID((short)-1);
			root.setLeaf(false);
			root.setNumberOfEntries((short)1);
			root.setChild(0, lastNodeID);

			root.setMbrs(oldMbr);
			rtreeTable.updateRtreeIndexNode(root);

			// 수정2015-05-12
			//차일드 노드 찾을 수 없음

			node.setNodeID(lastNodeID);
			node.setParentNodeID(ROOT_NODE_ID);
			node.setParentEntryID((short)0);
			rtreeTable.insertRtreeIndexNode(node);
		}

		RtreeIndexNode newNode = new RtreeIndexNode();

		newNode.setParentNodeID(node.getParentNodeID());
		newNode.setParentEntryID((short)-1);
		newNode.setLeaf(node.isLeaf());
		newNode.setNumberOfEntries((short)0);
		long newID = lastNodeID+1;
		newNode.setNodeID(newID);


		regroup(nEntry, mbrs, children, node, newNode);


		adjustParent(node);
		adjustChildren(node);
		adjustParent(newNode);
		adjustChildren(newNode);

		rtreeTable.insertRtreeIndexNode(newNode);
		rtreeTable.updateRtreeIndexNode(node);

		logger.info("end");

	}


	/**
	 * @작성일 2015-05-06
	 * @설명 parent node의 MBR을 조정함, 
	 *  	childNode가 새로은 node이면 parent node에 새로운 index entry를 만듬, 
	 *  	parent node가 full이면 parent node를 split 함
	 * 
	 * @param node
	 * @throws SQLException 
	 */
	private void adjustParent(RtreeIndexNode node) throws SQLException
	{	
		logger.info("start");
		long nodeID = node.getNodeID();
		if (nodeID == ROOT_NODE_ID) // root node
			return;
		MBR mbr = node.getCoveringMBR();
		RtreeIndexNode parentNode = rtreeTable.getRtreeIndexNode(node.getParentNodeID());
		short parentEntryID = node.getParentEntryID();

		if (parentEntryID < 0)
		{
			if (parentNode.getNumberOfEntries() < RtreeIndexNode.MAX_NUMBER_OF_ENTRIES)
			{
				parentNode.add(mbr, nodeID);
				rtreeTable.updateRtreeIndexNode(parentNode);
				adjustParent(parentNode);
			}
			else
			{
				splitNreassign(parentNode, mbr, nodeID);
			}
		}
		else
		{
			parentNode.setMBR(parentEntryID, mbr);
			rtreeTable.updateRtreeIndexNode(parentNode);
		}
		logger.info("end");
	}


	/**
	 * @작성일 2015-05-06
	 * @설명 모든 child node들의 parentNodeID와 parentEntryID를 조정함
	 * @param node
	 */
	private void adjustChildren(RtreeIndexNode node)
	{
		logger.info("start");
		long nodeID = node.getNodeID();
		int nEntry = node.getNumberOfEntries();
		long[] children = node.getChildren();
		for(int i = 0; i < nEntry; i++)
		{
			RtreeIndexNode childNode = rtreeTable.getRtreeIndexNode(children[i]);


			/*
			 * 오류
			 * node 의 childen 값이 -1을 가리킴 
			 * 정확하게 할당이되지 않는 것 같음
			 */
			if(childNode == null)
				continue;
			childNode.setParentNodeID(nodeID);
			childNode.setParentEntryID((short)i);
			rtreeTable.updateRtreeIndexNode(childNode);
		}
		logger.info("end");
	}


	/**
	 * @param nEntry
	 * @param mbrs
	 * @param children
	 * @param node1
	 * @param node2
	 */
	private void regroup(int nEntry, MBR[] mbrs, long[] children, RtreeIndexNode node1, RtreeIndexNode node2)
	{
		logger.info("start");

		pickSeedsNassign(nEntry, mbrs, children, node1, node2);
		for(int i = 2; i < nEntry; i++)
		{	
			pickNextNassign(nEntry, mbrs, children, node1, node2);
		}
		logger.info("end");
	}

	/* 
	 * X축과 Y축 모두에 대해 lowest UpperRight와 highest LowerLeft의 gap이 가장 큰 pair를 seeds로 선정한 후
	 * node1과 node2에 각각 할당
	 */
	private void pickSeedsNassign(int nEntry, MBR[] mbrs, long[] children, RtreeIndexNode node1, RtreeIndexNode node2)
	{
		logger.debug("start");
		double maxSeparation = 0;
		double lowestSide = -1;
		double highestSide = -1;
		double lowestHighSide = -1;
		double highestLowSide = -1;
		int lowestHighSideIndexX = -1;
		int highestLowSideIndexX = -1;
		for(int i = 0; i < nEntry; i++)
		{
			if (children[i] < 0) continue;

			if (lowestSide < 0 || mbrs[i].getLowerLeftX() < lowestSide)
			{
				lowestSide = mbrs[i].getLowerLeftX();
			}

			if (mbrs[i].getUpperRightX() > highestSide)
			{
				highestSide = mbrs[i].getUpperRightX();
			}

			if (lowestHighSide < 0 || mbrs[i].getUpperRightX() < lowestHighSide)
			{
				lowestHighSide = mbrs[i].getUpperRightX();
				lowestHighSideIndexX = i;
			}

			if (mbrs[i].getLowerLeftX() > highestLowSide)
			{
				highestLowSide = mbrs[i].getLowerLeftX();
				highestLowSideIndexX = i;
			}
		}

		maxSeparation = Math.abs((highestLowSide - lowestHighSide) / (highestSide - lowestSide));
		lowestSide = -1;
		highestSide = -1;
		lowestHighSide = -1;
		highestLowSide = -1;
		int lowestHighSideIndexY = -1;
		int highestLowSideIndexY = -1;
		for(int i = 0; i < nEntry; i++)
		{
			if (children[i] < 0) continue;

			if (lowestSide < 0 || mbrs[i].getLowerLeftY() < lowestSide)
			{
				lowestSide = mbrs[i].getLowerLeftY();
			}

			if (mbrs[i].getUpperRightY() > highestSide)
			{
				highestSide = mbrs[i].getUpperRightY();
			}

			if (lowestHighSide < 0 || mbrs[i].getUpperRightY() < lowestHighSide)
			{
				lowestHighSide = mbrs[i].getUpperRightY();
				lowestHighSideIndexY = i;
			}

			if (mbrs[i].getLowerLeftY() > highestLowSide)
			{
				highestLowSide = mbrs[i].getLowerLeftY();
				highestLowSideIndexY = i;
			}
		}

		if (maxSeparation > Math.abs(((highestLowSide - lowestHighSide) / (highestSide - lowestSide))))
		{
			if (lowestHighSideIndexX == highestLowSideIndexX)
			{
				highestLowSideIndexX = lowestHighSideIndexX == 0 ? nEntry - 1 : 0;
			}

			node1.add(mbrs[lowestHighSideIndexX], children[lowestHighSideIndexX]);
			node2.add(mbrs[highestLowSideIndexX], children[highestLowSideIndexX]);

		//	children[lowestHighSideIndexX] = -1;
		//	children[highestLowSideIndexX] = -1;
		}
		else
		{
			if (lowestHighSideIndexY == highestLowSideIndexY)
			{
				highestLowSideIndexY = lowestHighSideIndexY == 0 ? nEntry - 1 : 0;
			}
			// 수정




			node1.add(mbrs[lowestHighSideIndexY], children[lowestHighSideIndexY]);
			node2.add(mbrs[highestLowSideIndexY], children[highestLowSideIndexY]);

			//children[lowestHighSideIndexY] = -1;
		//	children[highestLowSideIndexY] = -1;
		}
		logger.info("end");
	}

	/*
	 * 모든 MBR에 대해 node1에 포함되었을 경우의 면적 증가분 d1과 node2에 포함되었을 경우의 면적 증가분 d2를 구한 후
	 * |d1 - d2|의 크기가 가장 큰 MBR을 node1이나 node2에 할당
	 */
	private void pickNextNassign(int nEntry, MBR[] mbrs, long[] children, RtreeIndexNode node1, RtreeIndexNode node2)
	{
		logger.info("node1:"+node1.getNodeID()+", node2:"+node2.getNodeID());
		double lowerLeftX1 = -1;
		double lowerLeftY1 = -1;
		double upperRightX1 = -1;
		double upperRightY1 = -1;
		double areaOfNode1 = 0;

		double lowerLeftX2 = -1;
		double lowerLeftY2 = -1;
		double upperRightX2 = -1;
		double upperRightY2 = -1;
		double areaOfNode2 = 0;

		int nEntryOfNode = node1.getNumberOfEntries();
		MBR[] mbrsOfNode = node1.getMBRs();


		for(int i = 0; i < nEntryOfNode; i++)
		{
			// 추가 소스
			if(mbrsOfNode[i]==null)
				continue;


			if (lowerLeftX1 < 0 || mbrsOfNode[i].getLowerLeftX() < lowerLeftX1)
			{
				try{
					lowerLeftX1 = mbrsOfNode[i].getLowerLeftX();
				}catch(NullPointerException e)
				{
					e.printStackTrace();
				}
			}
			if (lowerLeftY1 < 0 || mbrsOfNode[i].getLowerLeftY() < lowerLeftY1)
			{
				lowerLeftY1 = mbrsOfNode[i].getLowerLeftY();
			}
			if (mbrsOfNode[i].getUpperRightX() > upperRightX1)
			{
				upperRightX1 = mbrsOfNode[i].getUpperRightX();
			}
			if (mbrsOfNode[i].getUpperRightY() > upperRightY1)
			{
				upperRightY1 = mbrsOfNode[i].getUpperRightY();
			}
		}
		areaOfNode1 = (upperRightX1 - lowerLeftX1) * (upperRightY1 - lowerLeftY1);

		nEntryOfNode = node2.getNumberOfEntries();
		mbrsOfNode = node2.getMBRs();
		for(int i = 0; i < nEntryOfNode; i++)
		{
			if (lowerLeftX2 < 0 || mbrsOfNode[i].getLowerLeftX() < lowerLeftX2)
			{
				lowerLeftX2 = mbrsOfNode[i].getLowerLeftX();
			}
			if (lowerLeftY2 < 0 || mbrsOfNode[i].getLowerLeftY() < lowerLeftY2)
			{
				lowerLeftY2 = mbrsOfNode[i].getLowerLeftY();
			}
			if (mbrsOfNode[i].getUpperRightX() > upperRightX2)
			{
				upperRightX2 = mbrsOfNode[i].getUpperRightX();
			}
			if (mbrsOfNode[i].getUpperRightY() > upperRightY2)
			{
				upperRightY2 = mbrsOfNode[i].getUpperRightY();
			}
		}
		areaOfNode1 = (upperRightX1 - lowerLeftX1) * (upperRightY1 - lowerLeftY1);

		double maxAreaDiff = -1;
		int selectedIndex = -1;
		RtreeIndexNode selectedNode = null;
		for(int i = 0; i < nEntry; i++)
		{
			if (children[i] < 0) continue;

			double newAreaOfNode1 = 
					((mbrs[i].getUpperRightX() > upperRightX1 ? mbrs[i].getUpperRightX() : upperRightX1) -
					 (mbrs[i].getLowerLeftX() < lowerLeftX1 ? mbrs[i].getLowerLeftX() : lowerLeftX1)) *
					((mbrs[i].getUpperRightY() > upperRightY1 ? mbrs[i].getUpperRightY() : upperRightY1) -
					 (mbrs[i].getLowerLeftY() < lowerLeftY1 ? mbrs[i].getLowerLeftY() : lowerLeftY1));
			
			double d1 = newAreaOfNode1 - areaOfNode1;

			double newAreaOfNode2 = 
					((mbrs[i].getUpperRightX() > upperRightX2 ? mbrs[i].getUpperRightX() : upperRightX2) -
					 (mbrs[i].getLowerLeftX()  < lowerLeftX2 ? mbrs[i].getLowerLeftX() : lowerLeftX2)) *
					((mbrs[i].getUpperRightY() > upperRightY2 ? mbrs[i].getUpperRightY() : upperRightY2) -
					 (mbrs[i].getLowerLeftY()  < lowerLeftY2 ? mbrs[i].getLowerLeftY() : lowerLeftY2));
			
			double d2 = newAreaOfNode2 - areaOfNode2;

			if (Math.abs(d1 - d2) > maxAreaDiff)
			{
				maxAreaDiff = Math.abs(d1 - d2);
				selectedIndex = i;
				if (d1 < d2)
				{
					selectedNode = node1;
				}
				else
				{
					selectedNode = node2;
				}
			}
		}
		selectedNode.add(mbrs[selectedIndex], children[selectedIndex]);
		mbrs[selectedIndex] = null;
		children[selectedIndex] = -1;
	}

	private long getLastNodeID()
	{

		try {
			lastNodeID = rtreeTable.getLastNodeID();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return lastNodeID;
	}

	private RtreeIndexNode findLeaf(RtreeIndexNode node, MBR targetMBR)
	{
		logger.info("start");
		RtreeIndexNode leafNode = null;
		if (node.isLeaf())
		{
			leafNode = node;
		}
		else
		{

			int nEntry = node.getNumberOfEntries();
			for(int i = 0; i < nEntry; i++)
			{
				//창현 수정
				if(node.getMBR(i)== null)
					continue;

				if (node.getMBR(i).contain(targetMBR))
				{
					leafNode = findLeaf(rtreeTable.getRtreeIndexNode(node.getChild(i)), targetMBR);
					break;
				}
			}
		}
		logger.info("end");
		return leafNode; 
	}

	/**
	 * @param childNode
	 */
	private void condenseParentNode(RtreeIndexNode childNode)
	{
		RtreeIndexNode parentNode = rtreeTable.getRtreeIndexNode(childNode.getParentNodeID());
		short parentEntryID = childNode.getParentEntryID();
		if (childNode.getNumberOfEntries() > 0)
		{
			MBR childMBR = childNode.getCoveringMBR();
			parentNode.setMBR(parentEntryID, childMBR);
			rtreeTable.updateRtreeIndexNode(parentNode);
		}
		else
		{
			parentNode.remove(parentEntryID, childNode.getNodeID());
			if (parentNode.getNumberOfEntries() > 0)
			{
				rtreeTable.updateRtreeIndexNode(parentNode);
			}
			else
			{
				rtreeTable.deleteRtreeIndexNode(parentNode);
			}
		}
		if (parentNode.getNodeID() != ROOT_NODE_ID)
		{
			condenseParentNode(parentNode);
		}
	}

	/**
	 *@설명 루트 노드 초기화 
	 */
	public void initRoot()
	{
		logger.debug("root 노드 초기화");
		rtreeTable.deleteRtreeIndexNode();
		RtreeIndexNode node = new RtreeIndexNode();
		node.setNodeID(ROOT_NODE_ID);
		node.setLeaf(true);
		node.setnEntry(0);
		node.setParentEntryID((short)-1);
		node.setParentNodeID(-1);
		node.setChildren1(-1);
		node.setChildren2(-1);
		node.setChildren3(-1);
		node.setChildren4(-1);
		node.setChildren5(-1);		
		rtreeTable.updateRtreeIndexNode(node);
		root = rtreeTable.getRtreeIndexNode(ROOT_NODE_ID);
	}

}