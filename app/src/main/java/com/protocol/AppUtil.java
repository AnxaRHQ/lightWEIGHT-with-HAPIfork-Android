package com.protocol;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.obj.UserProfile.Gender;

public class AppUtil {




	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	/**/


	public static long getDateNow(){

		Calendar c = Calendar.getInstance();
//		TimeZone z = c.getTimeZone();
//		int offset = z.getRawOffset();
//
//		if(z.inDaylightTime(new Date())){
//			offset = offset + z.getDSTSavings();
//		}
//
//
//		int offsetHrs = offset / 1000 / 60 / 60;
//		int offsetMins = offset / 1000 / 60 % 60;
//
//		c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY) + (offsetHrs));
//		c.set(Calendar.MINUTE,c.get(Calendar.MINUTE) + (offsetMins));

		// returnValue = c.getTimeInMillis()/1000;

		System.out.println("AppUtil current Time: " + (c.getTimeInMillis())/1000  );
//		+ "  used time zone: " + z.getDisplayName() + " raw offset: " + offset);
		return (c.getTimeInMillis())/1000;

	}
	//from ForkProtocol.java
	public static String timeToString(long tm){
		SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.ENGLISH); 
		String str = sDateFormat.format(new Date(tm)); 
		return str;
	}


	public static String asHex(byte[] buf)
	{
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i)
		{
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

	public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha1hash = md.digest();
		return asHex(sha1hash);
	}

	public static boolean StringtoBooleanFormat(String str,boolean defaultBoolean){
		if (str!=null && str.length()>0){
			if (str.toLowerCase(Locale.US).equals("true")){
				return true;
			}else if (str.toLowerCase(Locale.US).equals("false")){
				return false;
			}
		}
		return defaultBoolean;
	}
	public static float StringtoFloatFormat(String str){
		Float f = 0f;

		if (str!=null && str.length()>0){
			f = Float.valueOf(str);
		}
		return f;
	}

	public static Gender StringtoGender(String str){
		if (str!=null && str.length()>0){
			if (str.toLowerCase(Locale.US).equals("female")){
				return Gender.FEMALE;
			}else if (str.toLowerCase(Locale.US).equals("male")){
				return Gender.FEMALE;
			}
		}
		return null;

	}






}