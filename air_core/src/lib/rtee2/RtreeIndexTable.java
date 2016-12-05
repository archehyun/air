package lib.rtee2;

import java.io.IOException;
import java.util.HashMap;

import lib.rtree.MBR;
import buffer.dao.SqlMapManager;

import com.ibatis.sqlmap.client.SqlMapClient;

public class RtreeIndexTable {
	private static RtreeIndexTable table;
	private SqlMapClient sqlMap;// sql ¿˙¿Â ∞¥√º
	static
	{
		table = new RtreeIndexTable();
	}
	
	public static RtreeIndexTable getInstance()
	{
		return table;
	}
	
	public RtreeIndexTable()
	{
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public RtreeIndexNode2 getRtreeIndexNode(long nodeID)
	{
		
		RtreeIndexNode2 parameter=new RtreeIndexNode2();
		parameter.setNodeID(nodeID);
		try {
			HashMap<String, Object> map = (HashMap) sqlMap.queryForObject("tb_rtreeindex.selectMap",parameter);
			RtreeIndexNode2 node= new RtreeIndexNode2();
			node.setNodeID(new Double((double) map.get("nodeID")).longValue());
			node.setParentNodeID(new Double((double) map.get("nodeID")).longValue());
			node.setParentEntryID(new Double((double) map.get("parentNodeID")).longValue());
			node.setIsLeafInt((int)map.get("isLeafInt"));
			node.setnEntry((int)map.get("nEntry"));
			for(int i=0;i<RtreeIndexNode2.MAX_NUMBER_OF_ENTRIES;i++)
			{
				node.setMBR(i, new MBR((double)map.get("mbr"+(i+1)+"_x1"), (double)map.get("mbr"+(i+1)+"_y1"), (double)map.get("mbr"+(i+1)+"_x2"),(double)map.get("mbr"+(i+1)+"_y1")));
				node.setChild(i, new Double((double) map.get("children"+(i+1))).longValue());
			}			
			return node; 
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		RtreeIndexNode2 map=new RtreeIndexTable().getRtreeIndexNode(0);
		System.out.println(map);
	}
}
