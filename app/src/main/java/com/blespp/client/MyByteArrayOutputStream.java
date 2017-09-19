package com.blespp.client;

import android.view.animation.BounceInterpolator;

public class MyByteArrayOutputStream {

	private byte[] mBuffer;
	private int curPos = 0;
	private int mMaxPos = 0;
	
	public int CHECKSUM = 0x00;
	public int getSize(){
		return curPos;
	}
	
	
	public MyByteArrayOutputStream(int len){
		mBuffer = new byte[len];
		curPos = 0;
		mMaxPos = len;
	}
	
	public byte[] getData(){
		return mBuffer;
	}
	
	public void writebyte(int value) {
		mBuffer[curPos++] = (byte)(value & 0xff);
		
		CHECKSUM += (byte)(value & 0xff);
		return ;
	}
	
	public void write2byte(int value) {
		mBuffer[curPos++] = (byte)(value & 0xff);
		CHECKSUM += (byte)(value & 0xff);
		mBuffer[curPos++] = (byte)((value & 0xff00)>>8);
		CHECKSUM += (byte)((value & 0xff00)>>8);
		
		return ;
	}
	
	public void write3byte(int value) {
		mBuffer[curPos++] = (byte)(value & 0xff);
		CHECKSUM += (byte)(value & 0xff);
		mBuffer[curPos++] = (byte)((value & 0xff00)>>8);
		CHECKSUM += (byte)((value & 0xff00)>>8);
		mBuffer[curPos++] = (byte)((value & 0xff0000)>>16);
		CHECKSUM += (byte)((value & 0xff0000)>>16);
		
		return ;
	}
	
	public void write4byte(int value) {
		mBuffer[curPos++] = (byte)(value & 0xff);
		CHECKSUM += (byte)(value & 0xff);
		mBuffer[curPos++] = (byte)((value & 0xff00)>>8);
		CHECKSUM += (byte)((value & 0xff00)>>8);
		mBuffer[curPos++] = (byte)((value & 0xff0000)>>16);
		CHECKSUM += (byte)((value & 0xff0000)>>16);
		mBuffer[curPos++] = (byte)((value & 0xff000000)>>24);
		CHECKSUM += (byte)((value & 0xff000000)>>24);
		
		return ;
	}
	
	public void writeNbyte(byte[] value, int len) {
		System.arraycopy(value, 0, mBuffer, curPos, len);
		curPos += len;
		
		for (int i = 0; i < value.length; i++) {
			CHECKSUM += value[i] & 0xff;
		}
		
		return ;
	}
	
	public void writebyteForDoubla(double dd) {
		byte bb = 0;
		bb += (int)dd << 4;
		bb += (dd - (int)dd)*10;
		mBuffer[curPos++] = bb;
		
		CHECKSUM += dd;
		return ;
	}
	
	public void writeChecksum() {
		mBuffer[curPos++] = (byte)((CHECKSUM) & 0xff);
		
		return ;
	}
}
