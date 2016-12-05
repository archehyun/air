package query.executer;

import java.util.List;

import msg.node.InboundMsgForData;
import msg.node.MsgForAPI;
import msg.queue.MsgQueueForAPI;
import msg.queue.TagControlMsgQueue;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import query.IFActionExecuter;
import buffer.info.ActionInfo;


/**
 * 
 * 
 * @author		박창현
 * @since       2014-01-25
 * @version     0.1       
 */
public class ActionExecuter implements IFActionExecuter{


	Logger logger = Logger.getLogger(this.getClass());
	MsgQueueForAPI queue = MsgQueueForAPI.getInstance();
	TagControlMsgQueue tagQueue =TagControlMsgQueue.getInstance();
	Element root;
	XMLOutputter outputter;
	public ActionExecuter() {
		document = new Document(); 
		outputter = new XMLOutputter();
	}

	/**
	 * @param actionList2 
	 * @param queryResult
	 * @return
	 */
	public String IF_QueryAction(List<ActionInfo> actionList, int[][] queryResult,InboundMsgForData data,String userID)
	{
		return this.createXMLResult(queryResult, actionList,data,userID);
	}
	public void execute(MsgForAPI api)
	{		
		//TODO 사용자별 질의 결과 전송
		//TODO 목적별 질의 결과 전송
		
		//loger.debug(acitonResult);
		queue.append(api);
		/*if(true)// 수정예정
		{
			
			// 1. 질의 아이디별 사용자 조회
			// 2. 메세지 전달 목적지 지정
			// 3. 목적지 별 해당 큐에 저장
			MsgForAPI api = new MsgForAPI("user1", 0, acitonResult);
				
		}*/
	}
	public MsgForAPI createMsg(String userID,int queryID,String xml)
	{
		MsgForAPI api = new MsgForAPI(userID, queryID, xml);
		return api;
	}

	public boolean checkResult(int result[])
	{
		for(int i=0;i<result.length;i++)
		{
			if(result[i]==1)
				return true;
		}
		return false;
	}
	Document document;
	/**
	 * @param result
	 * @param actionList
	 * @param data
	 * @return xml결과
	 */
	public String createXMLResult(int[][] result, List<ActionInfo> actionList, InboundMsgForData data,String userID)
	{
		document.removeContent(); 

		root = new Element("rookupResult");

		for(int i=0;i<result[0].length;i++)
		{
			if(result[1][i]==1)
			{
				
				for(int j=0;j<actionList.size();j++)
				{
					
					if((result[0][i]==Integer.parseInt(actionList.get(j).getQuery_number())&&
						userID.equals(actionList.get(j).getUser_id())))
					{
						Element condition = new Element("query");
						Element qid = new Element("qid");
						qid.setText(actionList.get(j).getQuery_number());
						Element cid = new Element("cid");
						cid.setText(data.getCid());
						Element tid = new Element("tid");
						tid.setText(data.getTid());
						Element user = new Element("user");
						user.setText(actionList.get(j).getUser_id());
						
						Element temperature = new Element("temperature");
						temperature.setText(String.valueOf(data.getTemperature()));
						Element humidity = new Element("humidity");
						humidity.setText(String.valueOf(data.getHumidity()));
						Element latitude = new Element("latitude");
						latitude.setText(String.valueOf(data.getLatitude()));
						Element longitude = new Element("longitude");
						longitude.setText(String.valueOf(data.getLongitude()));
						
						Element door = new Element("door");
						door.setText(String.valueOf(data.getDoor()));

						root.addContent(condition);
						condition.addContent(qid);
						condition.addContent(cid);
						condition.addContent(tid);
						condition.addContent(user);
						condition.addContent(temperature);
						condition.addContent(humidity);
						condition.addContent(latitude);
						condition.addContent(longitude);
						condition.addContent(door);
					}
				}
			}
		}



		document.setContent(root);
		
		Format format = Format.getPrettyFormat();
		format.setEncoding("EUC-KR");
		outputter.setFormat(format);
		// 힙 메모리 오류 발생
		String results=outputter.outputString(document);
		//logger.info(results);
		return results;
	}

}
