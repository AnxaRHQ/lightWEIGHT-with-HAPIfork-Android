package com.blespp.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.controllers.ApplicationEx;
import com.controllers.StatsData.MODE;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class CustomService extends Service {

	//	public static final UUID HRP_SERVICE = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
	//    public static final UUID DEVICE_INFORMATION = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
	//    public static final UUID HEART_RATE_MEASUREMENT_CHARAC = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
	public static final UUID CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	//    public static final UUID BODY_SENSOR_LOCATION = UUID.fromString("00002A38-0000-1000-8000-00805f9b34fb");
	//    public static final UUID SERIAL_NUMBER_STRING = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
	//    public static final UUID MANUFATURE_NAME_STRING = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
	//    public static final UUID ICDL = UUID.fromString("00002A2A-0000-1000-8000-00805f9b34fb");
	//    public static final UUID HeartRate_ControlPoint = UUID.fromString("00002A39-0000-1000-8000-00805f9b34fb");
	//    public static final UUID DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
	//    public static final UUID FIRMWARE_REVISON_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");

	static final String TAG = "CustomService";

	public static final String CUSTOM_BROADCAST = "com.blespp.client.CustomService.action.VALUES_CHANGE";
	public static final String CUSTOM_ATTR_MSG = "custom_msg";
	public static final String CUSTOM_ATTR_COMMAND = "command";

	public static final int CUSTOM_CONNECT_MSG = 1;
	public static final int CUSTOM_DISCONNECT_MSG = 2;
	public static final int CUSTOM_READY_MSG = 3;
	public static final int CUSTOM_VALUE_MSG = 4;
	public static final int GATT_DEVICE_FOUND_MSG = 5;

	/** Source of device entries in the device list */
	public static final int DEVICE_SOURCE_SCAN = 0;
	public static final int DEVICE_SOURCE_BONDED = 1;
	public static final int DEVICE_SOURCE_CONNECTED = 2;
	public static final int RESET_ENERGY_EXPANDED = 1;

	/** Intent extras */
	public static final String EXTRA_DEVICE = "DEVICE";
	public static final String EXTRA_RSSI = "RSSI";
	public static final String EXTRA_SOURCE = "SOURCE";
	public static final String EXTRA_ADDR = "ADDRESS";
	public static final String EXTRA_CONNECTED = "CONNECTED";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_UUID = "UUID";
	public static final String EXTRA_VALUE = "VALUE";
	public static final String BSL_VALUE = "com.siso.ble.hrpservice.bslval";
	public static final String HRM_VALUE = "com.siso.ble.hrpservice.hrmval";
	public static final String SERIAL_STRING = "com.siso.ble.hrpservice.serialstring";
	public static final String MANF_NAME = "com.siso.ble.hrpservice.manfname";
	public static final String ICDL_VALUE = "com.siso.ble.hrpservice.icdl";
	public static final String HRM_EEVALUE = "com.siso.ble.hrpservice.eeval";
	public static final String HRM_RRVALUE = "com.siso.ble.hrpservice.rrval";

	public static final int ADV_DATA_FLAG = 0x01;
	public static final int LIMITED_AND_GENERAL_DISC_MASK = 0x03;
	public static final int FIRST_BITMASK = 0x01;
	public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
	public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
	public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
	public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
	public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
	public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
	public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;


	private Handler mActivityHandler = null;
	private Handler mDeviceListHandler = null;
	public boolean isNoti = false;

	private int mConnectionState = STATE_DISCONNECTED;


	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;



	// Implements callback methods for GATT events that the app cares about.  For example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {

				sendtoBroadcast(CUSTOM_ATTR_MSG, CUSTOM_CONNECT_MSG);

				mBluetoothGatt.discoverServices();

				mConnectionState = STATE_DISCONNECTED;

				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:" +
						mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

				sendtoBroadcast(CUSTOM_ATTR_MSG, CUSTOM_DISCONNECT_MSG);

				mConnectionState = STATE_CONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				sendtoBroadcast(CUSTOM_ATTR_MSG, CUSTOM_READY_MSG);
				DummyReadForSecLevelCheck(gatt.getDevice());
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}

		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic,
				int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				UUID charUuid = characteristic.getUuid();

				Log.d(TAG, "onCharacteristicRead = "+charUuid);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.i(TAG, "onCharacteristicChanged");
			//          Bundle mBundle = new Bundle();
			//          Message msg = Message.obtain(mActivityHandler, CUSTOM_VALUE_MSG);
			int hrmval = 0;
			int eeval = -1;
			ArrayList<Integer> rrinterval = new ArrayList<Integer>();
			int length = characteristic.getValue().length;
			UUID cuuid = characteristic.getUuid();

			Log.i(TAG, "onCharacteristicChanged"+cuuid+ " "+CHARACTERISTIC_NOTIFY);
			//	           
			if (cuuid.equals(CHARACTERISTIC_NOTIFY)) {

				byte[] data = characteristic.getValue();

				//mProtocol.parseData(new DataInputStream(new ByteArrayInputStream(data)));
				int command = mProtocol.parseData(new MyByteArrayInputSream(data));

				//				mBundle.putByteArray("data", data);
				//	            msg.setData(mBundle);
				//	            msg.sendToTarget();
				sendtoBroadcast(CUSTOM_ATTR_MSG, CUSTOM_VALUE_MSG, CUSTOM_ATTR_COMMAND, command);

				Log.d(TAG, "getValue" + data);
				Log.d(TAG, "command" + command);
				return ;
			}
		}
	};

	public void sendtoBroadcast(String name, int attr){
		Intent intent = new Intent(CUSTOM_BROADCAST);
		intent.putExtra(name, attr);
		sendBroadcast(intent);
	}

	public void sendtoBroadcast(String name, int attr, String name2, int attr2){

		Log.d(TAG, "sendtoBroadcast");
		Intent intent = new Intent(CUSTOM_BROADCAST);
		intent.putExtra(name, attr);
		intent.putExtra(name2, attr2);
		sendBroadcast(intent);
	}

	private final IBinder binder = new LocalBinder();
	public class LocalBinder extends Binder {
		public CustomService getService() {
			return CustomService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}



	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that BluetoothGatt.close() is called
		// such that resources are cleaned up properly.  In this particular example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address The device address of the destination device.
	 *
	 * @return Return true if the connection is initiated successfully. The connection result
	 *         is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device.  Try to reconnect.
		if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The disconnection result
	 * is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure resources are
	 * released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
	 * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic Characteristic to act on.
	 * @param enabled If true, enable notification.  False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
			boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This should be
	 * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null) return null;

		return mBluetoothGatt.getServices();
	}

	
	@Override
	public void onCreate() {

		initialize();
		mProtocol = new ForkProtocol(DeviceData.instance());
	}

	public void setActivityHandler(Handler mHandler) {
		Log.d(TAG, "Activity Handler set");
		mActivityHandler = mHandler;
	}

	public void setDeviceListHandler(Handler mHandler) {
		Log.d(TAG, "Device List Handler set");
		mDeviceListHandler = mHandler;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy() called");
		if (mBluetoothAdapter != null && mBluetoothGatt != null) {
			//	            mBluetoothGatt./closeProfileProxy(BluetoothGattAdapter.GATT, mBluetoothGatt);
		}
		super.onDestroy();
	}


	/*
	 * Broadcast mode checker API
	 */
	public boolean checkIfBroadcastMode(byte[] scanRecord) {
		int offset = 0;
		while (offset < (scanRecord.length - 2)) {
			int len = scanRecord[offset++];
			if (len == 0)
				break; // Length == 0 , we ignore rest of the packet
			// TODO: Check the rest of the packet if get len = 0

			int type = scanRecord[offset++];
			switch (type) {
			case ADV_DATA_FLAG:

				if (len >= 2) {
					// The usual scenario(2) and More that 2 octets scenario.
					// Since this data will be in Little endian format, we
					// are interested in first 2 bits of first byte
					byte flag = scanRecord[offset++];
					/*
					 * 00000011(0x03) - LE Limited Discoverable Mode and LE
					 * General Discoverable Mode
					 */
					if ((flag & LIMITED_AND_GENERAL_DISC_MASK) > 0)
						return false;
					else
						return true;
				} else if (len == 1) {
					continue;// ignore that packet and continue with the rest
				}
			default:
				offset += (len - 1);
				break;
			}
		}
		return false;
	}

	public void DummyReadForSecLevelCheck(BluetoothDevice device) {
		boolean result = false;
		if (mBluetoothGatt != null && device != null) {
			BluetoothGattService disService = mBluetoothGatt.getService(SERVICE_CUSTOM);
			if (disService == null) {
				showMessage("SERVICE_CUSTOM service not found!");
				return;
			}

			BluetoothGattCharacteristic firmwareIdCharc = disService.getCharacteristic(CHARACTERISTIC_NOTIFY);
			if (firmwareIdCharc == null) {
				showMessage("CHARACTERISTIC_NOTIFY charateristic not found!");
				return;
			}

		}
	}

	public boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
		if (mBluetoothGatt == null)
			return false;
		if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable))
			return false;

		BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(CCC);
		if (clientConfig == null)
			return false;

		if (enable) {
			Log.i(TAG,"enable notification");
			clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		} else {
			Log.i(TAG,"disable notification");
			clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}
		return mBluetoothGatt.writeDescriptor(clientConfig);
	}

	public int getConnectStatu(BluetoothDevice device){

		if (mBluetoothGatt != null) {
			return mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT_SERVER);
			//	            return mBluetoothGatt.getConnectionState(device);
		}
		return BluetoothProfile.STATE_DISCONNECTED;
	}



	public boolean isBLEDevice(BluetoothDevice device) {
		//	        return mBluetoothGatt.
		return true;
	}

	private void showMessage(String msg) {
		Log.e(TAG, msg);
	}


	//***************************************************************
	//						
	//***************************************************************
	public static final UUID SERVICE_CUSTOM = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID CHARACTERISTIC_WRITE = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
	public static final UUID CHARACTERISTIC_NOTIFY = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

	public void discoverServices(BluetoothDevice device){
		boolean result = false;
		if (mBluetoothGatt == null && device == null) {
			return ;
		}

		result = mBluetoothGatt.discoverServices();
		if (!result) {
			Log.d(TAG, "discoverServices fatil");
		}
	}

	private ForkProtocol mProtocol;

	public void enableCustomNotification(BluetoothDevice device) {
		boolean result = false;
		Log.i(TAG, "enableHRNotification ");
		isNoti = true;

		//	        mBluetoothGatt.getConnectedDevices();
		mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);

		BluetoothGattService mHRP = mBluetoothGatt.getService(SERVICE_CUSTOM);
		if (mHRP == null) {
			Log.e(TAG,  mBluetoothGatt+" "+"HRP service not found!");
			return;
		}
		BluetoothGattCharacteristic mHRMcharac = mHRP.getCharacteristic(CHARACTERISTIC_NOTIFY);
		if (mHRMcharac == null) {
			Log.e(TAG, "charateristic not found!");
			return;
		}
		BluetoothGattDescriptor mHRMccc = mHRMcharac.getDescriptor(CCC);
		if (mHRMccc == null) {
			Log.e(TAG, "charateristic not found!");
			return;
		}

		result = enableNotification(true, mHRMcharac);
		if (!result) {
			Log.e(TAG, "enableCustomNotification failed!");
			return;
		}
	}

	public void disableCustomNotification(BluetoothDevice device) {
		boolean result = false;
		Log.i(TAG, "enableHRNotification ");
		isNoti = true;
		BluetoothGattService mHRP = mBluetoothGatt.getService(SERVICE_CUSTOM);
		if (mHRP == null) {
			Log.e(TAG, "HRP service not found!");
			return;
		}
		BluetoothGattCharacteristic mHRMcharac = mHRP.getCharacteristic(CHARACTERISTIC_NOTIFY);
		if (mHRMcharac == null) {
			Log.e(TAG, " charateristic not found!");
			return;
		}
		BluetoothGattDescriptor mHRMccc = mHRMcharac.getDescriptor(CCC);
		if (mHRMccc == null) {
			Log.e(TAG, "CCC charateristic not found!");
			return;
		}

		result = enableNotification(false, mHRMcharac);
		if (!result) {
			Log.e(TAG, "enableCustomNotification failed!");
			return;
		}
	}

	public void writeCustomData(BluetoothDevice device, byte[] data){
		boolean result = false;

		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		if (mBluetoothManager == null) {
			Log.e(TAG, "Unable to initialize BluetoothManager.");
			return ;
		}

		try{
			BluetoothGattService mHRP = mBluetoothGatt.getService(SERVICE_CUSTOM);
			if (mHRP == null) {
				Log.e(TAG, "HRP service not found!");
				return;
			}
			BluetoothGattCharacteristic mHRMcharac = mHRP.getCharacteristic(CHARACTERISTIC_WRITE);
			if (mHRMcharac == null) {
				Log.e(TAG, "charateristic not found!");
				return;
			}
			result = mHRMcharac.setValue(data);
			if (result == false) {
				Log.e(TAG, "writeCustomData - setValue() is failed");
				return;
			}

			result = mBluetoothGatt.writeCharacteristic(mHRMcharac);
			if (result == false) {
				Log.e(TAG, "writeCustomData() is failed");
				return;
			}

		}catch(NullPointerException e){
			Log.e(TAG, e.toString());

		}
	}


}
