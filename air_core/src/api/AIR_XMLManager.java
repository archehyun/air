package api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import buffer.info.ActionInfo;
import buffer.info.ConditionInfo;

public class AIR_XMLManager {

	HashMap<String, ConditionInfo> conditionMap;
	public AIR_XMLManager() {
		conditionMap = new HashMap<String, ConditionInfo>();
	}
	public void addCondition(ConditionInfo info)
	{
		if(conditionMap.containsKey(info.getConditionType()))
		{
			System.err.println("key error");	
		}else
		{
			conditionMap.put(info.getConditionType(), info);

		}
	}
	/**
	 * @return
	 */
	public String lookUpResultXML() {
		Document document = new Document(); 

		Element root = new Element("rookupResult");  
		Element condition = new Element("query");
		Element qid = new Element("qid");
		Element cid = new Element("tid");
		Element tid = new Element("cid");


		root.addContent(condition);
		condition.addContent(qid);
		condition.addContent(cid);
		condition.addContent(tid);	


		document.setContent(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("EUC-KR");
		outputter.setFormat(format);
		return outputter.outputString(document);
	}
	public void clear()
	{
		conditionMap.clear();
	}
	public String registerQueryXML() {

		//XML 문서 객체 생성  

		Document document = new Document(); 

		Element root = new Element("registerQuery");  
		Element condition = new Element("condition");
		Element action = new Element("action");
		Element inform = new Element("inform");
		root.addContent(condition);
		root.addContent(action);

		Set keyset = conditionMap.keySet();
		Iterator<String> iter = keyset.iterator();

		while(iter.hasNext())
		{
			String keyItem = iter.next();
			ConditionInfo item =conditionMap.get(keyItem);

			if(item == null)
			{
				System.err.println("null:"+keyItem);
			}
			//condition.addContent(item.getXMLElement());
			inform.setAttribute(item.getConditionType(),"true");
		}

		action.addContent(inform);

		//addElement(isSMS, "sms", sms, action);
		//addElement(isMessage, "message", message, action);
		//addElement(isControl, "control", control, action);



		document.setContent(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("EUC-KR");
		outputter.setFormat(format);
		return outputter.outputString(document);

	}


	/**
	 * @param li
	 * @return
	 */
	public String queryIDList(List li) {
		Document document = new Document(); 

		Element root = new Element("queryIDList");
		Iterator<ActionInfo> iter = li.iterator();

		while(iter.hasNext())
		{

			ActionInfo item =iter.next();

			Element qid = new Element("qid");
			qid.setText(item.getQuery_number());
			root.addContent(qid);

		}

		document.setContent(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("EUC-KR");
		outputter.setFormat(format);
		return outputter.outputString(document);
	}


}
