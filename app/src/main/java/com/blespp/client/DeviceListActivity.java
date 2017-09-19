/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blespp.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.hapilabs.lightweight.R;
import com.storage.HAPIforkDAO;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



public class DeviceListActivity extends Activity {
   
	private BluetoothAdapter mBtAdapter;
    private TextView mEmptyList;
    public static final String hapifork = "hapifork";
    public static final String TAG = "DeviceListActivity";
    private CustomService mService = null;
    List<BluetoothDevice> deviceList;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    
    public int somevalue = 10;
    private ServiceConnection onService = null;
    Map<String, Integer> devRssiValues;
    
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean mScanning;
    private Handler mHandler;
    
    HAPIforkDAO forkDAO;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        setContentView(R.layout.device_list);
        
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }
        
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBtAdapter == null) {
            finish();
            return;
        }
        
        
        forkDAO = new HAPIforkDAO(this, null);
        
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
        Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
         
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBtAdapter.isEnabled()) {
            if (!mBtAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        populateList();
    }

    
    private void populateList() {
        /* Initialize device list container */
        Log.d(TAG, "populateList");
        deviceList = new ArrayList<BluetoothDevice>();
        mLeDeviceListAdapter = new LeDeviceListAdapter(this, deviceList);
        devRssiValues = new HashMap<String, Integer>();

        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mLeDeviceListAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        
        final TextView tvscanning = ((TextView)findViewById(R.id.empty));
        if(forkDAO.getForkAddress().equals("")){
        	tvscanning.setText(R.string.scanning);
        }else{
        	tvscanning.setText(R.string.searching_linked);
        }

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            addDevice(pairedDevice, 0);
        }
        scanLeDevice(true);
    }
    
    private void addDevice(BluetoothDevice device, int rssi) {
    	
    	System.out.println("1 saved address: " + forkDAO.getForkAddress());
    	
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
            String myDeviceName = device.getName();

            if (myDeviceName==null) {myDeviceName="";}

        	System.out.println(device.getName());
        	if ( (myDeviceName.toLowerCase(Locale.ENGLISH)).contains("hapifork")){
        		        		
        		if(device.getAddress().contains(forkDAO.getForkAddress())){
        			
                    if (device == null) return;

                    if (mScanning) {
                        mBtAdapter.stopLeScan(mLeScanCallback);
                        mScanning = false;
                    }
                    
                    Bundle b = new Bundle();
                    b.putString(BluetoothDevice.EXTRA_DEVICE, device.getAddress());

                    Intent result = new Intent();
                    result.putExtras(b);

                    setResult(Activity.RESULT_OK, result);
                    finish();
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
    protected void onDestroy() {
        super.onDestroy();
        unbindService(onService);
        scanLeDevice(false);
    }

   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLeDeviceListAdapter.clear();
    }
    
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
       
    	
    	@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        
    		final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
            if (device == null) return;

            if (mScanning) {
                mBtAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            
            Bundle b = new Bundle();
            b.putString(BluetoothDevice.EXTRA_DEVICE, device.getAddress());

            Intent result = new Intent();
            result.putExtras(b);

            setResult(Activity.RESULT_OK, result);
            finish();
   
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
                finish();
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                if (!mBtAdapter.isEnabled())
                    finish();
            }
        }
    };

    
 // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
    	
    	Context context;
        LayoutInflater inflater;
        
        public LeDeviceListAdapter(Context context, List<BluetoothDevice> deviceList) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
            deviceList = new ArrayList<BluetoothDevice>();
        }

        public void addDevice(BluetoothDevice device) {
            
            HAPIforkDAO forkDAO = new HAPIforkDAO(context, null);
        	
            boolean deviceFound = false;
        	 
            if (!deviceFound) {
            	//check if the device is a hapifork
            	//System.out.println(device.getName());

                String myDeviceName = device.getName();

                if (myDeviceName == null) {myDeviceName="";}


            	if ( (myDeviceName.toLowerCase(Locale.ENGLISH)).contains("hapifork")){
            		            		
            		if(device.getAddress().equals(forkDAO.getForkAddress())){
            			
            			if(!deviceList.contains(device)) {
                        	deviceList.add(device);
                        }
                        
                        if (device == null) return;

                        if (mScanning) {
                            mBtAdapter.stopLeScan(mLeScanCallback);
                            mScanning = false;
                        }
                        
                        Bundle b = new Bundle();
                        b.putString(BluetoothDevice.EXTRA_DEVICE, device.getAddress());

                        Intent result = new Intent();
                        result.putExtras(b);

                        setResult(Activity.RESULT_OK, result);
                        finish();
            		}
            	}
            	
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
        public Object getItem(int i) {
            return deviceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
        	ViewGroup vg;

            if (view != null) {
                vg = (ViewGroup) view;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.device_element, null);
            }
            
            BluetoothDevice device = deviceList.get(position);
            
            final TextView tvadd = ((TextView) vg.findViewById(R.id.address));
            final TextView tvname = ((TextView) vg.findViewById(R.id.name));
            final TextView tvpaired = (TextView) vg.findViewById(R.id.paired);
            final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);
            
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
            	tvname.setText(deviceName);
            else
            	tvname.setText("UNKNOWN DEVICE");
            tvadd.setText(device.getAddress());
            
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                tvname.setTextColor(Color.GRAY);
                tvadd.setTextColor(Color.GRAY);
                tvpaired.setTextColor(Color.GRAY);
                tvpaired.setVisibility(View.VISIBLE);
                tvpaired.setText(R.string.paired);
                tvrssi.setVisibility(View.GONE);
            } else {
                tvname.setTextColor(Color.WHITE);
                tvadd.setTextColor(Color.WHITE);
                tvpaired.setVisibility(View.GONE);
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.WHITE);
            }
            
            return vg;
        }
        
       
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBtAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    if(deviceList.isEmpty()){
                    	final TextView tvscanning = ((TextView)findViewById(R.id.empty));
                        tvscanning.setText(R.string.fork_not_found);
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBtAdapter.startLeScan(mLeScanCallback);
            
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);
            
        }
        invalidateOptionsMenu();
    }
    
    
 // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

}
