package com.ui.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.blespp.client.BleServiceProxy;
import com.blespp.client.CustomService;
import com.blespp.client.DeviceData;
import com.blespp.client.DeviceState;
import com.blespp.client.ForkProtocol;
import com.controllers.ApplicationEx;
import com.controllers.StatsData.MODE;
import com.hapilabs.lightweight.R;
import com.protocol.AppUtil;
import com.protocol.Connection;
import com.storage.HAPIforkDAO;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PairingActivity extends CommonActivity {

    public static final byte STATUS_SCANNING = 1;
    public static final byte STATUS_LINKING = 2;
    public static final byte STATUS_LINKEDSUCCESS = 3;
    public byte status = STATUS_SCANNING;

    private BluetoothAdapter mBtAdapter;
    private TextView mEmptyList;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    public static final String hapifork = "hapifork";
    public static final String TAG = "DeviceListActivity";
    private CustomService mService = null;

    String deviceAddress;

    List<BluetoothDevice> deviceList;
    ListView newDevicesListView;

    public int somevalue = 10;
    private ServiceConnection onService = null;
    Map<String, Integer> devRssiValues;

    private static final int REQUEST_ENABLE_BT = 1;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    int selectedDevice = 0;
    private boolean mScanning;
    private Handler mHandler;

    private BluetoothDevice selectedBLEDevice;


    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.pairing);

        //remove header icons
        try {
            ((ImageView) findViewById(R.id.header_left)).setVisibility(View.INVISIBLE);
        } catch (Exception e) {

        }

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBtAdapter == null) {
//             Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        onService = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder rawBinder) {
                mService = ((CustomService.LocalBinder) rawBinder).getService();
                if (mService != null) {
                    mService.setDeviceListHandler(mHandler);
                }
                populateList();

            }

            public void onServiceDisconnected(ComponentName classname) {
                mService = null;
            }

        };

        // start service, if not already running (but it is)
        startService(new Intent(this, CustomService.class));
        Intent bindIntent = new Intent(this, CustomService.class);
        bindService(bindIntent, onService, Context.BIND_AUTO_CREATE);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mEmptyList = (TextView) findViewById(R.id.empty);

        RelativeLayout cancelButton = (RelativeLayout) findViewById(R.id.btnrel_1);
        cancelButton.setOnClickListener(this);

        ((RelativeLayout) findViewById(R.id.btnrel_2)).setOnClickListener(this);

        initBleReceiver();

    }


    @Override
    public void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.

        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        populateList();
    }

    private void populateList() {

		/* Initialize device list container */
        Log.d(TAG, "populateList");
        deviceList = new ArrayList<BluetoothDevice>();
        mLeDeviceListAdapter = new LeDeviceListAdapter(this, deviceList);
        devRssiValues = new HashMap<String, Integer>();

        newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mLeDeviceListAdapter);
        newDevicesListView.setVisibility(View.VISIBLE);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            addDevice(pairedDevice, 0);
        }
        scanLeDevice(true);
    }


    DeviceData mData;

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;
        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }
        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            //check if the device is a hapifork
            String mydevice = device.getName();
            if (mydevice == null) {
                mydevice = "";
            }

            if ((mydevice.toLowerCase(Locale.ENGLISH)).contains("hapifork")) {
                mEmptyList.setVisibility(View.GONE);
                newDevicesListView.setVisibility(View.VISIBLE);
                ///add new
                String deviceAddress_select = device.getAddress();

                deviceAddress = device.getAddress();

                DeviceState.instance().mDevice = device;
                selectedBLEDevice = device;

                mProxy.connectBLE();
                mData = DeviceData.instance();

                initBleReceiver();

                Log.d(TAG, "... after connection" + DeviceState.instance().mDevice.getName());
                ///

                deviceList.add(device);
                mLeDeviceListAdapter.notifyDataSetChanged();

                try {
                    mProxy.sendCommand2();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Please make sure your bluetooth is turned on", Toast.LENGTH_LONG).show();
//                    Toast.makeText(this, getResources().getString(R.string.bluetooth_not_available), Toast.LENGTH_SHORT).show();

                }

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(onService);
        scanLeDevice(false);

        unregisterReceiver(mBroadcastReceiver);
        unbindService(mServiceConnection);

    }


    @Override
    protected void onPause() {
        super.onPause();
        //    scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
            if (device == null) return;
            newDevicesListView.setSelection(position);
            selectedDevice = position;
            ((LeDeviceListAdapter) newDevicesListView.getAdapter()).onItemClick(parent, view, position, id);


            if (mScanning) {
                mBtAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
        }
    };

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the
     * title when discovery is finished.
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mLeDeviceListAdapter.getCount() == 0) {
                    mEmptyList.setText(R.string.no_ble_devices);
                }
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                if (!mBtAdapter.isEnabled())
                    finish();
            }
        }
    };

    private class LeDeviceListAdapter extends BaseAdapter implements OnItemClickListener {
        Context context;
        LayoutInflater inflater;

        public LeDeviceListAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            deviceList = new ArrayList<BluetoothDevice>();
        }

        public void addDevice(BluetoothDevice device) {
            if (!deviceList.contains(device)) {
                Log.d("TAG", "add this device");
                deviceList.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return deviceList.get(position);
        }

        public void clear() {
            deviceList.clear();
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }


        @Override
        public Object getItem(int position) {
            return deviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;
            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.device_element, null);
            }

            BluetoothDevice device = deviceList.get(position);
            System.out.println("@getView... " + deviceList.size());
            final TextView tvadd = ((TextView) vg.findViewById(R.id.address));
            final TextView tvname = ((TextView) vg.findViewById(R.id.name));
            final TextView tvpaired = (TextView) vg.findViewById(R.id.paired);
            final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);

            tvrssi.setVisibility(View.VISIBLE);

            byte rssival = (byte) devRssiValues.get(device.getAddress()).intValue();

            if (rssival != 0) {
                tvrssi.setText("Rssi = " + String.valueOf(rssival));
            }

            tvname.setText(device.getName());
            if (ApplicationEx.getInstance().retrieveDeviceId(device.getAddress()) != null && ApplicationEx.getInstance().retrieveDeviceId(device.getAddress()).length() > 0) {
                tvadd.setText(ApplicationEx.getInstance().retrieveDeviceId(device.getAddress()));

            } else {

//                tvadd.setText(device.getAddress());
                tvadd.setText("Retrieving Device ID...");
            }

            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                tvname.setTextColor(Color.GRAY);
                tvadd.setTextColor(Color.GRAY);
                tvpaired.setTextColor(Color.GRAY);
                tvpaired.setVisibility(View.VISIBLE);
                tvpaired.setText(R.string.paired);
                tvrssi.setVisibility(View.GONE);
                tvrssi.setTextColor(Color.BLACK);
            } else {
                tvname.setTextColor(Color.BLACK);
                tvadd.setTextColor(Color.BLACK);
                tvpaired.setVisibility(View.GONE);
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.BLACK);
            }

            if (position == selectedDevice) {
                vg.setBackgroundColor(Color.GRAY);
            } else {
                vg.setBackgroundColor(Color.WHITE);

            }
            return vg;
        }//end getview

        @Override
        public void onItemClick(AdapterView<?> viewgroup, View view, int position, long id) {
            // TODO Auto-generated method stub
            selectedDevice = position;
            notifyDataSetChanged();
        }


        private void showMessage(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }//end Adapter

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBtAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            System.out.println("start Scanning..");
            mBtAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addDevice(device, rssi);
                            //mLeDeviceListAdapter.addDevice(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    //***********************************************************************
    // 							Function
    //***********************************************************************
    private BluetoothDevice mDevice = null;
    private BleServiceProxy mProxy;

    BluetoothAdapter mBluetoothAdapter = null;

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

    // Code to manage Service life cycle.
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

    private Hashtable<String, String> outData;


    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            BluetoothDevice localBDevice = selectedBLEDevice;
            String action = intent.getAction();
            if (!action.equals(CustomService.CUSTOM_BROADCAST)) {
                return;
            }

            int msg = intent.getIntExtra(CustomService.CUSTOM_ATTR_MSG, 0);

            System.out.println("Pairing BroadcastReceiver: " + msg);
            if (msg == CustomService.CUSTOM_CONNECT_MSG || msg == CustomService.CUSTOM_READY_MSG) {
                System.out.println(TAG + "CustomService.CUSTOM_CONNECT_MSG || CustomService.CUSTOM_READY_MSG");

                if (localBDevice.getAddress() != null) {
                    ApplicationEx.getInstance().statsData.deviceID = localBDevice.getAddress();
                }

                Log.d(TAG, "timestarted: " + AppUtil.timeToString(DeviceData.timeLog));

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mService.enableCustomNotification(mDevice);
                    }
                }, 1000L);

                mProxy.sendCommand31();

                setBluetooth();
            } else if (msg == CustomService.CUSTOM_VALUE_MSG) { // Notification data
                int command = intent.getIntExtra(CustomService.CUSTOM_ATTR_COMMAND, 0);

                System.out.println("CUSTOM_ATTR_COMMAND: " + command);
                if (command == ForkProtocol.RX_COMMAND_2) {
                    System.out.println("ForkProtocol.Command2: " + selectedBLEDevice.getAddress() + "ID :" + ApplicationEx.getInstance().statsData.deviceIDinHex);
                    ApplicationEx.getInstance().saveDeviceId(selectedBLEDevice.getAddress(), ApplicationEx.getInstance().statsData.deviceIDinHex);
                    mLeDeviceListAdapter.notifyDataSetChanged();

                } else if (command == ForkProtocol.RX_COMMAND_31) {
                    mProxy.sendCommand2();
                }

            }
        }
    };

    public void setBluetooth() {
        Log.d("setBT@ Pairing activity", "1");

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
                }
            }
        }, 2000L);

    }

    /*ADD FOR UI*/
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnrel_1) { //either stop scan or scan again or link this device
            if (status == STATUS_SCANNING)
                finishScanning();
            else if (status == STATUS_LINKING) {
                if (deviceList.size() == 0) { //scan again
                    scanLeDevice(true);
                    status = STATUS_SCANNING;
                    updateUI();
                } else {
                    ApplicationEx.getInstance().bluetoothDevice = deviceList.get(selectedDevice);

                    //start loader
                    ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.VISIBLE);

                    if (ApplicationEx.getInstance().statsData.deviceIDinHex != null) {
                        getDeviceIDStatus();
                    } else {
                        System.out.println("&&DeviceID: " + ApplicationEx.getInstance().retrieveDeviceId(selectedBLEDevice.getAddress()));
                        if (ApplicationEx.getInstance().retrieveDeviceId(selectedBLEDevice.getAddress()) != null) {


                            ApplicationEx.getInstance().statsData.deviceIDinHex = ApplicationEx.getInstance().retrieveDeviceId(selectedBLEDevice.getAddress());

                            System.out.println("&& DeviceID: " + ApplicationEx.getInstance().statsData.deviceIDinHex);

                            if (ApplicationEx.getInstance().statsData.deviceIDinHex == null) {
                                Toast.makeText(getApplicationContext(), "Device ID not found. Please try again.  ", Toast.LENGTH_LONG).show();

                                return;
                            } else {
                                getDeviceIDStatus();
                            }
                        }
                    }
                }
            } else if (status == STATUS_LINKEDSUCCESS) {
                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);
                DeviceState.instance().mDevice = ApplicationEx.getInstance().bluetoothDevice;

                saveForkData(ApplicationEx.getInstance().bluetoothDevice);

                gotoMainPage();
            }
        } else if (v.getId() == R.id.btnrel_2) { //cancel setup
            gotoMainPageCancelSetup();
        } else
            super.onClick(v);

    }


    private void getDeviceIDStatus() {
        Connection connection = new Connection();
        connection.get_device_id_status(ApplicationEx.getInstance().statsData.deviceIDinHex, getDeviceStatusHandler, "");
    }

    private void getDeviceProfile() {
        Connection connection = new Connection();
        connection.get_device_id_profile(ApplicationEx.getInstance().statsData.deviceIDinHex, getDeviceProfileHandler, "");
    }

    private void sendAccountLink() {
        com.protocol.xml.XMLHelper helper = new com.protocol.xml.XMLHelper();
        String data = helper.createSendAccountLinkXML(ApplicationEx.getInstance().retrieveUserName(), ApplicationEx.getInstance().retrieveUserPassword());

        Connection connection = new Connection();
        connection.send_account_link(ApplicationEx.getInstance().statsData.deviceIDinHex, sendAccountLinkHandler, data);
    }

    private void sendAccountUnLink() {
        com.protocol.xml.XMLHelper helper = new com.protocol.xml.XMLHelper();
        String data = helper.createSendAccountUnLinkXML(ApplicationEx.getInstance().retrieveUserName(), ApplicationEx.getInstance().retrieveUserPassword());

        Connection connection = new Connection();
        connection.send_account_unlink(ApplicationEx.getInstance().statsData.deviceIDinHex, sendAccountUnLinkHandler, data);
    }

    final Handler getDeviceStatusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            System.out.println("getDeviceStatusHandler status: " + msg.what);


            switch (msg.what) {
                case Connection.REQUEST_START:
                    //show progress bar here
                    System.out.println("START HERE");
                    break;
                case Connection.REQUEST_SUCCESS:
                    //  TODO:
                    //

                    System.out.println("SUCCESS HERE@ getDeviceStatusHandler " + " " + ApplicationEx.getInstance().deviceStatus);

                    if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("Successful")) {
                        //stop loader
                        ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);

                        //check if current account is using the same hapifork
                        if (ApplicationEx.getInstance().userprofile.getDevice_code() != null) {
                            if (ApplicationEx.getInstance().userprofile.getDevice_code().equalsIgnoreCase(ApplicationEx.getInstance().statsData.deviceIDinHex)) {
                                sendAccountLink();
                            } else {
                                if (!(PairingActivity.this.isFinishing())) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                                    alertDialog.setTitle("Alert");
                                    alertDialog.setMessage("Ooops!, this device is paired to another account");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            }
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage(getResources().getString(R.string.hapifork_now_active));
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("DeviceIdNotLinked")) {
                        //allow linking account to HAPIfork
                        sendAccountLink();
                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("DeviceIdError")) {
//                        try {
//                            mProxy.sendCommand31();
//
//                            getDeviceIDStatus();
//
//                        }catch (NullPointerException e){
//                            e.printStackTrace();
//                        }

                        if (!(PairingActivity.this.isFinishing())) {
                            AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Ooops!, device ID not found. Please try again.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }

                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("Failed")) {
                        //stop loader
                        ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);

                        AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage(getString(R.string.something_went_wrong));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        //stop loader
                        ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);

                    }
                    if (msg.obj != null && msg.obj instanceof String) {
                        String message = msg.obj.toString();
                        if (message.contains("Successful".toLowerCase())) {
                            System.out.println("SUCCESS HERE@ sendConnectionHandler SEND DATA");
                        } else
                            Toast.makeText(getApplicationContext(), "Error syncing your fork:  " + msg.obj.toString(), Toast.LENGTH_SHORT).show();

                    }

                    break;
                case Connection.REQUEST_ERROR:
                    // TODO:
                    if (msg.obj != null && msg.obj instanceof String) {
                        if (!(PairingActivity.this.isFinishing())) {
                            AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Mohon cek koneksi internet anda  " + msg.obj.toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        break;
                    }

            }//end switch
        }

    };

    final Handler getDeviceProfileHandler = new Handler() {
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
                    //
                    System.out.println("SUCCESS HERE@ getDeviceProfileHandler " + " " + msg.obj);
//                    updateUI();


                    if (msg.obj != null && msg.obj instanceof String) {
                        String message = msg.obj.toString();
                        if (message.contains("Successful".toLowerCase())) {
                            System.out.println("SUCCESS HERE@ sendConnectionHandler SEND DATA");

                            status = STATUS_LINKEDSUCCESS;

                        } else
                            Toast.makeText(getApplicationContext(), "Error syncing your fork:  " + msg.obj.toString(), Toast.LENGTH_SHORT).show();

                    }

                    break;
                case Connection.REQUEST_ERROR:
                    // TODO:
                    if (msg.obj != null && msg.obj instanceof String) {
                        if (!(PairingActivity.this.isFinishing())) {
                            AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Please check your network connection  " + msg.obj.toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }

                    break;

            }//end switch
        }

    };

    final Handler sendAccountLinkHandler = new Handler() {
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
                    //
                    //stop loader
                    ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);

                    System.out.println("SUCCESS HERE@ sendAccountLinkHandler " + " " + ApplicationEx.getInstance().deviceStatus);

                    if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("Successful")) {
                        ApplicationEx.getInstance().hasHAPIforkPairedToTheApp = true;

                        status = STATUS_LINKEDSUCCESS;

                        updateUI();
                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("AccountUnknown")) {
                        //logout
                        AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Account Unknown");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("FalsePassword")) {
                        //logout
                        AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("False Password");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("AccountAlreadyLinked")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage(getResources().getString(R.string.device_already_paired));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }

                    break;
                case Connection.REQUEST_ERROR:
                    // TODO:
                    if (msg.obj != null && msg.obj instanceof String) {
                        if (!(PairingActivity.this.isFinishing())) {
                            AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Mohon cek koneksi internet anda  " + msg.obj.toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }

                    break;

            }//end switch
        }

    };

    final Handler sendAccountUnLinkHandler = new Handler() {
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
                    //
                    System.out.println("SUCCESS HERE@ sendAccountUnLinkHandler " + " " + ApplicationEx.getInstance().deviceStatus);

                    if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("Successful")) {

                        sendAccountLink();
                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("AccountUnknown")) {
                        //logout

                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("FalsePassword")) {
                        //logout
                    } else if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("AccountAlreadyLinked")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage(getResources().getString(R.string.device_already_paired));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }

                    break;
                case Connection.REQUEST_ERROR:
                    // TODO:
                    if (msg.obj != null && msg.obj instanceof String) {
                        if (!(PairingActivity.this.isFinishing())) {
                            AlertDialog alertDialog = new AlertDialog.Builder(PairingActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Please check your network connection  " + msg.obj.toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }

                    break;

            }//end switch
        }

    };

    private void saveForkData(BluetoothDevice device) {

        HAPIforkDAO forkDAO = new HAPIforkDAO(this, null);
        forkDAO.createTable();

        boolean hasHAPIfork = false;
        try {
            hasHAPIfork = !forkDAO.getForkAddress().equals("");
        } catch (NullPointerException e) {
            hasHAPIfork = false;
        }

        if (hasHAPIfork) {
            forkDAO.clearTable();
        }

        forkDAO.insertRecord(device.getName(), device.getAddress());


    }

    private void gotoMainPage() {
        Intent mainIntent = new Intent(this, ViewActivity.class);
        mainIntent.putExtra("STATUS", "SUCCESSFULLY_PAIRED");
        ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);
        startActivity(mainIntent);
        finish();
    }

    private void gotoMainPageCancelSetup() {
        Intent mainIntent = new Intent(this, ViewActivity.class);
        mainIntent.putExtra("STATUS", "CANCEL_SETUP");
        startActivity(mainIntent);
        finish();
    }

    private void updateUI() {

        if (status == STATUS_LINKEDSUCCESS) {
            ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);
            TextView welcomemessage = (TextView) findViewById(R.id.hello);
            welcomemessage.setText(getResources().getString(R.string.hapifork_now_active));

            TextView welcomemessagesecondary = (TextView) findViewById(R.id.empty);
            welcomemessagesecondary.setText(getResources().getString(R.string.succesfully_paired_hapifork));
            welcomemessagesecondary.setVisibility(View.VISIBLE);

            TextView cancelButton = (TextView) findViewById(R.id.btntext_1);
            cancelButton.setText(getResources().getString(R.string.use_fork_now));
            ((RelativeLayout) findViewById(R.id.btnrel_1)).setOnClickListener(this);

            ((RelativeLayout) findViewById(R.id.btnrel_2)).setVisibility(View.INVISIBLE);

            newDevicesListView.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.warning_icon)).setImageResource(R.drawable.hapiforkactive);
            ((ImageView) findViewById(R.id.warning_icon)).setVisibility(View.VISIBLE);

            return;

        }

        ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
        newDevicesListView.setVisibility(View.VISIBLE);

        TextView welcomemessage = (TextView) findViewById(R.id.hello);
        welcomemessage.setText(getResources().getString(R.string.scanning_devices_pairing));
        ((ImageView) findViewById(R.id.warning_icon)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.empty)).setVisibility(View.GONE);

        TextView cancelButton = (TextView) findViewById(R.id.btntext_1);
        cancelButton.setText(getResources().getString(R.string.stop_scanning));
        ((RelativeLayout) findViewById(R.id.btnrel_1)).setOnClickListener(this);
    }

    private void finishScanning() {
        status = STATUS_LINKING;
        if (deviceList.size() == 0) {

            ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);

            TextView welcomemessage = (TextView) findViewById(R.id.hello);
            welcomemessage.setText(getResources().getString(R.string.no_hapifork_detected));

            TextView welcomemessagesecondary = (TextView) findViewById(R.id.empty);
            welcomemessagesecondary.setText(getResources().getText(R.string.please_make_sure_hapifork_on));
            welcomemessagesecondary.setVisibility(View.VISIBLE);

            TextView cancelButton = (TextView) findViewById(R.id.btntext_1);
            cancelButton.setText(getResources().getText(R.string.scan_again));
            ((RelativeLayout) findViewById(R.id.btnrel_1)).setOnClickListener(this);
            newDevicesListView.setVisibility(View.GONE);

            ((ImageView) findViewById(R.id.warning_icon)).setVisibility(View.VISIBLE);
        } else {

            ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);
            TextView cancelButton = (TextView) findViewById(R.id.btntext_1);
            cancelButton.setText(getResources().getText(R.string.link_this_device));

            ((RelativeLayout) findViewById(R.id.btnrel_1)).setOnClickListener(this);

            TextView welcomemessage = (TextView) findViewById(R.id.hello);
            welcomemessage.setText(getResources().getText(R.string.select_fork_from_list));

            ((ImageView) findViewById(R.id.warning_icon)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.empty)).setVisibility(View.GONE);

        }
    }
}