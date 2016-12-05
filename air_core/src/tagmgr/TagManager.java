package tagmgr;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;

import msg.node.InboundControlMsgForActivationPermissionACK;
import msg.node.InboundControlMsgForActivationRequest;
import msg.node.InboundControlMsgForHeartbeat;
import msg.node.InboundMsg;
import msg.node.InboundMsgForIPChange;
import msg.node.QueueNode;
import msg.node.TagControlMsg;
import msg.node.TagControlMsgForActivationPermission;
import msg.node.TagControlMsgForDistanceConditionChange;
import msg.node.TagControlMsgForQueryConditionChange;
import msg.queue.OutboundMsgQueue;
import msg.queue.TagMsgQueue;
import server.AIRThread;
import buffer.dao.TableBufferManager;
import buffer.info.TagInfo;


/**
 * @author 박창현
 *
 */
public class TagManager extends AIRThread implements Runnable{

	private static final String ACTIVATINO2 = "2";

	private static final String ACTIVATION = "1";

	private TagMsgQueue tagMsgQueue = TagMsgQueue.getInstance();
	
	private OutboundMsgQueue outboundMsgQueue 			= OutboundMsgQueue.getInstance();
	
	private HashMap<String, String> tagList =  new HashMap<String, String>();// 태그 목록(태그 아이디, 태그 아이피)
	
	public HashMap<String, String> getTagList() {
		return tagList;
	}

	private static TagManager tagManager;
	
	private TagManager() {

	}
	public void run()
	{
		while(isStarted)
		{
			QueueNode message=tagMsgQueue.poll();
			

			if(message instanceof TagControlMsg)
			{
				tagControlProcess((TagControlMsg) message);
			}
			
			else if(message instanceof TagControlMsgForDistanceConditionChange) //0x21->0x41
			{
				logger.info("distance update");
				
				TagControlMsgForDistanceConditionChange msg = (TagControlMsgForDistanceConditionChange) message;

				msg.setTagIPaddr(this.tagList.get(msg.getTid()));				

				outboundMsgQueue.append(msg);
			}
			
			else if(message instanceof InboundMsg)
			{
				try {
					inboundControlProcess(message);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private TableBufferManager bufferManager = TableBufferManager.getInstance();

	/**
	 * @param inboundNode
	 * @throws UnknownHostException
	 */
	private void inboundControlProcess(QueueNode inboundNode) throws UnknownHostException, SQLException
	{
		logger.info("tagmanager inboundControlProcess start");	
		
		// case 1 ActivationRequest
		if(inboundNode instanceof InboundControlMsgForActivationRequest) //0x21->0x41
		{
			final InboundControlMsgForActivationRequest item = (InboundControlMsgForActivationRequest) inboundNode;
			
			
			TagInfo param=new TagInfo();
			param.setTid(item.getTid());
			TagInfo searchTag = (TagInfo) bufferManager.selectTagInfo(param);
			
			// 기존 태그 가 없을때
			String tagID = item.getTid();
			
			logger.info("search Tag:"+searchTag);
			if(searchTag==null)
			{
				logger.error("not exist tag id:"+tagID);
			}
			// 기존 태그가 있을 때
			else
			{
				if(!tagList.containsKey(tagID))
				{
					logger.info("tagManager add ip to list=>tid:"+item.getTid()+",ip:"+item.getTagIPaddr());

					this.tagList.put(tagID, item.getTagIPaddr());
				}
				
				if(searchTag.getIs_activate().equals("0"))
				{
					
					TagInfo updateItem = new TagInfo();

					updateItem.setTid(tagID);

					updateItem.setIs_activate(ACTIVATION);
					
					logger.info("InboundControlMsgForActivationRequest: update tag Database table\n");
					
					int resultCount=(int) bufferManager.updateTagInfo(updateItem);

					if(resultCount==0)
					{				
						logger.error("not exist tag id:"+tagID);
					}
					else
					{						
						logger.info("activation request ack:"+tagID);
						
						outboundMsgQueue.append(item);					
						
						/*
						 * 질의 조건 전송
						 */
						
						short lowerBoundTemperature=0;
						short upperBoundTemperature=0;
						short lowerBoundHumidity=0;
						short upperBoundHumidity=0;
						short upperBoundHit=0;
						byte door=TagControlMsgForQueryConditionChange.DOOR_OPEN;
						int noticeInterval=0;
						int sensingInterval=0;
						boolean add= false;
						
						TagControlMsgForQueryConditionChange conditionChangeMessage = new TagControlMsgForQueryConditionChange(tagID, "", 
								lowerBoundTemperature, 
								upperBoundTemperature, 
								lowerBoundHumidity,
								upperBoundHumidity, 
								upperBoundHit, 
								door, 
								noticeInterval, 
								sensingInterval, 
								add);
						
						conditionChangeMessage.setTagIPaddr(item.getTagIPaddr());
						
						outboundMsgQueue.append(conditionChangeMessage);
						
						
						/*
						 * 거리 조건 전송
						 * 
						 */
						
						
						int movingDistance=10;// 수정 예정
						
						TagControlMsgForDistanceConditionChange distanceConditionChangeMessage = new TagControlMsgForDistanceConditionChange(tagID, "", movingDistance);
						
						distanceConditionChangeMessage.setTagIPaddr(item.getTagIPaddr());
						
						outboundMsgQueue.append(distanceConditionChangeMessage);
						
						/*new Thread(){
							
							public void run()
							{
								try {
									Thread.sleep(1000);
									
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
						}.start();*/
						
					}
				}
			}


		}
		// case 2 Heartbeat
		else if(inboundNode instanceof InboundControlMsgForHeartbeat) //0x47
		{
			InboundControlMsgForHeartbeat item = (InboundControlMsgForHeartbeat) inboundNode;

			String tagID = item.getTid();

			float battery = item.getBattery();
			
			if(item.getTagIPaddr()==null)
				
			item.setTagIPaddr(this.tagList.get(tagID));

			outboundMsgQueue.append(item);

			logger.info("InboundControlMsgForHeartbeat: update tag Database table");

		}
		// case 3 IPChange
		else if(inboundNode instanceof InboundMsgForIPChange) //0x48
		{
			InboundMsgForIPChange item = (InboundMsgForIPChange) inboundNode;
			String tagID = item.getTid();

			if(tagList.containsKey(tagID))
			{
				tagList.put(tagID, item.getTagIPaddr());

				logger.debug("update tag ip:"+item.getTagIPaddr());
			}
			else
			{
				tagList.put(tagID, item.getTagIPaddr());

				logger.debug("insert tag ip:"+item.getTagIPaddr());
			}
			outboundMsgQueue.append(item);


		}
		else if(inboundNode instanceof InboundControlMsgForActivationPermissionACK) //0x48
		{
			logger.info("InboundControlMsgForActivationPermissionACK: update tag Database table");

			InboundControlMsgForActivationPermissionACK item = (InboundControlMsgForActivationPermissionACK) inboundNode;
			
			TagInfo op =  new TagInfo();
			
			op.setTid(item.getTid());
			
			op.setIs_activate(ACTIVATINO2);

			TableBufferManager.getInstance().updateTagInfo(op);

			item.setTagIPaddr(this.tagList.get(item.getTid()));

			outboundMsgQueue.append(item);


		}
	}

	/**태그 컨트롤 메세지 처리
	 * @param msgNode
	 */
	public void tagControlProcess(TagControlMsg msgNode)
	{
		
		String ip = this.tagList.get(msgNode.getTid());

		logger.debug(msgNode.getTid()+" set ip:"+ip);// 대소문자 구분 필요

		if(ip!=null)
		{
			msgNode.setTagIPaddr(ip);

			outboundMsgQueue.append(msgNode);

		}else
		{
			logger.error(msgNode.getTid()+"=>ip가 할당되어 있지 않습니다.");
		}

		if(msgNode instanceof TagControlMsgForActivationPermission) //0x21->0x41
		{
			logger.debug("tagControl:TagControlMsgForActivationPermission");
			// 메세지 전송
			TagControlMsgForActivationPermission msg = (TagControlMsgForActivationPermission) msgNode;

			String tagip = this.tagList.get(msg.getTid());
			
			TableBufferManager bufferManager  = TableBufferManager.getInstance();
			
			TagInfo parameter = new TagInfo();
			
			parameter.setTid(msg.getTid());
			
			try {
				logger.info("tag info update:"+msg.getTid());
				parameter.setIs_activate(ACTIVATINO2);
				bufferManager.updateTagInfo(parameter);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			if(tagip!=null)
			{
				msg.setTagIPaddr(tagip);

				outboundMsgQueue.append(msg);

			}else
			{
				logger.error(msg.getTid()+"=>ip가 할당되어 있지 않습니다.");
			}
		}
		
		
		if(msgNode instanceof TagControlMsgForDistanceConditionChange) //0x21->0x41
		{
			logger.info("distance update");
			
			TagControlMsgForDistanceConditionChange msg = (TagControlMsgForDistanceConditionChange) msgNode;

			msg.setTagIPaddr(this.tagList.get(msg.getTid()));				

			outboundMsgQueue.append(msg);
		}
		
/*
		if(msgNode instanceof TagControlMsgForQueryConditionChange) //0x21->0x41
		{
			TagControlMsgForQueryConditionChange msg = (TagControlMsgForQueryConditionChange)msgNode;		

			msg.setTagIPaddr(this.tagList.get(msg.getTid()));

			outboundMsgQueue.append(msg);
		}

		

		if(msgNode instanceof TagControlMsgForActuatorControl) //0x21->0x41
		{
			TagControlMsgForActuatorControl msg = (TagControlMsgForActuatorControl) msgNode;

			msg.setTagIPaddr(this.tagList.get(msg.getTid()));

			outboundMsgQueue.append(msg);
		}*/
	}


	public static TagManager getInstance() {
		if(tagManager==null)
			tagManager = new TagManager();
		return tagManager;
	}

	@Override
	public void serverStart() {
		if(thread== null)
		{
			
			isStarted=true;
			thread = new Thread(this);  
			thread.start();
		}
		
	}

	@Override
	public void serverStop() {
		
		isStarted=false;
		thread = null;
		
	}

}
