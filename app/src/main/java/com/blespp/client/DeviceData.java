package com.blespp.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;


public class DeviceData {
	
	private static DeviceData mInstance;
	public static DeviceData instance(){
		if (mInstance == null) {
			mInstance = new DeviceData();
		}
		return mInstance;
	}
	
	public static long timeStd;		// 基准时间
	public static long timeLog;		// 基准时间
	public static String timeStdString; 
	public static final long TIME_2013 = 1356969600000L;		//2013.01.01
	public static final long TIME_2000 = 1356969600000L;		//2000.01.01  00:00:00
	
	private static long timeDiff;

	public byte[] lastDataReceived;
	public int lastDataCHKCalc;
	public int lastDataCHK;
	public int lastDataRecCmd;
	public int lastDataSendCmd;
	
	public boolean blastDataReceived=false;

	public boolean bGetCommand1=false;
	public boolean bGetCommand2=false;
	public boolean bGetCommand3=false;
	public boolean bGetCommand4=false;
	public boolean bGetCommand5=false;
	public boolean bGetCommand6=false;
	public boolean bGetCommand7=false;
	public boolean bGetCommand8=false;
	public boolean bGetCommand9=false;
	public boolean bGetCommand10=false;
	public boolean bGetCommand11=false;
	public boolean bGetCommand12=false;
	public boolean bGetCommand13=false;
	public boolean bGetCommand14=false;
	public boolean bGetCommand15=false;
	public boolean bGetCommand16=false;
	public boolean bGetCommand17=false;
	public boolean bGetCommand18=false;
	public boolean bGetCommand19=false;
	public boolean bGetCommand20=false;
	public boolean bGetCommand21=false;
	public boolean bGetCommand22=false;
	public boolean bGetCommand23=false;
	public boolean bGetCommand24=false;
	public boolean bGetCommand25=false;
	public boolean bGetCommand26=false;
	public boolean bGetCommand27=false;
	public boolean bGetCommand28=false;
	public boolean bGetCommand29=false;
	public boolean bGetCommand30=false;
	public boolean bGetCommand31=false;
	public boolean bGetCommand32=false;
	public boolean bGetCommand33=false;
	public boolean bGetCommand34=false;
	public boolean bGetCommand35=false;
	public boolean bGetCommand36=false;
	public boolean bGetCommand37=false;
	public boolean bGetCommand38=false;
	
	
	public int num;					// 保存的数目
	public int readSzie;			// XXX
	public int f_version;			// 软件版本号
	public int f_version_minor;			// 软件版本号
	
	
	public int s_version;			// 软件版本号
	public int s_version_minor;			// 软件版本号
	
	public int h_version;			// 固件版本号
	public int h_version_minor;			// 固件版本号
	public int pid;					// PID
	public int warning_time; 	// 2-20警报时间
	public int shock_level;		// 2-10振动强度
	public int model;			// 实时模式=01
	public int interval_time;		// XXX
	public String deviceID;					// 设备ID
	
	public int battery;						// 当前电量
	public int full_power;					// 满电阀值
	public int empty_power;					// 空电阀值
	public int low_power;					// 低电阀值
	
	public int touch_sensitive;					// 触摸灵敏度
	
	public boolean shock_enbale;				// 震动开关
	
	public boolean referA_enbale;		// A参考过滤开关
	public int referA_angle_min;				// A参考角度
	public int referA_angle_max;				// 
	
	public boolean referB_enbale;		// B参考过滤开关
	public int referB_angle_min;				// B参考角度
	public int referB_angle_max;				// 
	
	public int BLE_broadcast_time;			// BLE广播时间(0 - 255)
	
	public int log1_warning_max;				// LOG报警阀值1
	public int log2_warning_max;			// LOG报警阀值2
	
	public int timeout;				// 超时
	
	public int continuous_touch;		// 连续碰触过滤值
	
	public int bleN1;
	public int bleN2;
	public int bleY1;
	public int bleY2;
	public boolean led_enabled;

	//added fields for real time 
	public int real_interval;
	public int real_event_type;

	public ArrayList<DeviceData.Item> itemList = new ArrayList<DeviceData.Item>();
	public DeviceData.Item lastItem;
	public ArrayList<String> itemStrList = new ArrayList<String>();
	
	public void addItem(DeviceData.Item item){
		lastItem = item;
		itemList.add(item);
		
		itemStrList.add(item.dateStr);
	}
	
	public void updateData(){
		
	}
	
	
	public static String getTimeStdString() {
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
//		sDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
//		Calendar c = Calendar.getInstance();
//		TimeZone z = c.getTimeZone();
//		
//		sDateFormat.setTimeZone(z);
		
		long startDateLong = DeviceData.timeLog;
		
		DeviceData.timeStdString = sDateFormat.format(new Date(startDateLong));
		
		System.out.println("setTimeStd: " + timeLog + " start Date long: " + startDateLong + " timeStdString: " + sDateFormat.format(new Date(timeLog)) + timeDiff);
		
		return sDateFormat.format(new Date(startDateLong));
	}



	//////////////////////////////////////////////////////
	//					Item
	//////////////////////////////////////////////////////
	
	static public class Item{
		
		public long time;
		public int type;
		
		public String dateStr;
		//public String typeStr;
		public static int event_count = 0;
		public Item(int tm, int tp){
//			time = TIME_2013 + DeviceData.timeStd + tm*1000;
			
			time = tm + timeDiff/1000;
			type = tp;
			
			event_count++;
			dateStr = event_count + " - " + timeToString(time) + " --- " + type + "second";
		}
		
		public String timeToString(long tm){
			SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd- HH:mm:ss"); 
			String str = sDateFormat.format(new Date(tm)); 
			
			return str;
		}
		
	}



	@SuppressWarnings("deprecation")
	public static void setTimeDiff() {
		// TODO Auto-generated method stub
		
		String startDate = new StringBuilder(String.valueOf(DeviceData.getTimeStdString())).append(" 00:00:00").toString();
		
		System.out.println("STARTDATE: "+startDate);
		
		Date date = null;
		long milliseconds = 0;
		
		try {
					
			SimpleDateFormat isoFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
////		    isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//			Calendar c = Calendar.getInstance();
//			TimeZone z = c.getTimeZone();
			
//			isoFormat.setTimeZone(z);
		    date = isoFormat.parse(startDate);
						
			milliseconds = date.getTime();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//in milliseconds
		System.out.println(" DeviceData.timeLog: "+ DeviceData.timeLog);
		System.out.println(" DeviceData.milliseconds: "+ milliseconds);
		
		
//		timeDiff = TIME_2013 + DeviceData.timeLog - milliseconds;
		timeDiff = DeviceData.timeLog - milliseconds;
		Log.d("setTimeDiff  @ DeviceData: " + DeviceData.TIME_2000 + DeviceData.timeLog, "startDate: " + startDate + "-" + date + "mill: " + milliseconds + "diff: " + timeDiff);
	}
	
}
