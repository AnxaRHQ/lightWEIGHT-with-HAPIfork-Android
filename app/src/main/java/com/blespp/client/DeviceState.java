package com.blespp.client;

import android.bluetooth.BluetoothDevice;

public class DeviceState {
	public static final int CONNECT_STATE_DISCONNECT = 0;
	public static final int CONNECT_STATE_CONNECTED = 1;
	
	private static DeviceState mInstance;
	public static DeviceState instance(){
		if (mInstance == null) {
			mInstance = new DeviceState();
		}
		return mInstance;
	}

	public BluetoothDevice mDevice = null;
	public CustomService mService;
	
	public int mDeviceBondState;
	public int mDeviceConnectionState = CONNECT_STATE_DISCONNECT;
	public boolean mIsRemoveBondPressed = false;
	
	public boolean isNull(){
		if (mDevice == null || mService == null) {
			return true;
		}
		return false;
	}
	
	public boolean isConnect(){
		if (mDeviceConnectionState == CONNECT_STATE_CONNECTED) {
			return true;
		}else{
			return false;
		}
	}
	
}
