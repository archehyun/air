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
	 * 액셩 테이블 해당 사용자 아이디의 질의만 탐색해서 반환
	 * 
	 * @관련 테이블 TB_TAG, TB_ACTION
	 * @param userList 쿼리 아이디 리스트
	 * @return qid List
	 * @throws SQLException
	 */	
	private TableBufferManager bufferManager=TableBufferManager.getInstance(); // 버퍼메니저
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
		LinkedList<String> qidList = new LinkedList<String>();// 결과(태그 아이디에를 저장할 리스트
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

		LinkedList<String> qidList = new LinkedList<String>();// 결과(태그 아이디에를 저장할 리스트

		/*
		 * 액션 목록 전체에 대하여 사용자아이디로 동일한 액션에 대해서 질의 아이디를 수집
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
				// 액션 테이블에 등록된 사용자 아이디와 같은 같은 아이디를 가진 질의번호 목록을 조회
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
	 * 태그로 부터 전송된 메시지에 태그 아이디를 이용하여 관련 사용자 정보 조회
	 * 
	 * @param message 태그 메세지
	 * @return 태그 사용자 정보
	 * @throws SQLException
	 */
	public List<TagUserInfo> matchingUserList(InboundMsgForData message) throws SQLException {

		logger.info("start");
		
		try{
			// 수신된 태그 데이터서 태그아이디와 동일한 태그아이디를 가진 사용자 아이디를 조회
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
