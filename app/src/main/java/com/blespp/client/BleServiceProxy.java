package com.blespp.client;

import com.controllers.ApplicationEx;
import com.controllers.StatsData.MODE;

import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class BleServiceProxy {
	public static final String TAG = "BleServiceProxy";

	public byte[] lastDataSent;
	private DeviceState mDeviceState;

	private ForkProtocol mProtocol;

	public BleServiceProxy(DeviceState device, DeviceData data){
		mDeviceState = device;
		mProtocol = new ForkProtocol(data);
	}


	int recount = 0;
	public void connectBLE(){


		if (mDeviceState.isNull()){
			return ;
		}

		int statu = mDeviceState.mService.getConnectStatu(mDeviceState.mDevice);
		Log.d(TAG, "connectBLE: " + BluetoothProfile.STATE_CONNECTED + "**" +statu);

		if (statu != BluetoothProfile.STATE_CONNECTED && statu != BluetoothProfile.STATE_CONNECTING) {
			//			mDeviceState.mService.connect(mDeviceState.mDevice, false);
			if(mDeviceState.mService.connect(mDeviceState.mDevice.getAddress())){
				ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);
			}else{
				ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);
			}
		}

		Log.d(TAG, "connectBLE: " + BluetoothProfile.STATE_CONNECTED + "**" +statu);
	}

	public void disconnectBLE(){
		if (mDeviceState.isNull()){
			return ;
		}
		int statu = mDeviceState.mService.getConnectStatu(mDeviceState.mDevice);
		if (statu != BluetoothProfile.STATE_DISCONNECTED && statu != BluetoothProfile.STATE_DISCONNECTING) {
			mDeviceState.mService.disconnect();
		}

		ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);
	}

	public void discoverServices(){
		if (!mDeviceState.isNull()){
			mDeviceState.mService.discoverServices(mDeviceState.mDevice);
		}
	}

	public void discoverAllBleServices(){
		if (!mDeviceState.isNull()){
			mDeviceState.mService.discoverServices(mDeviceState.mDevice);
		}

	}

	public void registerWatcher(){
		if (!mDeviceState.isNull()){
			mDeviceState.mService.enableCustomNotification(mDeviceState.mDevice);
		}
	}

	public void unregisterWatcher(){
		if (!mDeviceState.isNull()){
			mDeviceState.mService.disableCustomNotification(mDeviceState.mDevice);
		}
	}

	// ÒÆ³ýÅä¶Ô
	public void removeBond() {
		if (!mDeviceState.isNull()){
			//			mDeviceState.mService.removeBond(mDeviceState.mDevice);
			Log.d("removeBond", TAG);
		}
	}

	public void sendCommand1(){
		byte[] data = mProtocol.prepareDataCommand1();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand2(){
		byte[] data = mProtocol.prepareDataCommand2();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand3(int s_version,int s_version_minor, int h_version,int h_version_minor, String deviceID){
		byte[] data = mProtocol.prepareDataCommand3(s_version,s_version_minor, h_version, h_version_minor, deviceID);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand4(){
		byte[] data = mProtocol.prepareDataCommand4();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand5(int curTime){
		byte[] data = mProtocol.prepareDataCommand5(curTime);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand6(int startpos, int count){
		byte[] data = mProtocol.prepareDataCommand6(startpos, count);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand7(){
		byte[] data = mProtocol.prepareDataCommand7();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand8(int model){
		byte[] data = mProtocol.prepareDataCommand8(model);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand9(){
		byte[] data = mProtocol.prepareDataCommand9();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand10(int warning_time, int shock_level){
		byte[] data = mProtocol.prepareDataCommand10(warning_time, shock_level);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand11(){
		byte[] data = mProtocol.prepareDataCommand11();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand12(int full_power, int empty_power, int low_power){
		byte[] data = mProtocol.prepareDataCommand12(full_power, empty_power, low_power);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand13(){
		byte[] data = mProtocol.prepareDataCommand13();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand14(int touch_sensitive){
		byte[] data = mProtocol.prepareDataCommand14(touch_sensitive);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand15(){
		byte[] data = mProtocol.prepareDataCommand15();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand16(int shock_enbale){
		byte[] data = mProtocol.prepareDataCommand16(shock_enbale);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand17(){

		byte[] data = mProtocol.prepareDataCommand17();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand18(int referA_enbale, int referA_angle_min, int referA_angle_max){

		byte[] data = mProtocol.prepareDataCommand18(referA_enbale, referA_angle_min, referA_angle_max);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand19(){

		byte[] data = mProtocol.prepareDataCommand19();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand20(int referB_enbale, int referB_angle_min, int referB_angle_max){

		byte[] data = mProtocol.prepareDataCommand20(referB_enbale, referB_angle_min, referB_angle_max);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand21(){

		byte[] data = mProtocol.prepareDataCommand21();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand22(int BLE_broadcast_time){

		byte[] data = mProtocol.prepareDataCommand22(BLE_broadcast_time);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand23(){

		byte[] data = mProtocol.prepareDataCommand23();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand24(int log1_warning_max, int log2_warning_max){

		byte[] data = mProtocol.prepareDataCommand24(log1_warning_max, log2_warning_max);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand25(){

		byte[] data = mProtocol.prepareDataCommand25();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand26(int timeout){

		byte[] data = mProtocol.prepareDataCommand26(timeout);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand27(){

		byte[] data = mProtocol.prepareDataCommand27();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand28(int continuous_touch){

		byte[] data = mProtocol.prepareDataCommand28(continuous_touch);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand29(){

		byte[] data = mProtocol.prepareDataCommand29();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand30(){

		byte[] data = mProtocol.prepareDataCommand30();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand31(){

		byte[] data = mProtocol.prepareDataCommand31();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand32(){

		byte[] data = mProtocol.prepareDataCommand32();
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}

	public void sendCommand33(int led_enabled){

		byte[] data = mProtocol.prepareDataCommand33(led_enabled);
		lastDataSent=data;
		mDeviceState.mService.writeCustomData(mDeviceState.mDevice, data);
	}
}
