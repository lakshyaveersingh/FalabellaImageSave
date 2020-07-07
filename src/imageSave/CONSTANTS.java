package imageSave;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CONSTANTS {

	public final static String DRIVER_CLASS = getValueByNodeName("config.xml", "driverClass");
	public final static String CONNECTION_URL = getValueByNodeName("config.xml", "connectionUrl");
	public final static String DB_USERNAME = getValueByNodeName("config.xml", "db_UserName");
	public final static String DB_PASSWORD = getValueByNodeName("config.xml", "db_Password");
	public final static String DB_NAME = getValueByNodeName("config.xml", "db_Name");
	public final static String DELEMITER = "||";
	public final static String DELEMITER_ARROW = "->"; 
	
	public static String getValueByNodeName(String xmlFilePath, String tagName)
	{
		String value="";
		try {
			File xmlFile = new File(xmlFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			org.w3c.dom.NodeList nList = doc.getElementsByTagName("value");
			Node nNode = nList.item(0);
			if(nNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element eElement = (Element) nNode;
				value = eElement.getElementsByTagName(tagName).item(0).getTextContent();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
}
