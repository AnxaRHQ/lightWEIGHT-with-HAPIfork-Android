package com.blespp.client;

import java.io.IOException;

import com.controllers.ApplicationEx;

import android.util.Log;

public class ForkProtocol {

	// TX通讯格式 0x55 CMD LEN DATA1…..DATAn checksum
	public static final int TX_PACKAGE_1 = 0x55;	// TX通讯格式
	public static final int TX_COMMAND_1 = 0x01;	// 读LOG信息 
	public static final int TX_COMMAND_2 = 0x02; 	// 读设备信息
	public static final int TX_COMMAND_3 = 0x03;	// 设置设备信息(CMD = 0x03)
	public static final int TX_COMMAND_4 = 0x04;	// 读取设备时基(CMD = 0x04)
	public static final int TX_COMMAND_5 = 0x05;	// 设置设备时基(CMD = 0x05)
	public static final int TX_COMMAND_6 = 0x06;	// 读取LOG信息表(CMD = 0x06)
	public static final int TX_COMMAND_7 = 0x07;	// 读取设备工作模式(CMD = 0x07
	public static final int TX_COMMAND_8 = 0x08;	// 设置设备工作模式(CMD = 0x08
	public static final int TX_COMMAND_9 = 0x09;	// 读取设备,报警时间间隔 + 震动强度(CMD = 0x09)
	public static final int TX_COMMAND_10 = 0x0a;	// 设置设备,报警时间间隔 + 震动强度(CMD = 0x0a)
	public static final int TX_COMMAND_11 = 0x0b;	// 读取设备电池电量满电阀值空电阀值低电阀值(CMD = 0x0b
	public static final int TX_COMMAND_12 = 0x0c;	// 设置设备,满电阀值,空电阀值,低电阀值(CMD = 0x0c)
	public static final int TX_COMMAND_13 = 0x0d;	// 读取设备,触摸感应灵敏度(CMD = 0x0d)
	public static final int TX_COMMAND_14 = 0x0e;	// 设置设备,触摸感应灵敏度(CMD = 0x0e)
	public static final int TX_COMMAND_15 = 0x0f;	// 读取设备,震动开关(CMD = 0x0f)
	public static final int TX_COMMAND_16 = 0x10;	// 设置设备,震动开关(CMD = 0x10)
	public static final int TX_COMMAND_17 = 0x11;	// 读取设备,GSENSOR A面设置(CMD = 0x11)
	public static final int TX_COMMAND_18 = 0x12;	// 设置设备,GSENSOR A面设置(CMD = 0x12)
	public static final int TX_COMMAND_19 = 0x13;	// 读取设备,GSENSOR B面设置(CMD = 0x13)
	public static final int TX_COMMAND_20 = 0x14;	// 设置设备GSENSOR 面设置(CMD = 0x14)
	public static final int TX_COMMAND_21 = 0x15;	// 读取设备,BLE 广播时间设置(CMD = 0x15)
	public static final int TX_COMMAND_22 = 0x16;	// 设置设备,GSENSOR A面设置(CMD = 0x16)
	public static final int TX_COMMAND_23 = 0x17;	// 读取设备,LOG存储剩余报警阀值(CMD = 0x17)
	public static final int TX_COMMAND_24 = 0x18;	// 设置设备,LOG存储剩余报警阀值(CMD = 0x18)
	public static final int TX_COMMAND_25 = 0x19;	// 读取设备,TIMEOUT值(CMD = 0x19)
	public static final int TX_COMMAND_26 = 0x1a;	// 设置设备,TIMEOUT值(CMD = 0x1a)
	public static final int TX_COMMAND_27 = 0x1b;	// 读取设备连续碰触过滤值(CMD = 0x1b)
	public static final int TX_COMMAND_28 = 0x1c;	// 设置设备,连续碰触过滤值(CMD = 0x1c)
	public static final int TX_COMMAND_29 = 0x1d;	// 清除设备LOG(CMD = 0x1d)
	public static final int TX_COMMAND_30 = 0x1e;	// 错误命令响应(CMD = 0x1e)
	public static final int TX_COMMAND_31 = 0x1f;	// 错误命令响应(CMD = 0x1e)
	public static final int TX_COMMAND_32 = 0x20;	// 错误命令响应(CMD = 0x1e)
	public static final int TX_COMMAND_33 = 0x21;	// 错误命令响应(CMD = 0x1e)
	public static final int TX_COMMAND_34 = 0x22;	// 错误命令响应(CMD = 0x1e)
	
	
	// RX通讯格式 命令返回0xaa CMD LEN DATA1…..DATAn checksum
	public static final int RX_PACKAGE_1 = 0xaa;	// RX通讯格式
	public static final int RX_COMMAND_1 = 0x01;	// 读LOG信息 
	public static final int RX_COMMAND_2 = 0x02; 	// 读设备信息
	public static final int RX_COMMAND_3 = 0x03;	// 设置设备信息(CMD = 0x03)
	public static final int RX_COMMAND_4 = 0x04;	// 读取设备时基(CMD = 0x04)
	public static final int RX_COMMAND_5 = 0x05;	// 设置设备时基(CMD = 0x05)
	public static final int RX_COMMAND_6 = 0x06;	// 读取LOG信息表(CMD = 0x06)
	public static final int RX_COMMAND_7 = 0x07;	// 读取设备工作模式(CMD = 0x07
	public static final int RX_COMMAND_8 = 0x08;	// 设置设备工作模式(CMD = 0x08
	public static final int RX_COMMAND_9 = 0x09;	// 读取设备,报警时间间隔 + 震动强度(CMD = 0x09)
	public static final int RX_COMMAND_10 = 0x0a;	// 设置设备,报警时间间隔 + 震动强度(CMD = 0x0a)
	public static final int RX_COMMAND_11 = 0x0b;	// 读取设备电池电量满电阀值空电阀值低电阀值(CMD = 0x0b
	public static final int RX_COMMAND_12 = 0x0c;	// 设置设备,满电阀值,空电阀值,低电阀值(CMD = 0x0c)
	public static final int RX_COMMAND_13 = 0x0d;	// 读取设备,触摸感应灵敏度(CMD = 0x0d)
	public static final int RX_COMMAND_14 = 0x0e;	// 设置设备,触摸感应灵敏度(CMD = 0x0e)
	public static final int RX_COMMAND_15 = 0x0f;	// 读取设备,震动开关(CMD = 0x0f)
	public static final int RX_COMMAND_16 = 0x10;	// 设置设备,震动开关(CMD = 0x10)
	public static final int RX_COMMAND_17 = 0x11;	// 读取设备,GSENSOR A面设置(CMD = 0x11)
	public static final int RX_COMMAND_18 = 0x12;	// 设置设备,GSENSOR A面设置(CMD = 0x12)
	public static final int RX_COMMAND_19 = 0x13;	// 读取设备,GSENSOR B面设置(CMD = 0x13)
	public static final int RX_COMMAND_20 = 0x14;	// 设置设备GSENSOR 面设置(CMD = 0x14)
	public static final int RX_COMMAND_21 = 0x15;	// 读取设备,BLE 广播时间设置(CMD = 0x15)
	public static final int RX_COMMAND_22 = 0x16;	// 设置设备,GSENSOR A面设置(CMD = 0x16)
	public static final int RX_COMMAND_23 = 0x17;	// 读取设备,LOG存储剩余报警阀值(CMD = 0x17)
	public static final int RX_COMMAND_24 = 0x18;	// 设置设备,LOG存储剩余报警阀值(CMD = 0x18)
	public static final int RX_COMMAND_25 = 0x19;	// 读取设备,TIMEOUT值(CMD = 0x19)
	public static final int RX_COMMAND_26 = 0x1a;	// 设置设备,LOG存储剩余报警阀值(CMD = 0x1a)
	public static final int RX_COMMAND_27 = 0x1b;	// 读取设备连续碰触过滤值(CMD = 0x1b)
	public static final int RX_COMMAND_28 = 0x1c;	// 设置设备,连续碰触过滤值(CMD = 0x1c)
	public static final int RX_COMMAND_29 = 0x1d;	// 清除设备LOG(CMD = 0x1d)
	public static final int RX_COMMAND_30 = 0x1e;	// 错误命令响应(CMD = 0x1e)
	public static final int RX_COMMAND_31 = 0x1f;	// 错误命令响应(CMD = 0x1e)
	public static final int RX_COMMAND_32 = 0x20;	// 错误命令响应(CMD = 0x1e)
	public static final int RX_COMMAND_33 = 0x21;	// 错误命令响应(CMD = 0x1e)
	public static final int RX_COMMAND_34 = 0x22;	// 错误命令响应(CMD = 0x1e)
	
	
	private DeviceData mData;
	
	public ForkProtocol(DeviceData data){
		mData = data;
	}

	public int parseData(MyByteArrayInputSream data){
		int pack = 0;
		int command = 0;
		
		mData.lastDataReceived = data.mBuffer;
		mData.blastDataReceived=true;
		
		try {
			pack = data.readbyte();
			if (pack != RX_PACKAGE_1) {
				return -1;
			}
			
			command = data.readbyte();
			mData.lastDataRecCmd = command;
			System.out.println("COM PARSE --> "+command);
			switch (command) {
				case RX_COMMAND_1:{
					parseDataCommand1(data);
					break;
				}
				
				case RX_COMMAND_2:{
					parseDataCommand2(data);
					break;
				}
				
				case RX_COMMAND_3:{
					parseDataCommand3(data);
					break;
				}
				
				case RX_COMMAND_4:{
					parseDataCommand4(data);
					break;
				}
				
				case RX_COMMAND_5:{
					parseDataCommand5(data);
					break;
				}
				
				case RX_COMMAND_6:{
					System.out.println("COM PARSE --> 6 to call");
					
					parseDataCommand6(data);
					break;
				}
				
				case RX_COMMAND_7:{
					parseDataCommand7(data);
					break;
				}
				
				case RX_COMMAND_8:{
					parseDataCommand8(data);
					break;
				}
				
				case RX_COMMAND_9:{
					parseDataCommand9(data);
					break;
				}
				
				case RX_COMMAND_10:{
					parseDataCommand10(data);
					break;
				}
				
				case RX_COMMAND_11:{
					parseDataCommand11(data);
					break;
				}
				
				case RX_COMMAND_12:{
					parseDataCommand12(data);
					break;
				}
				
				case RX_COMMAND_13:{
					parseDataCommand13(data);
					break;
				}
				
				case RX_COMMAND_14:{
					parseDataCommand14(data);
					break;
				}
				
				case RX_COMMAND_15:{
					parseDataCommand15(data);
					break;
				}
				
				case RX_COMMAND_16:{
					parseDataCommand16(data);
					break;
				}
				
				case RX_COMMAND_17:{
					parseDataCommand17(data);
					break;
				}
				
				case RX_COMMAND_18:{
					parseDataCommand18(data);
					break;
				}
				
				case RX_COMMAND_19:{
					parseDataCommand19(data);
					break;
				}
				
				case RX_COMMAND_20:{
					parseDataCommand20(data);
					break;
				}
				
				case RX_COMMAND_21:{
					parseDataCommand21(data);
					break;
				}
				
				case RX_COMMAND_22:{
					parseDataCommand22(data);
					break;
				}
				
				case RX_COMMAND_23:{
					parseDataCommand23(data);
					break;
				}
				
				case RX_COMMAND_24:{
					parseDataCommand24(data);
					break;
				}
				
				case RX_COMMAND_25:{
					parseDataCommand25(data);
					break;
				}
				
				case RX_COMMAND_26:{
					parseDataCommand26(data);
					break;
				}
				
				case RX_COMMAND_27:{
					parseDataCommand27(data);
					break;
				}
				
				case RX_COMMAND_28:{
					parseDataCommand28(data);
					break;
				}
				
				case RX_COMMAND_29:{
					parseDataCommand29(data);
					break;
				}
				
				case RX_COMMAND_30:{
					parseDataCommand30(data);
					break;
				}
				
				case RX_COMMAND_31:{
					parseDataCommand31(data);
					break;
				}
				case RX_COMMAND_32:{
					parseDataCommand32(data);
					break;
				}
				case RX_COMMAND_33:{
					parseDataCommand33(data);
					break;
				}
				case RX_COMMAND_34:{
					parseDataCommand34(data);
					break;
				}
				
				default:
					break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return command;
	}
	
	private void readChecksum(MyByteArrayInputSream data, int length)throws IOException {
		int checksum = data.readbyte();		// checksum校验
		if (checksum != length + 1) {
			throw new ChecksumException();
		}
	}
	
	/**
	 * 读取LOG信息(CMD = 0x01)
	 */
	private void parseDataCommand1(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
		mData.num = data.read2byte();
		mData.readSzie = data.read2byte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand1 =true;
		else
			mData.bGetCommand1 =false;
	}
	
	/**
	 * 读取设备信息(CMD = 0x02)
	 */
	private void parseDataCommand2(MyByteArrayInputSream data)throws IOException {
		System.out.println("HELLO ");
		int length = data.readbyte();
		mData.s_version = data.readbyte();
		mData.s_version_minor = data.readbyte();
		mData.h_version = data.readbyte();
		mData.h_version_minor = data.readbyte();
		//mData.pid = data.read2byte();
		
		byte[] buf = data.readNbyte(9);
		System.out.println("parseDataCommand2 " + data);
		System.out.println("parseDataCommand2 buf " + buf);

		mData.deviceID = new String((char)buf[0]+""+(char)buf[1]+""+(char)buf[2]+""+(char)buf[3]+""+(char)buf[4]+""+(char)buf[5]+""+(char)buf[6]+""+(char)buf[7]+""+(char)buf[8]);
			
		ApplicationEx.getInstance().statsData.deviceIDinHex = mData.deviceID;

		System.out.println("parseDataCommand2 deviceIDinHex " + mData.deviceID);

		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand2 =true;
		else
			mData.bGetCommand2 =false;
		
		System.out.println("COMMAND 2");
	}
	
	/**
	 * 设置设备信息(CMD = 0x03)
	 */
	private void parseDataCommand3(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand3 =true;
		else
			mData.bGetCommand3 =false;
	}
	
	/**
	 * 读取设备时基(CMD = 0x04)
	 */
	private void parseDataCommand4(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
//		long tmp = data.read4byte();
		DeviceData.timeStd = data.read4byte() * 1000;
		DeviceData.timeLog = data.read4byte() * 1000;
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand4 =true;
		else
			mData.bGetCommand4 =false;
		
		System.out.println("JEN = timelog"+DeviceData.timeLog + " **timeStd: " + DeviceData.timeStd);
		
	}
	
	/**
	 * 设置设备时基(CMD = 0x05)
	 */
	private void parseDataCommand5(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand5 =true;
		else
			mData.bGetCommand5 =false;
	}
	
	/**
	 * 读取LOG信息表(CMD = 0x06)
	 */
	private void parseDataCommand6(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
		int time = data.read3byte();
		int type = data.readbyte();
		
		DeviceData.Item item = new DeviceData.Item(time, type);
		mData.addItem(item);
		
		System.out.println("parseDataCommand6: "+ ""+ length + " time: " + time + "type: " +type+" "+DeviceData.timeLog+" "+DeviceData.timeStd+" "+DeviceData.timeStdString);
			
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand6 =true;
		else
			mData.bGetCommand6 =false;
	}
	
	/**
	 * 读取设备工作模式(CMD = 0x07
	 */
	private void parseDataCommand7(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.model = data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand7 =true;
		else
			mData.bGetCommand7 =false;
		
	}
	
	/**
	 * 设置设备工作模式(CMD = 0x08
	 */
	private void parseDataCommand8(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
		//mData.model = data.readbyte();
		
		if (length == 5) {
			int time = data.read3byte();
			int type = data.readbyte();
			
			DeviceData.Item item = new DeviceData.Item(time, type);
			mData.addItem(item);
		}
		
		if (length == 1) {
			
		}
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand8 =true;
		else
			mData.bGetCommand8 =false;
		
	}	

	/**
	 * 读取设备,报警时间间隔 + 震动强度(CMD = 0x09)
	 */
	private void parseDataCommand9(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
			
		mData.warning_time = data.readbyte();
		mData.shock_level = data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand9 =true;
		else
			mData.bGetCommand9 =false;
	}
	
	/**
	 * 设置设备,报警时间间隔 + 震动强度(CMD = 0x0a)
	 */
	private void parseDataCommand10(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand10 =true;
		else
			mData.bGetCommand10 =false;
	}
	
	/**
	 * 读取设备电池电量满电阀值空电阀值低电阀值(CMD = 0x0b
	 */
	private void parseDataCommand11(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.battery = data.read2byte();
		mData.full_power = data.read2byte();
		mData.empty_power = data.read2byte();
		mData.low_power = data.read2byte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand11 =true;
		else
			mData.bGetCommand11 =false;
	}
	
	
	/**
	 * 设置设备,满电阀值,空电阀值,低电阀值(CMD = 0x0c)
	 */
	private void parseDataCommand12(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand12 =true;
		else
			mData.bGetCommand12 =false;
	}
	
	
	/**
	 * 读取设备,触摸感应灵敏度(CMD = 0x0d)
	 */
	private void parseDataCommand13(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.touch_sensitive = data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand13 =true;
		else
			mData.bGetCommand13 =false;
	}
	
	/**
	 * 设置设备,触摸感应灵敏度(CMD = 0x0e)
	 */
	private void parseDataCommand14(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand14 =true;
		else
			mData.bGetCommand14 =false;
	}
	
	/**
	 * 读取设备,震动开关(CMD = 0x0f)
	 */
	private void parseDataCommand15(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.shock_enbale = (data.readbyte() == 0)? false : true;
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand15 =true;
		else
			mData.bGetCommand15 =false;
	}
	
	/**
	 * 设置设备,震动开关(CMD = 0x10)
	 */
	private void parseDataCommand16(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand16 =true;
		else
			mData.bGetCommand16 =false;
	}
	
	/**
	 * 读取设备,GSENSOR A面设置(CMD = 0x11)
	 */
	private void parseDataCommand17(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.referA_enbale = (data.readbyte() == 0)? false : true;
		mData.referA_angle_min = (byte)data.readbyte();
		mData.referA_angle_max = (byte)data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand17 =true;
		else
			mData.bGetCommand17 =false;
	}
	
	/**
	 * 设置设备,GSENSOR A面设置(CMD = 0x12)
	 */
	private void parseDataCommand18(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand18 =true;
		else
			mData.bGetCommand18 =false;
	}
	
	/**
	 * 读取设备,GSENSOR B面设置(CMD = 0x13)
	 */
	private void parseDataCommand19(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.referB_enbale = (data.readbyte() == 0)? false : true;
		mData.referB_angle_min = (byte)data.readbyte();
		mData.referB_angle_max = (byte)data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand19 =true;
		else
			mData.bGetCommand19 =false;
	}
	
	/**
	 * 设置设备GSENSOR 面设置(CMD = 0x14)
	 */
	private void parseDataCommand20(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand20 =true;
		else
			mData.bGetCommand20 =false;
	}
	
	/**
	 * 读取设备,BLE 广播时间设置(CMD = 0x15)
	 */
	private void parseDataCommand21(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.BLE_broadcast_time = data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand21 =true;
		else
			mData.bGetCommand21 =false;
	}
	
	/**
	 * 设置设备,GSENSOR A面设置(CMD = 0x16)
	 */
	private void parseDataCommand22(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand22 =true;
		else
			mData.bGetCommand22 =false;
	}
	
	/**
	 * 读取设备,LOG存储剩余报警阀值(CMD = 0x17)
	 */
	private void parseDataCommand23(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.log1_warning_max = data.read2byte();
		mData.log2_warning_max = data.read2byte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand23 =true;
		else
			mData.bGetCommand23 =false;
	}
	
	/**
	 * 设置设备,LOG存储剩余报警阀值(CMD = 0x18)
	 */
	private void parseDataCommand24(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand24 =true;
		else
			mData.bGetCommand24 =false;
	}
	
	/**
	 * 读取设备,TIMEOUT值(CMD = 0x19)
	 */
	private void parseDataCommand25(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.timeout = data.read2byte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand25 =true;
		else
			mData.bGetCommand25 =false;
	}
	
	/**
	 * 设置设备,LOG存储剩余报警阀值(CMD = 0x1a)
	 */
	private void parseDataCommand26(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand26 =true;
		else
			mData.bGetCommand26 =false;
	}
	
	/**
	 * 读取设备连续碰触过滤值(CMD = 0x1b)
	 */
	private void parseDataCommand27(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.continuous_touch = data.readbyte();
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand27 =true;
		else
			mData.bGetCommand27 =false;
	}
	
	/**
	 * 设置设备,连续碰触过滤值(CMD = 0x1c)
	 */
	private void parseDataCommand28(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand28 =true;
		else
			mData.bGetCommand28 =false;
	}
	
	/**
	 * 清除设备LOG(CMD = 0x1d)
	 */
	private void parseDataCommand29(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand29 =true;
		else
			mData.bGetCommand29 =false;
	}
	
	/**
	 * 错误命令响应(CMD = 0x1e)
	 */
	private void parseDataCommand30(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.bleN1 = data.readbyte();
		mData.bleN2 = data.readbyte();

		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand30 =true;
		else
			mData.bGetCommand30 =false;
	}
	
	private void parseDataCommand31(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.bleY1 = data.readbyte();
		mData.bleY2 = data.readbyte();

		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand31 =true;
		else
			mData.bGetCommand31 =false;
	}

	
	private void parseDataCommand32(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.led_enabled = (data.readbyte() == 0)? false : true;
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand32 =true;
		else
			mData.bGetCommand32 =false;
	}

	
	private void parseDataCommand33(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();	// 数据长度
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand33 =true;
		else
			mData.bGetCommand33 =false;
	}
	
	private void parseDataCommand34(MyByteArrayInputSream data)throws IOException {
		int length = data.readbyte();
//		long tmp = data.read4byte();
//		DeviceData.timeStd = data.read4byte() * 1000;
//		DeviceData.timeLog = data.read4byte() * 1000;
		mData.real_interval = data.read3byte();
		int type = data.readbyte();
		mData.real_event_type = type;
		Log.d("parseDataCommand34: ", ""+data.readbyte());
		
		mData.lastDataCHKCalc = (byte)data.CHECKSUM;
		int checksum = (byte)data.readbyte();
		mData.lastDataCHK = checksum;
		if (checksum==mData.lastDataCHKCalc)
			mData.bGetCommand34 =true;
		else
			mData.bGetCommand34 =false;
	}

////////////////////////////////////////////////////////////////////////////
//						准备数据
////////////////////////////////////////////////////////////////////////////
	public static final int CHECKSUM = 0x41;
	
	/**
	 * 读取LOG信息(CMD = 0x01)
	 */
	public byte[] prepareDataCommand1(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_1);		// 命令码
		data.writebyte(0x01);		// 数据长度
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备信息(CMD = 0x02)
	 */
	public byte[] prepareDataCommand2(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_2);		// 命令码
		data.writebyte(0x01);		// 数据长度
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备信息(CMD = 0x03)
	 */
	public byte[] prepareDataCommand3(int s_version, int s_version_minor, int h_version, int h_version_minor, String deviceID){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(17);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_3);		// 命令码
		data.writebyte(14);		// 数据长度
		data.writebyte(s_version);
		data.writebyte(s_version_minor);
		data.writebyte(h_version);
		data.writebyte(h_version_minor);
		data.writeNbyte(deviceID.getBytes(), 9);
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备时基(CMD = 0x04)
	 */
	public byte[] prepareDataCommand4(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_4);		// 命令码
		data.writebyte(0x01);		// 数据长度
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备时基(CMD = 0x05)
	 */
	public byte[] prepareDataCommand5(int curTime){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(8);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_5);	// 命令码
		data.writebyte(5);		// 数据长度
		data.write4byte(curTime);
		data.writeChecksum();
		
		return data.getData();
	}
	
	/**
	 * 读取LOG信息表(CMD = 0x06)
	 */
	public byte[] prepareDataCommand6(int startIndex, int count){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(8);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_6);		// 命令码
		data.writebyte(5);		// 数据长度
		data.write2byte(startIndex);
		data.write2byte(count);
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备工作模式(CMD = 0x07
	 */
	public byte[] prepareDataCommand7(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_7);		// 命令码
		data.writebyte(0x01);		// 数据长度
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备工作模式(CMD = 0x08
	 */
	public byte[] prepareDataCommand8(int model){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(5);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_8);		// 命令码
		data.writebyte(2);		// 数据长度
		data.writebyte(model);
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备,报警时间间隔 + 震动强度(CMD = 0x09)
	 */
	public byte[] prepareDataCommand9(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_9);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,报警时间间隔 + 震动强度(CMD = 0x0a)
	 */
	public byte[] prepareDataCommand10(int warning_time, int shock_level){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(6);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_10);		// 命令码
		data.writebyte(3);		// 数据长度
		data.writebyte(warning_time);
		data.writebyte(shock_level);
		data.writeChecksum();		// checksum
		return data.getData();
	}
	
	/**
	 * 读取设备电池电量满电阀值空电阀值低电阀值(CMD = 0x0b
	 */
	public byte[] prepareDataCommand11(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_11);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	
	/**
	 * 设置设备,满电阀值,空电阀值,低电阀值(CMD = 0x0c)
	 */
	public byte[] prepareDataCommand12( int full_power, int empty_power, int low_power){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(10);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_12);		// 命令码
		data.writebyte(7);		// 数据长度
		
		data.write2byte(full_power);
		data.write2byte(empty_power);
		data.write2byte(low_power);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	
	/**
	 * 读取设备,触摸感应灵敏度(CMD = 0x0d)
	 */
	public byte[] prepareDataCommand13(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_13);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,触摸感应灵敏度(CMD = 0x0e)
	 */
	public byte[] prepareDataCommand14(int touch_sensitive){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(5);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_14);		// 命令码
		data.writebyte(2);		// 数据长度
		
		data.writebyte(touch_sensitive);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备,震动开关(CMD = 0x0f)
	 */
	public byte[] prepareDataCommand15(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_15);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,震动开关(CMD = 0x10)
	 */
	public byte[] prepareDataCommand16(int shock_enbale){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(5);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_16);		// 命令码
		data.writebyte(2);		// 数据长度
		
		data.writebyte(shock_enbale);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备,GSENSOR A面设置(CMD = 0x11)
	 */
	public byte[] prepareDataCommand17(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_17);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,GSENSOR A面设置(CMD = 0x12)
	 */
	public byte[] prepareDataCommand18(int referA_enbale, int referA_angle_min, int referA_angle_max){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(7);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_18);		// 命令码
		data.writebyte(4);		// 数据长度
		
		data.writebyte(referA_enbale);
		data.writebyte(referA_angle_min);
		data.writebyte(referA_angle_max);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备,GSENSOR B面设置(CMD = 0x13)
	 */
	public byte[] prepareDataCommand19(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_19);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备GSENSOR B面设置(CMD = 0x14)
	 */
	public byte[] prepareDataCommand20(int referB_enbale, int referB_angle_min, int referB_angle_max){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(7);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_20);		// 命令码
		data.writebyte(4);		// 数据长度
		
		data.writebyte(referB_enbale);
		data.writebyte(referB_angle_min);
		data.writebyte(referB_angle_max);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备,BLE 广播时间设置(CMD = 0x15)
	 */
	public byte[] prepareDataCommand21(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_21);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,BLE 广播时间设置(CMD = 0x16)
	 */
	public byte[] prepareDataCommand22(int BLE_broadcast_time){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(5);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_22);		// 命令码
		data.writebyte(0x02);		// 数据长度
		
		data.writebyte(BLE_broadcast_time);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备,LOG存储剩余报警阀值(CMD = 0x17)
	 */
	public byte[] prepareDataCommand23(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_23);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,LOG存储剩余报警阀值(CMD = 0x18)
	 */
	public byte[] prepareDataCommand24(int log1_warning_max, int log2_warning_max){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(8);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_24);		// 命令码
		data.writebyte(5);		// 数据长度
		
		data.write2byte(log1_warning_max);
		data.write2byte(log2_warning_max);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 读取设备,TIMEOUT值(CMD = 0x19)
	 */
	public byte[] prepareDataCommand25(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_25);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,超时(CMD = 0x1a)
	 */
	public byte[] prepareDataCommand26(int timeout){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(6);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_26);		// 命令码
		data.writebyte(3);		// 数据长度
		data.write2byte(timeout);
		data.writeChecksum();		// checksum
		return data.getData();
	}
	
	/**
	 * 读取设备连续碰触过滤值(CMD = 0x1b)
	 */
	public byte[] prepareDataCommand27(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_27);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 设置设备,连续碰触过滤值(CMD = 0x1c)
	 */
	public byte[] prepareDataCommand28(int continuous_touch){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(5);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_28);		// 命令码
		data.writebyte(2);		// 数据长度
		
		data.writebyte(continuous_touch);
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 清除设备LOG(CMD = 0x1d)
	 */
	public byte[] prepareDataCommand29(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(5);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_29);		// 命令码
		data.writebyte(0x02);		// 数据长度
		data.writebyte(0x27);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	/**
	 * 错误命令响应(CMD = 0x1e)
	 */
	public byte[] prepareDataCommand30(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(6);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_30);		// 命令码
		data.writebyte(0x03);		// 数据长度
		data.writebyte(0x43);		// 数据长度
		data.writebyte(0x4E);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}

	public byte[] prepareDataCommand31(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(6);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_31);		// 命令码
		data.writebyte(3);		// 数据长度
		data.writebyte(0x43);		// 数据长度
		data.writebyte(0x59);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
	public byte[] prepareDataCommand32(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_32);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	public byte[] prepareDataCommand33(int led_enabled){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(5);
		data.writebyte(0x55);
		data.writebyte(TX_COMMAND_33);		// 命令码
		data.writebyte(0x02);		// 数据长度
		data.writebyte(led_enabled);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}

	public byte[] prepareDataCommandReset(){
		MyByteArrayOutputStream data = new MyByteArrayOutputStream(4);
		data.writebyte(0x56);
		data.writebyte(0xFA);		// 命令码
		data.writebyte(0x01);		// 数据长度
		
		data.writeChecksum();		// checksum
		
		return data.getData();
	}
	
}
