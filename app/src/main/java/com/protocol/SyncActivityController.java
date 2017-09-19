package com.protocol;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.blespp.client.DeviceData;
import com.controllers.ApplicationEx;
import com.hapilabs.lightweight.R;
import com.ui.custom.SyncActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class SyncActivityController {
	Handler handler;
	Context context;
	public DeviceData mData;

	public SyncActivityController(Context context) {
		this.context = context;
		handler = new Handler();

	}


	public void sendConnection(String device_id, DeviceData devicedata) {

		XMLHelper helper = new XMLHelper();
		String data = helper.sendDeviceInfo(WebServices.COMMAND.SEND_CONNECT,devicedata );
		Connection connection = new Connection();

		System.out.println("sendConnection@SyncActivityControl: deviceIDinHex - " + ApplicationEx.getInstance().statsData.deviceIDinHex);
		connection.send_connection(device_id,  sendConnectionHandler,data);


	}
	public void sendData(String device_id,DeviceData devicedata) {

		System.out.println("sendData: deviceid: " + device_id);

		XMLHelper helper = new XMLHelper();
		String data = helper.sendDeviceData(WebServices.COMMAND.SEND_DATA,devicedata );
		Connection connection = new Connection();
		connection.sendData(device_id, sendDataHandler,data);

	}




	final Handler sendConnectionHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case Connection.REQUEST_START:
				//show progress bar here
				System.out.println("START HERE");
				break;
			case Connection.REQUEST_SUCCESS:
				//  TODO: 
				//                	System.out.println("SUCCESS HERE@ sendConnectionHandler SEND DATA"+" "+msg.obj);
				updateProgress();
				sendData(mData.deviceID,mData);

				/*if (msg.obj !=null && msg.obj instanceof String){
                		String message = msg.obj.toString();
                		if (message.contains("Successful".toLowerCase())){
                			System.out.println("SUCCESS HERE@ sendConnectionHandler SEND DATA");
                            sendData(mData.deviceID,mData);
                      } else 
                	    	Toast.makeText(context, "Error syncing your fork:  "+msg.obj.toString(), Toast.LENGTH_SHORT).show();

                	}*/

				break;
			case Connection.REQUEST_ERROR:
				// TODO:
				if (msg.obj !=null && msg.obj instanceof String)
					Toast.makeText(context, ((SyncActivity)context).getResources().getString(R.string.connect_hapifork_first) +msg.obj.toString(), Toast.LENGTH_SHORT).show();

				//                	Toast.makeText(context, "Error syncing your fork:  "+msg.obj.toString(), Toast.LENGTH_SHORT).show();


				break;

			}//end switch
		}

	};


	private void updateProgress(){
		((SyncActivity)context).showProgressDialog("Sending data to server...");

	}
	private void clearFork(){
		((SyncActivity)context).showProgressDialog("Successfully synced your fork");

		((SyncActivity)context).setClearMem("Successfully synced your fork");

	}
	final Handler sendDataHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case Connection.REQUEST_START:
				//show progress bar here
				System.out.println("START HERE");
				break;
			case Connection.REQUEST_SUCCESS:
				System.out.println("SUCCESS HERE");
				Toast.makeText(context, "Successfully synced your fork:  ", Toast.LENGTH_SHORT).show();

				saveSyncDate();

				((SyncActivity)context).refreshSyncList();

				clearFork();

				/*if (msg.obj !=null && msg.obj instanceof String){
                		String message = msg.obj.toString();
                		if (message.contains("Successful".toLowerCase())){
                			System.out.println("SUCCESS HERE@ sendDataHandlersendDataHandler SEND DATA");
                			Toast.makeText(context, "Successfully synced your fork:  "+msg.obj.toString(), Toast.LENGTH_SHORT).show();
                      } else 
                	    	Toast.makeText(context, "Error syncing your fork:  "+msg.obj.toString(), Toast.LENGTH_SHORT).show();

                	}*/
				break;
			case Connection.REQUEST_ERROR:
				if (msg.obj !=null && msg.obj instanceof String)
					Toast.makeText(context, "Error syncing your fork:  "+msg.obj.toString(), Toast.LENGTH_SHORT).show();
				// TODO:

				break;

			}//end switch
		}

	};

	protected void saveSyncDate() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getDefault());
		String dateNow = sdf.format(new Date());

		System.out.println("SAVE SYNC DAte timezone: " + TimeZone.getDefault());
		System.out.println("SAVE SYNC DAte: " + dateNow);

		((SyncActivity)context).saveDateToSharedPreferences(dateNow);

	}

}
