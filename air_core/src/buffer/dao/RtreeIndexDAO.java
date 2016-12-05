package buffer.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;

import lib.rtree.MBR;
import lib.rtree.RtreeIndexNode;

/**
 * 물류거점에 대한 R-tree 인덱스 테이블 객체
 * 
 * @author		박병권
 * @since       2014-08-03
 * @version     0.1       
 */
public class RtreeIndexDAO
{
	protected Logger	logger = Logger.getLogger(getClass()); // 로그 생성 객체
	private static RtreeIndexDAO table;
	private SqlMapClient sqlMap;// sql 저장 객체
	static
	{
		table = new RtreeIndexDAO();
	}
	
	public static RtreeIndexDAO getInstance()
	{
		return table;
	}
	
	public RtreeIndexDAO()
	{
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertRtreeIndexNode(RtreeIndexNode node) throws SQLException
	{
		logger.info("start");
		try
		{
			sqlMap.insert("tb_rtreeindex.insert",node);
		}catch(SqlMapException e)
		{
			this.updateRtreeIndexNode(node);
		}
			
	
	}
	
	public void deleteRtreeIndexNode(RtreeIndexNode node)
	{
		try {
			sqlMap.delete("tb_rtreeindex.delete",node);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Long getLastNodeID() throws SQLException
	{
		return (Long) sqlMap.queryForObject("tb_rtreeindex.selectLastNodeID");
		
	}
	
	public RtreeIndexNode getRtreeIndexNode(long nodeID)
	{
		RtreeIndexNode node=null;
		RtreeIndexNode parameter=new RtreeIndexNode();
		parameter.setNodeID(nodeID);
		try {
			node= (RtreeIndexNode) sqlMap.queryForObject("tb_rtreeindex.select",parameter);
			if(node==null)
				return null;
			
			MBR mbr0 = new MBR(node.getMbr1_x1(), node.getMbr1_y1(), node.getMbr1_x2(), node.getMbr1_y2());
			MBR mbr1 = new MBR(node.getMbr2_x1(), node.getMbr2_y1(), node.getMbr2_x2(), node.getMbr2_y2());
			MBR mbr2 = new MBR(node.getMbr3_x1(), node.getMbr3_y1(), node.getMbr3_x2(), node.getMbr3_y2());
			MBR mbr3 = new MBR(node.getMbr4_x1(), node.getMbr4_y1(), node.getMbr4_x2(), node.getMbr4_y2());
			MBR mbr4 = new MBR(node.getMbr5_x1(), node.getMbr5_y1(), node.getMbr5_x2(), node.getMbr5_y2());
			MBR mbr5 = new MBR(node.getMbr6_x1(), node.getMbr6_y1(), node.getMbr6_x2(), node.getMbr6_y2());
			node.setMBR(0, mbr0);
			node.setMBR(1, mbr1);
			node.setMBR(2, mbr2);
			node.setMBR(3, mbr3);
			node.setMBR(4, mbr4);
			node.setMBR(5, mbr5);
			node.setChild(0, node.getChildren1());
			node.setChild(1, node.getChildren2());
			node.setChild(2, node.getChildren3());
			node.setChild(3, node.getChildren4());
			node.setChild(4, node.getChildren5());
			node.setChild(5, node.getChildren6());
			/*node.setMBR(6, new MBR(node.getMbr7_x1(), node.getMbr7_y1(), node.getMbr7_x2(), node.getMbr7_y2()));
			node.setMBR(7, new MBR(node.getMbr8_x1(), node.getMbr8_y1(), node.getMbr8_x2(), node.getMbr8_y2()));*/
			
		}
		catch (SQLException e) 
		{
			 
			e.printStackTrace();
		};
		
		return node;
	}
	
	public boolean updateRtreeIndexNode(RtreeIndexNode node)
	{
		
		boolean result = true;
		
		try {
			int re=sqlMap.update("tb_rtreeindex.update",node);
			
			if(re==0)
				result=false;
			
		}
		catch (SQLException e) 
		{
			 
			e.printStackTrace();
		};
		return result;
	}
/*	public static void main(String[] args) throws SQLException {
		
		System.out.println("RtreeIndexTable Test....");
		RtreeIndexDAO indexTable =RtreeIndexDAO .getInstance();
		RtreeIndexNode node = new RtreeIndexNode();
		node.setNodeID(new Long(123).longValue());
		node.setParentNodeID(new Long(123).longValue());
		node.setMbr1(new Long(123).longValue());
		node.setChildren1(new Long(123).longValue());
		node.setnEntry(3);
		
		System.out.println();
		System.out.println("<==insertRtreeIndexNode==>");
		System.out.println();
		indexTable.insertRtreeIndexNode(node);
		
		System.out.println();		
		System.out.println("<==getRtreeIndexNode==>");
		System.out.println();
		RtreeIndexNode result = indexTable.getRtreeIndexNode(new Long(123).longValue());
		System.out.println("result:\n"+result);
		

		System.out.println();
		System.out.println("<==updateRtreeIndexNode==>");
		System.out.println();
		System.out.println("result:"+indexTable.updateRtreeIndexNode(node));
		
		System.out.println();		
		System.out.println("<==deleteRtreeIndexNode==>");
		System.out.println();
		indexTable.deleteRtreeIndexNode(node);
		
		
	}*/

	public void deleteRtreeIndexNode() {
		try {
			sqlMap.delete("tb_rtreeindex.deleteAll");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List getRtreeIndexNodes() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("tb_rtreeindex.select");
	}
}

