package com.blespp.client;

import java.io.DataInputStream;
import java.io.IOException;

public class MyByteArrayInputSream {

	public byte[] mBuffer;
//	private byte[] mBuffer;
	private int curPos = 0;
	private int mMaxPos = 0;
	
	public int CHECKSUM = 0x00;
	
	public MyByteArrayInputSream(byte[] buf){
		mBuffer = buf;
		curPos = 0;
		mMaxPos = buf.length - 1;
	}
	
	public int readbyte() {
		CHECKSUM += (byte)(mBuffer[curPos]);
		return mBuffer[curPos++] & 0xff;
	}
	
	byte[] b = new byte[4];
	public int read2byte() {
		b[0] = mBuffer[curPos++];
		b[1] = mBuffer[curPos++];
		CHECKSUM += (byte)(b[0]);
		CHECKSUM += (byte)(b[1]);
		return ( ((int)b[1] & 0xff) << 8
				| ((int)b[0] & 0xff) );
	}

	public int read3byte() {
		b[0] = mBuffer[curPos++];
		b[1] = mBuffer[curPos++];
		b[2] = mBuffer[curPos++];
		CHECKSUM += (byte)(b[0]);
		CHECKSUM += (byte)(b[1]);
		CHECKSUM += (byte)(b[2]);
		return ( ((int)b[2] & 0xff) << 16 
				| ((int)b[1] & 0xff) << 8
				| ((int)b[0] & 0xff) );
	}
	
	public long read4byte()  {
		b[0] = mBuffer[curPos++];
		b[1] = mBuffer[curPos++];
		b[2] = mBuffer[curPos++];
		b[3] = mBuffer[curPos++];
		CHECKSUM += (byte)(b[0]);
		CHECKSUM += (byte)(b[1]);
		CHECKSUM += (byte)(b[2]);
		CHECKSUM += (byte)(b[3]);
		return ( ((long)b[3] & 0xff) << 24 
				| ((long)b[2] & 0xff) << 16 
				| ((long)b[1] & 0xff) << 8
				| ((long)b[0] & 0xff) );
		
	}
	
	public byte[] readNbyte(int len){
		byte[] buf = new byte[len];
		int tmpSum=0;
		System.arraycopy(mBuffer, curPos, buf, 0, len);
		for (int i=0;i<len;i++)
			tmpSum += (buf[i]);
		CHECKSUM+=(byte)tmpSum;
		curPos += len;
		
		return buf;
	}
	
	public double readbyteForDouble() {
		CHECKSUM += (byte)(mBuffer[curPos]);
		int val = mBuffer[curPos++] & 0xff;
		double dd = 0.0;
		dd += val & 0xf0 >> 4;
		dd += (double)(val & 0x0f) / 10;
		return dd;
	}
}
