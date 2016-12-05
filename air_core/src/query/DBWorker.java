package query;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import msg.node.InboundMsgForData;

import org.apache.log4j.Logger;

import buffer.dao.TableBufferManager;
import buffer.info.ActionInfo;
import buffer.info.ConditionInfo;
import buffer.info.TagInfo;
import buffer.info.TagUserInfo;

public class DBWorker {
	
	public DBWorker() throws SQLException {
		actionList = bufferManager.selectListActionInfo();
	}
	
	protected Logger 			logger = Logger.getLogger(getClass());
	/**	  
	 * �׼� ���̺� �ش� ����� ���̵��� ���Ǹ� Ž���ؼ� ��ȯ
	 * 
	 * @���� ���̺� TB_TAG, TB_ACTION
	 * @param userList ���� ���̵� ����Ʈ
	 * @return qid List
	 * @throws SQLException
	 */	
	private TableBufferManager bufferManager=TableBufferManager.getInstance(); // ���۸޴���
	private List<ActionInfo> actionList;
	public List<ActionInfo> getActionList() {
		return actionList;
	}

	private	List<TagUserInfo> userList =null;
	
	
	public String[] extractQID(String tid) throws SQLException {
		
		TableBufferManager bufferManager = TableBufferManager.getInstance();
		ConditionInfo op=new ConditionInfo(ConditionInfo.CONTAINER);
		
		StringBuffer newTid =new StringBuffer();
		byte ar[] = new byte[8];
		for(int i=0;i<ar.length;i++)
		{
			
			newTid.append(Integer.parseInt(tid.substring(i*2,i*2+2),16));
		}
		
		
		op.setTid(newTid.toString());
		List li=bufferManager.selectConditionInfoListByTagID(op);
		Iterator<ConditionInfo> iter = li.iterator();
		LinkedList<String> qidList = new LinkedList<String>();// ���(�±� ���̵𿡸� ������ ����Ʈ
		while(iter.hasNext())
		{
			ConditionInfo item =iter.next();
			qidList.add(item.getQuery_number());
			
		}

		String list[] = new String[qidList.size()];
		StringBuffer qidLog = new StringBuffer();		
		
		for(int i=0;i<list.length;i++)
		{
			list[i] =  qidList.get(i);
			qidLog.append(list[i]+(i<list.length-1?",":""));
		}
		
		
		return list;
	}
	
	public String[] extractQID(List<TagUserInfo> userList) throws SQLException {

		logger.debug("start");

		Iterator<TagUserInfo> iter = userList.iterator();

		LinkedList<String> qidList = new LinkedList<String>();// ���(�±� ���̵𿡸� ������ ����Ʈ

		/*
		 * �׼� ��� ��ü�� ���Ͽ� ����ھ��̵�� ������ �׼ǿ� ���ؼ� ���� ���̵� ����
		 * 
		 */
		
		
		
		

		while(iter.hasNext())
		{
			TagInfo item =iter.next();
			String userID=item.getUser_id();
			for(int i=0;i<actionList.size();i++)
			{
				ActionInfo actionItem = (ActionInfo) actionList.get(i);
				if(actionItem.getUser_id()==null)
					continue;
				// �׼� ���̺� ��ϵ� ����� ���̵�� ���� ���� ���̵� ���� ���ǹ�ȣ ����� ��ȸ
				if(actionItem.getUser_id().equals(userID))
				{
					qidList.add(actionItem.getQuery_number());
				}
			}
		}

		String list[] = new String[qidList.size()];
		StringBuffer qidLog = new StringBuffer();		
		
		for(int i=0;i<list.length;i++)
		{
			list[i] =  qidList.get(i);
			qidLog.append(list[i]+(i<list.length-1?",":""));
		}
		return list;
	}
	/**
	 * �±׷� ���� ���۵� �޽����� �±� ���̵� �̿��Ͽ� ���� ����� ���� ��ȸ
	 * 
	 * @param message �±� �޼���
	 * @return �±� ����� ����
	 * @throws SQLException
	 */
	public List<TagUserInfo> matchingUserList(InboundMsgForData message) throws SQLException {

		logger.info("start");
		
		try{
			// ���ŵ� �±� �����ͼ� �±׾��̵�� ������ �±׾��̵� ���� ����� ���̵� ��ȸ
			TagUserInfo info = new TagUserInfo();
			
			StringBuffer newTid =new StringBuffer();
			byte ar[] = new byte[8];
			for(int i=0;i<ar.length;i++)
			{
				
				newTid.append(Integer.parseInt(message.getTid().substring(i*2,i*2+2),16));
			}
			
			
			info.setTid(newTid.toString());
			
			userList=TableBufferManager.getInstance().selectListTagUserInfo(info);
			System.out.println(newTid.toString()+" matching user:"+userList);			

		}catch(NullPointerException e)
		{
			e.printStackTrace();
			throw new NullAIRMessageException(e.getMessage());
		}catch(Exception ee)
		{
			ee.printStackTrace();
		}
		logger.debug("end");
		return userList;
	}

}
