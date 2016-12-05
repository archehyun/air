package api;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RegisterQueryXML {

	private static RegisterQueryXML registerQueryXML; // RegisterQueryXML ∞¥√º ¿ŒΩ∫≈œΩ∫
	
	static {
		registerQueryXML = new RegisterQueryXML();
	}
	
	private RegisterQueryEntity entity; // RegisterQuery ∞¥√º
	
	private RegisterQueryXML() {
		entity = new RegisterQueryEntity();
	}
	
	public RegisterQueryEntity xmlParser(String query) throws NullPointerException{
		try {
			DocumentBuilder domParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlDoc = domParser.parse(new InputSource(new StringReader(query)));
			NodeList conditionElements = xmlDoc.getElementsByTagName("registerQuery");
			
			Element registerElement =	(Element) conditionElements.item(0);						
			Element tid 			=	(Element) registerElement.getElementsByTagName("tid").item(0);						
			Element cid 			=	(Element) registerElement.getElementsByTagName("cid").item(0);			
			Element temp_lower		=	(Element) registerElement.getElementsByTagName("temp_lower").item(0);
			Element temp_upper		=	(Element) registerElement.getElementsByTagName("temp_upper").item(0);
			Element humid_lower		=	(Element) registerElement.getElementsByTagName("humid_lower").item(0);
			Element humid_upper		=	(Element) registerElement.getElementsByTagName("humid_upper").item(0);
			Element hit_upper		=	(Element) registerElement.getElementsByTagName("hit_upper").item(0);
			Element door			=	(Element) registerElement.getElementsByTagName("door").item(0);
			Element notice			=	(Element) registerElement.getElementsByTagName("notice").item(0);
			Element sensing			=	(Element) registerElement.getElementsByTagName("sensing").item(0);
			Element location_code	=	(Element) registerElement.getElementsByTagName("location_code").item(0);
			Element operator		=	(Element) registerElement.getElementsByTagName("operator").item(0);
			Element phone			=	(Element) registerElement.getElementsByTagName("phone").item(0);
			Element message			=	(Element) registerElement.getElementsByTagName("message").item(0);
			Element switchNo		=	(Element) registerElement.getElementsByTagName("switch").item(0);
			Element switchButton	=	(Element) registerElement.getElementsByTagName("onoff").item(0);			
			Element inform			=	(Element) registerElement.getElementsByTagName("inform").item(0);
			
			entity.setTid(tid.getTextContent());
			entity.setCid(cid.getTextContent());
			entity.setTemp_lower(temp_lower.getTextContent());
			entity.setTemp_upper(temp_upper.getTextContent());
			entity.setHumid_lower(humid_lower.getTextContent());
			entity.setHumid_upper(humid_upper.getTextContent());
			entity.setHit_upper(hit_upper.getTextContent());
			entity.setDoor(door.getTextContent());
			entity.setNotice(notice.getTextContent());
			entity.setSensing(sensing.getTextContent());
			entity.setLocation_code(location_code.getTextContent());
			entity.setOperator(operator.getTextContent());
			entity.setPhone(phone.getTextContent());
			entity.setMessage(message.getTextContent());
			entity.setSwitchNo(switchNo.getTextContent());
			entity.setSwitchButton(switchButton.getTextContent());
			
			
			entity.setInfo_tid(inform.getAttribute("tid"));
			entity.setInfo_cid(inform.getAttribute("cid"));
			entity.setInfo_temp(inform.getAttribute("temperature"));
			entity.setInfo_humid(inform.getAttribute("humidity"));
			entity.setInfo_hit(inform.getAttribute("hit"));
			entity.setInfo_location(inform.getAttribute("location"));
			entity.setInfo_latitude(inform.getAttribute("latitude"));
			entity.setInfo_longitude(inform.getAttribute("longitude"));
			entity.setInfo_door(inform.getAttribute("door"));
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return entity;
	}
	
	public static RegisterQueryXML getInstance() {
		return registerQueryXML;
	}
}
