package com.protocol;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.blespp.client.DeviceData;

import android.annotation.SuppressLint;
import android.util.Log;



public class XMLHelper {

	DocumentBuilder documentBuilder;

	public XMLHelper(){
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String sendDeviceInfo(String command, DeviceData data){

		/*<send_connection>
		 * <firmware_version>1.1</firmware_version>
		 * <hardware_version>1.1</hardware_version>
		 * <hcs_version>Mobile 0.1.0.2</hcs_version>
		 * </send_connection>
		 */

		DeviceData mData = DeviceData.instance();

		//		data = DeviceData.instance();

		Log.d("XMLHelper sendDeviceInfo", mData.s_version + "." + mData.s_version_minor + "Hardware" + mData.h_version + "." + mData.h_version_minor);

		String xmlString="";

		//Create a new Document object, using the newDocument() method of the DocumentBuilder class, with: 
		Document document = documentBuilder.newDocument();

		//Create the root element "mobilelogin" of the Document object using the createElement() method: 
		Element rootElement = document.createElement("send_connection");
		document.appendChild(rootElement);

		Element element1 = document.createElement("firmware_version");
		rootElement.appendChild(element1);
		element1.appendChild(document.createTextNode(mData.s_version + "." + mData.s_version_minor));

		Element element2 = document.createElement("hardware_version");
		rootElement.appendChild(element2);
		element2.appendChild(document.createTextNode(mData.h_version + "." + mData.h_version_minor));

		Element element3 = document.createElement("hcs_version");
		rootElement.appendChild(element3);
		//	element3.appendChild(document.createTextNode(mData.s_version+""));

		element3.appendChild(document.createTextNode("Mobile 0.1.0.2"));

		xmlString  = documentToXMLString(document);

		return xmlString;
	}

	public String sendDeviceData(String command, DeviceData data){

		/*
		 * <send_data>
		 * <user_activity_collection>
		 * <user_activity>
		 * <day>21/10/2013</day>
		 * <fork_value>T37408;OT;T133587;S;T133678;F;T133689;F;T133725;
		 * F;T133739;F;T133745;FO;T133770;F;T133784;F;T133789;FO;T133810;
		 * F;T133838;F;T133861;F;T133862;FO;T133902;F;T133917;F;T133952;
		 * F;T133959;FO;T133987;F;T133996;FO;T134013;F;T134029;F;T134039;FO;T134052;
		 * F;T134057;FO;T134082;F;T134083;FO;T134107;F;T134130;F;T134146;F;T134151;
		 * FO;T134184;F;T134189;FO;T134220;F;T134244;F;T134267;F;T134274;FO;T134277;
		 * FO;T134321;F;T134345;F;T134351;FO;T135258;OT;T149471;S;T149473;PB;T149473;PB;T149512;
		 * PB;T149566;PB;T149566;PB;T149572;F;T149604;PB;T149639;PB;T149639;PB;T149675;PB;T149732;PB;T149732;PB;
		 * </fork_value>
		 * </user_activity>
		 * </user_activity_collection>
		 * </send_data>
		 */

		String xmlString="";

		//Create a new Document object, using the newDocument() method of the DocumentBuilder class, with: 
		Document document = documentBuilder.newDocument();

		//Create the root element "mobilelogin" of the Document object using the createElement() method: 
		Element rootElement = document.createElement("send_data");
		document.appendChild(rootElement);

		Element element1 = document.createElement("user_activity_collection");
		rootElement.appendChild(element1);

		Element element2 = document.createElement("user_activity");
		element1.appendChild(element2);

		Element element3 = document.createElement("day");
		element2.appendChild(element3);
		element3.appendChild(document.createTextNode(DeviceData.getTimeStdString()));

		Element element4 = document.createElement("fork_value");
		element2.appendChild(element4);
		element4.appendChild(document.createTextNode(createForkValueString()));

		xmlString  = documentToXMLString(document);

		return xmlString;
	}


	private String createForkValueString(){
		String forkValueStr = "";


		List<DeviceData.Item> list = DeviceData.instance().itemList;
		int len = DeviceData.instance().itemList.size();
		for(int i=0; i<len; i++){
			DeviceData.Item item = list.get(i);
			forkValueStr = forkValueStr.concat("T" + item.time + ";" + processEventType(item.type) + ";");
			//			Log.d("createForkValueString: ", forkValueStr);
		}

		Log.d("XMLHelper : createForkValueString: ", forkValueStr);

		return forkValueStr;

	}

	private String processEventType(int event){

		switch (event) {

		case 0x01:
			return "S";
		case 0x02:
			return "SLB";
		case 0x03:
			return "SEB";
		case 0x04:
			return "SHL";
		case 0x05:
			return "SFL";
		case 0x06:
			return "OB";
		case 0x07:
			return "OT";
		case 0x08:
			return "R";
		case 0x09:
			return "PU";
		case 0x0B:
			return "PB";
		case 0x0C:
			return "F";
		case 0x0D:
			return "FO";
		default:
			break;

		}
		return null;


	}

	/*DO NOT EDIT THIS PART - Jen*/
	private String documentToXMLString(Document document){

		try{


			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = factory.newTransformer();

			Properties outFormat = new Properties();
			outFormat.setProperty(OutputKeys.INDENT, "yes");
			outFormat.setProperty(OutputKeys.METHOD, "xml");
			outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			outFormat.setProperty(OutputKeys.VERSION, "1.0");
			outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperties(outFormat);
			DOMSource domSource = 
					new DOMSource(document.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);

			transformer.transform(domSource, result);

			return output.toString();

		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "ERROR";

	}

	/*DO NOT EDIT THIS PART - Jen*/
	@SuppressLint("DefaultLocale")
	private String shaHashed(String stringToHash){

		try{
			byte[] result;

			final MessageDigest digest = MessageDigest.getInstance("SHA-1");
			result = digest.digest(stringToHash.getBytes("UTF-8"));

			// Another way to make HEX, my previous post was only the method like your solution
			StringBuilder sb = new StringBuilder();

			for (byte b : result) // This is your byte[] result..
			{
				sb.append(String.format("%02X", b));
			}

			return sb.toString().toLowerCase();
		}
		catch( NoSuchAlgorithmException e )
		{
			e.printStackTrace();
		}
		catch( UnsupportedEncodingException e )
		{
			e.printStackTrace();
		}
		return "";
	}

}
