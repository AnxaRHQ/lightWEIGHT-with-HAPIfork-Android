package com.protocol.xml.handlers;

import org.apache.http.HttpStatus;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.protocol.Connection;

import android.os.Handler;
import android.os.Message;

public abstract class DefaultResponseHandler extends DefaultHandler {

	protected Handler handler;
	protected String resultCode;
	private String resultMessage;
	protected Object responseObj;
	protected boolean isError = false;
	StringBuilder builder;
	
	
	public DefaultResponseHandler(Handler handler){
		this.handler = handler;
	}
	
	public abstract Object getResponse();
	
	public Object getResponseObj(){
		  return responseObj;
	}
	
	public void setResponseObj(Object responseObj){
		this.responseObj = responseObj;
	}
	public void setResultCode(String resultCode){
		this.resultCode = resultCode;
	}
	
	public void setResultMessage(String resultMessage){
		this.resultMessage = resultMessage;
	}
	
	public String getResultCode(){
		return resultCode;
	}
	
	public String getResultMessage(){
		return resultMessage;
	}
	
	@Override
	public void startDocument() throws SAXException{
		super.startDocument();
		builder = new StringBuilder();
	}
	
	
	@Override
	public void endDocument() throws SAXException{
		super.endDocument();
	
		Message message = Message.obtain(handler);
		
		resultCode = ("".equals(resultCode) || resultCode == null) ? "0" : resultCode.trim();
		
		int rCode = Integer.parseInt(resultCode);
		message.arg1 = rCode;
		
		if (rCode == 0) return;
		System.out.println(rCode);
		switch (rCode) {
			case HttpStatus.SC_OK:
				message.what = Connection.REQUEST_SUCCESS;
				
				message.obj = responseObj;
		         break;
		    default:
		    	message.what = Connection.REQUEST_ERROR;
		        message.obj = resultMessage;

		}//end switch
		
		 if (handler != null) 
			 handler.sendMessage(message);
		
	}
	
	
	@Override
	public void endElement(String uri, String localName,String qName) throws SAXException{
		
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException{
		 
		 
	}
	
	
}
