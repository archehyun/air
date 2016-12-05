package api;


import info.ActionInfo;
import info.ConditionInfo;
import info.XMLEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.ProcessingInstruction;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class AIR_XMLManager {

	private static final String TEMP_UPPER = "temp_upper";
	private static final String TEMP_LOWER = "temp_lower";
	private static final String INFORM = "inform";
	private static final String ACTION = "action";
	private static final String CONDITION = "condition";
	private static final String REGISTER_QUERY = "registerQuery";
	private static final String QID = "qid";
	private static final String QUERY = "query";
	private static final String ROOKUP_RESULT = "rookupResult";
	private static final String UPPER_BOUND = "upper_bound";
	private static final String LOWWER_BOUND = "lowwer_bound";
	private static final String TID = "tid";
	private static final String CID = "cid";
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
	public void delCondition(ConditionInfo info)
	{
		if(conditionMap.containsKey(info.getConditionType()))
		{
			conditionMap.remove(info.getConditionType());
		}
	}
	public String getValue(String xml,String child)
	{

		String value="";
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new InputSource(new StringReader(xml)));
			Element xmlRoot = doc.getRootElement();

			Element query = xmlRoot.getChild(QUERY);
			value = query.getChild(child).getValue();

		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}  




		return value;



	}
	/**
	 * @return
	 */
	public String lookUpResultXML() {
		Document document = new Document(); 

		Element root = new Element(ROOKUP_RESULT);  
		Element condition = new Element(QUERY);
		Element qid = new Element(QID);
		Element cid = new Element(TID);
		Element tid = new Element(CID);


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
	/**
	 * @param entity
	 * @return
	 */
	public String registerQueryXML(XMLEntity entity) {

		Element root = new Element(REGISTER_QUERY);  
		Element condition = new Element(CONDITION);
		Element action = new Element(ACTION);
		Element inform = new Element(INFORM);

		root.addContent(condition);
		root.addContent(action);
		root.addContent(inform);


		if(entity.getCid()!=null)
		{

			Element cid = new Element(CID);
			cid.setText(entity.getCid());			
			condition.addContent(cid);
			inform.setAttribute(CID,"true");
		}else
		{
			inform.setAttribute(CID,"false");
		}

		if(entity.getTid()!=null)
		{
			Element tid = new Element(TID);
			tid.setText(entity.getTid());
			condition.addContent(tid);
			inform.setAttribute(TID,"true");
		}else
		{
			inform.setAttribute(TID,"false");
		}


		if(entity.getTemp_lower()!=null||entity.getTemp_upper()!=null)
		{
			Element lowbound = new Element(TEMP_LOWER);
			lowbound.setText(entity.getTemp_lower());			
			Element upperbound = new Element(TEMP_UPPER);
			upperbound.setText(entity.getTemp_upper());
			inform.setAttribute(TID,"true");
		}
		

		Document document = new Document(); 
		document.setContent(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("EUC-KR");
		outputter.setFormat(format);
		return outputter.outputString(document);
	}
	/**
	 * @return
	 */
	public String registerQueryXML() {


		//XML 문서 객체 생성  

		Document document = new Document(); 

		Element root = new Element(REGISTER_QUERY);  
		Element condition = new Element(CONDITION);
		Element action = new Element(ACTION);
		Element inform = new Element(INFORM);
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
			Element con = new Element(item.getConditionType());


			if(item.getConditionType().equals(CID))
			{
				con.setText(item.getCid());
			}else if(item.getConditionType().equals(TID)){
				con.setText(item.getTid());
			}else
			{
				Element min =new Element(LOWWER_BOUND);
				Element max =new Element(UPPER_BOUND);

				min.setText(String.valueOf(item.getMin()));
				max.setText(String.valueOf(item.getMax()));


				con.addContent(min);
				con.addContent(max);
			}


			condition.addContent(con);

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
	/*	private void addElement(boolean isCondition, String eName,String value,Element parent)
	{
		if(isCondition)
		{
			Element acction = new Element(eName);
			acction.setText(value);
			parent.addContent(acction);
		}
	}*/
	public static void main(String[] args) {
		/*	AIR_XMLManager manager = new AIR_XMLManager();
		ConditionInfo info = new ConditionInfo(CONDITION.TEMPERATURE);
		info.setMin(15);
		info.setMax(0);
		ConditionInfo info2 = new ConditionInfo(CONDITION.HUMIDITY);
		info2.setMin(15);
		info2.setMax(0);

		manager.addCondition(info);
		manager.addCondition(info2);
		System.out.println(manager.registerQueryXML());

		 */
	}
	public String queryIDList(List li) {
		Document document = new Document(); 

		Element root = new Element("queryIDList");
		Iterator<ActionInfo> iter = li.iterator();

		while(iter.hasNext())
		{
			ActionInfo item =iter.next();

			Element qid = new Element(QID);
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

	public QueryBundle resultPaser(String result) throws JDOMException, IOException
	{
		
		QueryBundle bundle = new QueryBundle();
		
		SAXBuilder saxBuilder = new SAXBuilder();

		Document document = saxBuilder.build(new ByteArrayInputStream(result.getBytes()));

		Element root = document.getRootElement();
		
		List list=root.getChildren("query");
		
		Iterator iter = list.iterator();
		while(iter.hasNext())
		{
			
			
			Element query = (Element) iter.next();
			String qid=query.getChild("qid").getValue();
			String cid=query.getChild("cid").getValue();
			String tid=query.getChild("tid").getValue();
			String user = query.getChild("user").getValue();
			int temperature=Integer.parseInt(query.getChild("temperature").getValue());
			int humidity=Integer.parseInt(query.getChild("humidity").getValue());
			double latitude= Double.parseDouble(query.getChild("latitude").getValue());
			double longitude=Double.parseDouble(query.getChild("longitude").getValue());
			int door = Integer.parseInt(query.getChild("door").getValue());
			
			QueryEntity entity = new QueryEntity(qid,cid,tid,user,temperature,humidity,latitude,longitude,door);
			
			bundle.addQueryResult(entity);
		}

		
		return bundle;


	}

	public static void demo(Document doc) {

		List children = doc.getContent();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof Element) {
				demo((Element) o);
			}
			else if (o instanceof Comment)
				doComment((Comment) o);
			else if (o instanceof ProcessingInstruction) 
				doPI((ProcessingInstruction) o);
		}
	} 


	public static void doComment(Comment c) {
		System.out.println("Comment: " + c);
	}
	public static void doPI(ProcessingInstruction pi) {
		System.out.println("PI: " + pi);
	}
	public static void demo(Element element) {
		System.out.println("Element " + element);

		List attributes = element.getAttributes();
		List children = element.getContent();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o instanceof Element) {
				demo((Element) o);
			}
			else if (o instanceof Comment) 
				doComment((Comment)o);
			else if (o instanceof ProcessingInstruction) 
				doPI((ProcessingInstruction)o);
			else if (o instanceof String) {
				System.out.println("String: " + o);
			}   
		}
	} 

}
