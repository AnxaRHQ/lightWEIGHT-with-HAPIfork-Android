package com.ui.custom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.blespp.client.BleServiceProxy;
import com.blespp.client.CustomService;
import com.blespp.client.DeviceData;
import com.blespp.client.DeviceListActivity;
import com.blespp.client.DeviceState;
import com.blespp.client.ForkProtocol;
import com.controllers.ApplicationEx;
import com.controllers.StatsData.MODE;
import com.hapilabs.lightweight.R;
import com.protocol.AppUtil;
import com.protocol.SyncActivityController;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SyncActivity extends CommonActivity implements OnClickListener {

    public static final String TAG = "SyncActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_SELECT_DEVICE = 2;
    private static final int REQUEST_LOADWEBKIT = 3;

    boolean isDeviceListShowing = false; //flag to make sure that only oe dialog is display at one time

    DeviceData mData;

    boolean isSyncReady = false;

    private String deviceAddress;

    ImageView quick_action;
    com.ui.custom.DemoPopupWindow dwLeft;

    private Hashtable<String, String> outData;

    public static boolean falgSend = false;
    public static int recvCount = 0;

    Context mContext;


    //*UI VIEW*/
    ImageView sync_button;
    TextView headerMessage;
    TextView headerMessageStatus;
    ImageView statsIcon;
    LinearLayout headerSecondary;

    ListView syncListView;

    //BLE FUNCTION
    private BluetoothDevice mDevice = null;
    private CustomService mService;
    private BleServiceProxy mProxy;
    private Handler mHandler = new Handler();
    BluetoothAdapter mBluetoothAdapter = null;

    //BLE Controller for connection functions
    SyncActivityController controller;

    ArrayList<String> syncList;
    ArrayAdapter<String> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_sync);

        controller = new SyncActivityController(mContext);

        initUI();

        isBluetoothEnabled();

        initBleReceiver();

        syncList = retrieveHistory();
        refreshListView();

        if (ApplicationEx.getInstance().hasHAPIforkPairedToTheApp) {
            displayDeviceList();
        }else{
            ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);
        }

    }


    @Override
    public void onResume() {
        // TODO record here the current time resume the counter based onPause time and ONResumeTime

        if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
            //			mProxy.sendCommand8(1);
        }

        updateHeaderStatus();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

//        if (mProxy != null) {
//            mProxy.disconnectBLE();
//        }

//        unbindService(mServiceConnection);
        unregisterReceiver(mBroadcastReceiver);
//        stopService(new Intent(this, CustomService.class));
    }


    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            BluetoothDevice localBDevice = DeviceState.instance().mDevice;
            String action = intent.getAction();

            if (!action.equals(CustomService.CUSTOM_BROADCAST)) {
                return;
            }

            int msg = intent.getIntExtra(CustomService.CUSTOM_ATTR_MSG, 0);
            System.out.println("SyncActivity##########################");
            System.out.println("SyncActivitymBroadcastReceiver@SyncActivity:  RECIEVED CUSTOM BROADCAST: " +
                    " MODE = " + ApplicationEx.getInstance().statsData.getAppMode() +
                    " TYPE = " + msg);

            if (msg == CustomService.CUSTOM_CONNECT_MSG || msg == CustomService.CUSTOM_READY_MSG) {
                System.out.println(TAG + "CustomService.CUSTOM_CONNECT_MSG || CustomService.CUSTOM_READY_MSG");

                try {
                    outData.put("Device", localBDevice.getName());
                    outData.put("Status", "connected");
                    ApplicationEx.getInstance().statsData.deviceID = localBDevice.getAddress();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);

                updateHeaderStatus();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mService.enableCustomNotification(mDevice);
                    }
                }, 1000L);

                mProxy.sendCommand31();

                setBluetooth();


            } else if (msg == CustomService.CUSTOM_DISCONNECT_MSG) {
                System.out.println(TAG + " CustomService.CUSTOM_DISCONNECT_MSG");
                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);

                updateHeaderStatus();
                disconnectBlueTooth(localBDevice);

            } else if (msg == CustomService.CUSTOM_VALUE_MSG) { // Notification data
                System.out.println(TAG + " CustomService.CUSTOM_CONNECT_MSG || CustomService.CUSTOM_READY_MSG");

                int command = intent.getIntExtra(CustomService.CUSTOM_ATTR_COMMAND, 0);

                System.out.println(TAG + " CUSTOM_ATTR_COMMAND: " + command);

                if ((ApplicationEx.getInstance().statsData.getAppMode() != MODE.MANUAL) && (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT)) {

                    switch (command) {
                        case ForkProtocol.RX_COMMAND_4:

                            if (isSyncReady == false)
                                isSyncReady = true;
                            updateHeaderStatus();

                            //get logs
                        case ForkProtocol.RX_COMMAND_6:
                            recvCount--;
                            if (recvCount <= 0) {
                                falgSend = false;
                            }
                            Log.d(TAG, "recvCount: " + recvCount);
                            //						updateLog();
                            break;
                        //working mode
                        case ForkProtocol.RX_COMMAND_8:
                            updateHeaderStatus();
                            break;
                        case ForkProtocol.RX_COMMAND_31:
                            mProxy.sendCommand1();
                            setBluetooth();
                        case ForkProtocol.RX_COMMAND_1:
                            mProxy.sendCommand2();
                            break;
                        case ForkProtocol.RX_COMMAND_5:

                            mProxy.sendCommand29();
                            //	mProxy.sendCommand(AppUtil.getDateNow());
                            break;
                        case ForkProtocol.RX_COMMAND_2:
                            mProxy.sendCommand4();


                        case ForkProtocol.RX_COMMAND_29:
                            mProxy.sendCommand4();
                            disconnectBlueTooth(localBDevice);
                            break;
                        default:
                            break;
                    }

                }//end if status else


            }
            System.out.println("SyncActivity ##########################");

        }

    };

    /*
     * Broadcast Receiver for Bluetooth Device Settings on your Mobile.
     * Example: Bluetooth is On, OFF, Bluetooth turnedoff
     *
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(TAG + " onActivityResult: "+ requestCode + "*" + resultCode);
        switch (requestCode) {
            case REQUEST_LOADWEBKIT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    int quickaction = data.getIntExtra("quickaction", 0);
                    onClickQuickAction(quickaction);
                }
                break;
            case REQUEST_SELECT_DEVICE:
                isDeviceListShowing = false;
                if (resultCode == Activity.RESULT_OK && data != null) {

                    deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    DeviceState.instance().mDevice = mDevice;

                    mProxy.connectBLE();

                    outData = new Hashtable<String, String>();
                    outData.put("Device", "xxx");
                    outData.put("Status", "xxx");
                    outData.put("RSSI", "xxx");

                    mData = DeviceData.instance();

                    initBleReceiver();

                    Log.d(TAG, "... after connection" + DeviceState.instance().mDevice.getName());
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, getResources().getString(R.string.bluetooth_turned_on), Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, getResources().getString(R.string.problem_bt_turning_on), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                Log.e(TAG, "wrong request Code");
                break;
        }//end switch
    }


    // Code to manage Service lifecycle.

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {

            mService = ((CustomService.LocalBinder) rawBinder).getService();
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            DeviceState.instance().mService = mService;
            // Automatically connects to the device upon successful start-up initialization.
            mService.connect(deviceAddress);
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    //***********************************************************************
    // 							Function INITIALIZATION
    //***********************************************************************


    private void initBleReceiver() {

        registerReceiver(this.mBroadcastReceiver, new IntentFilter(CustomService.CUSTOM_BROADCAST));

        mProxy = new BleServiceProxy(DeviceState.instance(), DeviceData.instance());

        // Start ble Services
        Intent bindIntent = new Intent(this, CustomService.class);
        startService(bindIntent);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        this.registerReceiver(mBroadcastReceiver, filter);

    }

    public void isBluetoothEnabled() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.bluetooth_not_available), Toast.LENGTH_SHORT).show();
            finish();
        } else if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        }
    }


    public synchronized void setClearMem(String message) {
        headerMessageStatus.setText(message);
        //	mProxy.sendCommand29();
        mProxy.sendCommand5((int) AppUtil.getDateNow());

        hideProgressDialog();
    }


    private void disconnectBlueTooth(BluetoothDevice localBDevice) {
        try {
            outData.put("Status", "disconnected");
        } catch (Exception e) {

        }
        updateHeaderStatus();
        /*ApplicationEx.getInstance().statsData.deviceID = "";

		try{
			if (mProxy != null) {

			mProxy.removeBond();
			mService.disconnect(mDevice);
			DeviceState.instance().mDevice= null;
		}
		}catch(Exception e){

		}*/
    }

    private Handler mMsgHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 1) {
//				showProgressDialog("Reading HAPIfork data...");
                showProgressDialog(getResources().getString(R.string.reading_hapifork_data));

            }

            if (msg.what == 2) {
                hideProgressDialog();
                DeviceData.instance();
//				showProgressDialog("Establishing server connection ...");
                showProgressDialog(getResources().getString(R.string.establishing_connection));
                controller.mData = DeviceData.instance();
                controller.sendConnection(ApplicationEx.getInstance().statsData.deviceIDinHex, DeviceData.instance());
            }
        }

    };

    public void setBluetooth() {
        Log.d("setBT@ SyncActivity", "1");
        ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);

        BluetoothDevice localBDevice = DeviceState.instance().mDevice;

        if (localBDevice != null)
            ApplicationEx.getInstance().statsData.deviceID = localBDevice.getAddress();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                mService.enableCustomNotification(mDevice);
            }
        }, 1000L);

        // Unlock BLE, send time from hapifork
        mHandler.postDelayed(new Runnable() {
            public void run() {
                synchronized (mProxy) {
                    //unlock BLE

                    try {
                        mProxy.sendCommand31();
                    } catch (Exception e) {

                    }

                    //TIME command
                    try {
                        mProxy.sendCommand4();
                    } catch (Exception e) {

                    }

                    //working/real time mode, bluetooth
                    try {
                        mProxy.sendCommand8(1);
                    } catch (Exception e) {

                    }

                }
            }
        }, 2000L);

    }

    //***********************************************************************
    // 							CLICK EVENTS
    //***********************************************************************

    private void onClickQuickAction(int position) {

        switch (position) {
            case 0: //bluetooth
//                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);
                ApplicationEx.getInstance().helpDone = true;
//                updateHeaderStatus();

                Intent viewIntent = new Intent(this, ViewActivity.class);
                viewIntent.putExtra("STATUS", "CONNECT");
                startActivity(viewIntent);
                finish();

                break;
            case 1:  //manual
                ApplicationEx.getInstance().statsData.setAppMode(MODE.MANUAL);
                ApplicationEx.getInstance().helpDone = true;
                updateHeaderStatus();

                Intent viewManualIntent = new Intent(this, ViewActivity.class);
                startActivity(viewManualIntent);
                finish();


                //reset all

                break;
            case 2:
                break;
            case 3: {//help
                Intent mainIntent = new Intent(this, WebKitActivity.class);
                mainIntent.putExtra("URL", ApplicationEx.URL_HELP);
                startActivityForResult(mainIntent, REQUEST_LOADWEBKIT);
            }
            break;
            case 4: {//about
                Intent mainIntent = new Intent(this, WebKitActivity.class);
                mainIntent.putExtra("URL", ApplicationEx.URL_ABOUT);
                startActivityForResult(mainIntent, REQUEST_LOADWEBKIT);
            }

            break;
            case 5: { //contact us
                Intent mainIntent = new Intent(this, WebKitActivity.class);
                mainIntent.putExtra("URL", ApplicationEx.URL_CONTACTUS);
                startActivityForResult(mainIntent, REQUEST_LOADWEBKIT);
            }
            break;
            case 6: { //5dietRules
                Intent mainIntent = new Intent(this, WebKitActivity.class);
                mainIntent.putExtra("URL", ApplicationEx.URL_DIET_RULES);
                startActivityForResult(mainIntent, REQUEST_LOADWEBKIT);
            }
            break;
            case 7: { //meal list
                Intent mainIntent = new Intent(this, WebKitActivity.class);
                mainIntent.putExtra("URL", ApplicationEx.URL_MEAL_LIST);
                startActivityForResult(mainIntent, REQUEST_LOADWEBKIT);
            }
            break;
            case 8: { //meal dashboard
                Intent mainIntent = new Intent(this, WebKitActivity.class);
                mainIntent.putExtra("URL", ApplicationEx.URL_MEAL_DASHBOARD);
                startActivityForResult(mainIntent, REQUEST_LOADWEBKIT);
            }
            case 9: { //log out
                finish();
                Intent mainIntent = new Intent(this, TourPageActivity.class);
                mainIntent.putExtra("STATUS", TourPageActivity.STATUS_LOGOUT);
                startActivity(mainIntent);
            }
            break;
        }//end switch


    }

    @Override
    public void onClick(View v) {
//		System.out.println("SyncActivity"+ "ONCLICK");

        // TODO Auto-generated method stub
        if (v == sync_button) {
//			System.out.println("SyncActivity"+ "SYNC");
//
//			System.out.println("SyncActivity"+ "start/done button");
//
//			System.out.println("SyncActivitySTATUS"+isSyncReady+" "+ApplicationEx.getInstance().statsData.getAppMode());

            DeviceData.setTimeDiff();

            //initBleReceiver();


            if (isSyncReady) { // meal button is SYNC

//				System.out.println("SyncActivity "+ApplicationEx.getInstance().statsData.getAppMode());
                if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            // To display the dialog box
                            Message.obtain(mMsgHandler, 1).sendToTarget();

                            int num = 100;    //DeviceData.instance().num;
                            int startpos = 0;
                            int len = 0;

                            int sleepTm = 0;

                            while (num > 0) {
                                if (falgSend) {
                                    try {
                                        Thread.sleep(50);
                                        sleepTm += 50;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (sleepTm < 500) {
                                        continue;
                                    } else {
                                        sleepTm = 0;
                                        //											ContentFragment.falgSend = true;
                                        //											ContentFragment.recvCount = len;
                                        //											mProxy.sendCommand6(startpos, len);
                                    }
                                }
                                sleepTm = 0;

                                if (num > 4) {
                                    len = 4;
                                } else {
                                    len = num;
                                }

                                falgSend = true;
                                recvCount = len;
//								System.out.println("SyncActivity %%%%%%%%%%%%%%%%%%%%%%%% Command 6 +"+num+" "+len);

                                mProxy.sendCommand6(startpos, len);
                                startpos += len;
                                num -= len;

                            }

                            // To close the dialog box
                            Message.obtain(mMsgHandler, 2).sendToTarget();
                        }
                    }).start();


                } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
//					Toast.makeText(this, "Please connect to your hapifork first" , Toast.LENGTH_LONG).show();
                    Toast.makeText(this, getResources().getString(R.string.connect_hapifork_first), Toast.LENGTH_LONG).show();
                }
            } else if (isSyncReady == false) { // meal button is DONE
                if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
//					Toast.makeText(this, "Preparing hapifork data. Please wait" , Toast.LENGTH_LONG).show();
                    Toast.makeText(this, getResources().getString(R.string.preparing_data), Toast.LENGTH_LONG).show();
                } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
//					Toast.makeText(this, "Please connect to your hapifork first" , Toast.LENGTH_LONG).show();
                    Toast.makeText(this, getResources().getString(R.string.connect_hapifork_first), Toast.LENGTH_LONG).show();
                }
            }


        } else if (v == quick_action) {
//			System.out.println("SyncActivity"+ "QA");

            /**isSyncReady = false;
             //working/real time mode, bluetooth
             //if(DeviceState.instance().mDevice == null){
             ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);
             displayDeviceList();
             updateHeaderStatus();

             //}
             **/
            //showaction
            dwLeft = new DemoPopupWindow(v, R.layout.add_quickaction/*pass the view of the quick action*/, this);
            dwLeft.showLikePopDownMenu(true);

        } else if (v.getId() == R.id.quickaction_bluetooth) {
//			System.out.println("ViewActivity"+ "BLE");
            dwLeft.dismiss();
            onClickQuickAction(0);
            /**
             //working/real time mode, bluetooth
             if(DeviceState.instance().mDevice == null){
             ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);
             displayDeviceList();
             updateHeaderStatus();
             }else{
             setBluetooth();
             updateHeaderStatus();
             }

             dwLeft.dismiss();
             dwLeft.dismiss();
             onClickQuickAction(0);
             **/
        }
		else if (v.getId() == R.id.quickaction_manual){
			//manual
			dwLeft.dismiss();
			onClickQuickAction(1);
		}
        else if (v.getId() == R.id.quickaction_sync) {
            //manual
            dwLeft.dismiss();

        } else if (v.getId() == R.id.quickaction_help) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(3);
        } else if (v.getId() == R.id.quickaction_about) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(4);
        } else if (v.getId() == R.id.quickaction_contact) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(5);
        } else if (v.getId() == R.id.quickaction_5diet_rules) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(6);
        } else if (v.getId() == R.id.quickaction_logout) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(9);
        } else if (v == headerSecondary) {
            System.out.println("onClick @ ViewActivity -- headerSecondary");
            isSyncReady = false;
            //working/real time mode, bluetooth
            if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
                if (ApplicationEx.getInstance().hasHAPIforkPairedToTheApp) {
                    displayDeviceList();
                    updateHeaderStatus();
                }

            }
        } else if (v.getId() == R.id.quickaction_mealList) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(7);
        } else if (v.getId() == R.id.quickaction_mealDashboard) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(8);
        }
    }


    //***********************************************************************
    // 							UI DISPLAYS
    //***********************************************************************

	/* @displayDeviceList
     * Call deviceList Activity and display device list
	 **/

    private void displayDeviceList() {

        if (!isDeviceListShowing) { //to make sure that there is only one instance of the dialog
            //check if the the bluetooth is on first
            if (mBluetoothAdapter.isEnabled()) {
                //				if (DeviceState.instance().mDevice == null) {
                isDeviceListShowing = true;
                Intent newIntent = new Intent(SyncActivity.this, DeviceListActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                //				}
            }
        }
    }


    private void initUI() {

        quick_action = (ImageView) findViewById(R.id.header_left);
        quick_action.setOnClickListener(this);

        sync_button = (ImageView) findViewById(R.id.login_btn);
        sync_button.setOnClickListener(this);

        statsIcon = (ImageView) findViewById(R.id.header_ble);

        headerMessage = (TextView) findViewById(R.id.header_message);
        headerMessageStatus = (TextView) findViewById(R.id.header_message_status);

        headerSecondary = (LinearLayout) findViewById(R.id.headersecondary);
        headerSecondary.setOnClickListener(this);

        isSyncReady = false; //ble not yet ready to receive data

        syncListView = (ListView) findViewById(R.id.syncHistoryListView);

    }

    private void updateHeaderStatus() {

        if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
            if (isSyncReady) {
                headerMessageStatus.setText(getResources().getString(R.string.ble_connect_message) + " HAPIFork " + ApplicationEx.getInstance().statsData.deviceIDinHex);
                sync_button.setImageResource(R.drawable.ble_live_sync);
            } else { //if bluetooth is connected but start is not yet pressed
//				headerMessageStatus.setText("Preparing fork for Sync... Please wait");
                headerMessageStatus.setText(getResources().getString(R.string.preparing_for_sync));
                sync_button.setImageResource(R.drawable.ble_live_sync_disable);
            }
            headerMessage.setText(getResources().getString(R.string.ble_message));

            statsIcon.setImageResource(R.drawable.blemode_connected);
            headerMessageStatus.setTextColor(Color.parseColor(ApplicationEx.COLOR_BG_GREEN));

            ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.GONE);


        } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
            statsIcon.setImageResource(R.drawable.blemode_disconnected);
            headerMessage.setText(getResources().getString(R.string.ble_message));
            headerMessageStatus.setText(getResources().getString(R.string.ble_disconnect_message));
            headerMessageStatus.setTextColor(0x000000);
            headerMessageStatus.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_BLACK));
            //if bluetooth is connected but start is not yet pressed
            sync_button.setImageResource(R.drawable.ble_live_start_disconnect);

            ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.GONE);



        } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.MANUAL) {
            statsIcon.setImageResource(R.drawable.manualmode_connected);
            headerMessage.setText(getResources().getString(R.string.manual_message));
            headerMessageStatus.setText("");

            //if blue tooth is connected but start is not yet pressed
            sync_button.setImageResource(R.drawable.ble_live_sync);

            ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.GONE);


        }

    }


    public static ProgressDialog dialog = null;

    public void showProgressDialog(String message) {

        System.out.println("showProgressDialog" + message);
        if (dialog == null || !dialog.isShowing()) {
            dialog = new ProgressDialog(mContext);
            dialog.setTitle("");
            // dialog.setIcon(R.drawable.icon);
            dialog.setMessage(message);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

        } else {
            dialog.setMessage(message);

        }
    }

    private void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();

        }
    }

    private void refreshListView() {

        // Create ArrayAdapter using the sync list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, syncList);

        // Set the ArrayAdapter as the ListView's adapter.
        syncListView.setAdapter(listAdapter);
    }


    private ArrayList<String> retrieveHistory() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        Set<String> set = new HashSet<String>();

        ArrayList<String> list = new ArrayList<String>();

        try {

            set = settings.getStringSet("syncHistory"+ApplicationEx.getInstance().userprofile.getFirstName(), null);
            list = new ArrayList<String>(set);

        } catch (NullPointerException e) {

        }

        System.out.println(TAG + " list: " + list.size());
        return list;
    }

    public void refreshSyncList() {

        syncList = retrieveHistory();
        listAdapter.clear();
        listAdapter.addAll(syncList);
        listAdapter.notifyDataSetChanged();
        syncListView.refreshDrawableState();
    }

    public void saveDateToSharedPreferences(String saveThis) {

        syncList.add(saveThis);

        System.out.println(TAG + " add to list: " + saveThis);

        Set<String> set = new HashSet<String>(syncList);

        //Set the values
        //save the task list to preference
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putStringSet("syncHistory"+ApplicationEx.getInstance().userprofile.getFirstName(), set);
        editor.commit();
    }

    //***********************************************************************
    // 							/**UNUSED METHODS**/
    //***********************************************************************

	/*private void showResetDialog(){

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(SyncActivity.this);

			// Setting Dialog Title
			alertDialog.setTitle("HAPIfork");

			// Setting Dialog Message
			alertDialog.setMessage("Are you sure you want to reset?");

			alertDialog.setIcon(R.id.icon);

			// Setting Positive "Yes" Btn
			alertDialog.setPositiveButton("OK",
			        new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			                // Write your code here to execute after dialog
			            	resetValues();
			            }
			        });
			// Setting Negative "NO" Btn
			alertDialog.setNegativeButton("Cancel",
			        new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {

			                dialog.cancel();
			            }
			        });

			// Showing Alert Dialog
			alertDialog.show();
		}*/


}
