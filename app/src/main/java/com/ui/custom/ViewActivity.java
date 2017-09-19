package com.ui.custom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.blespp.client.BleServiceProxy;
import com.blespp.client.CustomService;
import com.blespp.client.DeviceData;
import com.blespp.client.DeviceListActivity;
import com.blespp.client.DeviceState;
import com.blespp.client.ForkProtocol;
import com.controllers.ApplicationEx;
import com.controllers.StatsData.MODE;
import com.hapilabs.lightweight.R;
import com.protocol.Connection;
import com.protocol.WebServices;
import com.protocol.xml.XMLHelper;
import com.storage.HAPIforkDAO;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewActivity extends CommonActivity implements OnClickListener {

    private static final String TAG = "ViewActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_SELECT_DEVICE = 2;
    private static final int REQUEST_LOADWEBKIT = 3;

    private static final int EVENT_SUCCESS = 0x0C;

    private static int recvCount = 0;

    private boolean isDeviceListShowing = false; //flag to make sure that only one dialog is display at one time
    private boolean hasStarted = false;

    private int fork_serving = 0;
    private int success_i = 0;
    private int last_interval_i = 0;

    private DeviceData mData;
    private Context mContext;
    private String deviceAddress;
    private ImageView quick_action;

    private com.ui.custom.DemoPopupWindow dwLeft;

    //*UI VIEW*/
    private ImageView bite;
    private LinearLayout duration;
    private LinearLayout lastInterval;
    private ImageView start_done;
    private ImageView reset_btn;
    private Button reset;
    private TextView timer_duration;
    private TextView bite_counter;
    private TextView headerMessage;
    private TextView headerMessageStatus;
    private LinearLayout livemode;
    private ImageView statsIcon;
    private String MEAL_START = "0:00:00";
    private String DURATION_START = "0:00:00";
    private String TARGET_INTERVAL = "10 detik";
    private int targetInterval_int = 10;
    private String last_interval = "0 detik";
    private LinearLayout headerSecondary;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        TARGET_INTERVAL = ApplicationEx.getInstance().userprofile.getHapifork_interval() + getResources().getString(R.string.seconds_unit);

        targetInterval_int = ApplicationEx.getInstance().userprofile.getHapifork_interval();
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        initUI();

        isBluetoothEnabled();

        if (getIntent().getStringExtra("STATUS") != null) {
            if (getIntent().getStringExtra("STATUS").equalsIgnoreCase("CANCEL_SETUP")) {
                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);
            } else {
                initBleReceiver();
            }
        }

        updateStatsValues(false); //update

        resetValues();
        System.out.println("ViewActivity deviceID: " + ApplicationEx.getInstance().statsData.deviceIDinHex);


        if (!ApplicationEx.getInstance().helpDone) {
            ((LinearLayout) findViewById(R.id.overlay_bg)).setBackgroundResource(R.drawable.overlay);
            ((LinearLayout) findViewById(R.id.overlay_bg)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.overlay_bg)).setOnClickListener(this);
            ApplicationEx.getInstance().helpDone = true;

        }
        if (getIntent().getStringExtra("STATUS") != null) {
            if (!getIntent().getStringExtra("STATUS").equalsIgnoreCase("CANCEL_SETUP")) {
                if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
                    HAPIforkDAO forkDAO = new HAPIforkDAO(this, null);
                    deviceAddress = forkDAO.getForkAddress();
                } else {
                    if (ApplicationEx.getInstance().hasHAPIforkPairedToTheApp) {
//                        displayDeviceList();
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void displayDeviceList() {
        if (!isDeviceListShowing) { //to make sure that there is only one instance of the dialog
            //check if the the bluetooth is on first
            if (mBluetoothAdapter.isEnabled()) {
                isDeviceListShowing = true;
                Intent newIntent = new Intent(ViewActivity.this, DeviceListActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
            }
        }
    }


    @Override
    public void onResume() {
        // TODO record here the current time resume the counter based onPause time and ONResumeTime
        isDeviceListShowing = false;

        System.out.println("ViewActivity onResume: " + ApplicationEx.getInstance().statsData.getAppMode());

        if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
            if (getIntent().getStringExtra("STATUS") != null) {

                if (!getIntent().getStringExtra("STATUS").equalsIgnoreCase("CANCEL_SETUP")) {
                    if (hasStarted) { //start button is already pressed make sure it is in working mode
                        mProxy.sendCommand8(1);
                    }

                    System.out.println("ViewActivity onResume: " + ApplicationEx.getInstance().statsData.getAppMode());

                    updateHeaderStatus();
                }
            }

        }
//        updateHeaderStatus();
        super.onResume();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        resetValues();
        if (mProxy != null) {
            mProxy.disconnectBLE();
        }

        try {
            unbindService(mServiceConnection);

            unregisterReceiver(mBroadcastReceiver);
            stopService(new Intent(this, CustomService.class));
        } catch (Exception e) {

        }

        if (ApplicationEx.getInstance().helpDone) {
            ((LinearLayout) findViewById(R.id.overlay_bg)).setVisibility(View.GONE);

        }
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

            System.out.println("ViewActivity BroadcastReceiver: " + msg);

            if (msg == CustomService.CUSTOM_CONNECT_MSG || msg == CustomService.CUSTOM_READY_MSG) {
//                Toast.makeText(mContext, getResources().getString(R.string.device_connected) + localBDevice.getName(), Toast.LENGTH_LONG).show();

                System.out.println("ViewActivity CUSTOM_CONNECT_MSG: " + ApplicationEx.getInstance().statsData.deviceID);

                try {
                    ApplicationEx.getInstance().statsData.deviceID = localBDevice.getAddress();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);

                updateHeaderStatus();

                Log.d(TAG, "timestarted: " + timeToString(DeviceData.timeStd));
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
                            mProxy.sendCommand31();

                            //TIME command
                            mProxy.sendCommand4();

                            //working/real time mode, bluetooth
                            mProxy.sendCommand8(1);
                        }
                    }
                }, 2000L);

            } else if (msg == CustomService.CUSTOM_DISCONNECT_MSG) {
                ApplicationEx.getInstance().statsData.deviceID = "";
//                Toast.makeText(mContext, getResources().getString(R.string.device_disconnected) + localBDevice.getName(), Toast.LENGTH_LONG).show();

                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);

                updateHeaderStatus();

                if (mProxy != null) {
                    mProxy.removeBond();
                    mService.disconnect();
                }

            } else if (msg == CustomService.CUSTOM_VALUE_MSG) { // Notification data
                if (hasStarted && ApplicationEx.getInstance().statsData.getAppMode() != MODE.MANUAL) { //start button is active

                    updateLog();

                    int command = intent.getIntExtra(CustomService.CUSTOM_ATTR_COMMAND, 0);
                    Log.d(TAG, "CUSTOM_VALUE_MSG: " + command);

                    switch (command) {

                        //get logs
                        case ForkProtocol.RX_COMMAND_6:

                            recvCount--;
                            if (recvCount <= 0) {
                            }

                            break;

                        //working mode
                        case ForkProtocol.RX_COMMAND_8:
                            updateStatsValues(false);
                            break;

                        //realtime, every fork serving from HAPIfork
                        case ForkProtocol.RX_COMMAND_34:

                            Log.d(TAG, "FORK SERVING RECEIVED .. ");

                            DeviceData data = DeviceData.instance();

                            //increment fork serving
                            fork_serving++;

                            ApplicationEx.getInstance().statsData.setI_forkServing(fork_serving);

                            //set last interval in sec
                            ApplicationEx.getInstance().statsData.setI_lastInterval(last_interval_i);

                            last_interval = ApplicationEx.getInstance().statsData.getI_lastInterval() + " sec";

                            stopCountdownTimer();

                            startCountdownTimer();

                            //for every success 0x0C
                            if (data.real_event_type == EVENT_SUCCESS) {
                                success_i++;
                                ApplicationEx.getInstance().statsData.setI_success(success_i);
                                ApplicationEx.getInstance().statsData.isSuccess = true;
                            } else {
                                ApplicationEx.getInstance().statsData.isSuccess = false;
                            }

                            updateStatsValues(true);

                            break;
                        default:
                            break;
                    }//end switch
                }//end if status
                else {
                    int command = intent.getIntExtra(CustomService.CUSTOM_ATTR_COMMAND, 0);

                    System.out.println("CUSTOM_ATTR_COMMAND: " + command);
                    if (command == ForkProtocol.RX_COMMAND_2) {
                        System.out.println("ForkProtocol.Command2: " + ApplicationEx.getInstance().statsData.deviceIDinHex);
                    }

                }

            }
        }
    };


    //from ForkProtocol.java
    private String timeToString(long tm) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        return sDateFormat.format(new Date(tm));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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

                    String deviceAddress_select = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress_select);

                    deviceAddress = mDevice.getAddress();
                    updateStatsValues(false);

                    DeviceState.instance().mDevice = mDevice;

                    try {
                        mProxy.connectBLE();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mData = DeviceData.instance();

                    initBleReceiver();

                    Log.d(TAG, "... after connection" + DeviceState.instance().mDevice.getName());
                }
                break;

            case REQUEST_ENABLE_BT:

                Log.d(TAG, "REQUEST_ENABLE_BT");

                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    bite.setVisibility(View.INVISIBLE);
//				Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, getResources().getString(R.string.bluetooth_turned_on), Toast.LENGTH_SHORT).show();

                } else {
                    bite.setVisibility(View.VISIBLE);
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

    //***********************************************************************
    // 							Function
    //***********************************************************************
    private BluetoothDevice mDevice = null;
    private CustomService mService;
    private BleServiceProxy mProxy;
    private Handler mHandler = new Handler();

    private BluetoothAdapter mBluetoothAdapter = null;

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

    private void isBluetoothEnabled() {

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
//			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, getResources().getString(R.string.bluetooth_not_available), Toast.LENGTH_SHORT).show();

            finish();
        } else if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        }
    }


    private void updateManualServing() {
        fork_serving++;

        ApplicationEx.getInstance().statsData.setI_forkServing(fork_serving);
        //set last interval in sec
        ApplicationEx.getInstance().statsData.setI_lastInterval(last_interval_i);

        last_interval = ApplicationEx.getInstance().statsData.getI_lastInterval() + " sec";

        stopCountdownTimer();

        startCountdownTimer();

        //for every success
        if (ApplicationEx.getInstance().statsData.getI_lastInterval() > targetInterval_int) {
            success_i++;
            ApplicationEx.getInstance().statsData.setI_success(success_i);
            ApplicationEx.getInstance().statsData.isSuccess = true;
        } else {
            ApplicationEx.getInstance().statsData.isSuccess = false;
        }

        updateStatsValues(true);
    }

    private void stopBluetoothWorkingModeServing() {
        mProxy.sendCommand8(1); //do we need this ?

        updateHeaderStatus();

        stopCountdownTimer();

        if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT)
            start_done.setImageResource(R.drawable.ble_live_done);
        else
            start_done.setImageResource(R.drawable.ble_live_start_v2);

        //change button  to start
        hasStarted = false;
    }

    private void startBluetoothWorkingModeServing() {
        resetValues();

        mProxy.sendCommand8(1);

        Time now = new Time();
        now.setToNow();
        MEAL_START = now.format("%H:%M:%S");

        //set stats data
        ApplicationEx.getInstance().statsData.setStr_mealStart(MEAL_START);
        ApplicationEx.getInstance().statsData.setI_forkServing(0);
        ApplicationEx.getInstance().statsData.setI_success(0);
        ApplicationEx.getInstance().statsData.setI_lastInterval(0);
        ApplicationEx.getInstance().statsData.setI_nextCounter(0);
        ApplicationEx.getInstance().statsData.setI_targetInterval(targetInterval_int);

        updateStatsValues(false);

        //start timer

        hasStarted = true;
        stopCountdownTimer();
        startCountdownTimer();

        //change button  to done
        start_done.setImageResource(R.drawable.ble_live_done);
        //make sure it is in working mode
    }

    private void showResetDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.app_name));

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.reset_confirmation));

        alertDialog.setIcon(R.drawable.ic_launcher);

        // Setting Positive Btn
        alertDialog.setPositiveButton(getResources().getString(R.string.positive_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        resetValues();
                    }
                });
        // Setting Negative Btn
        alertDialog.setNegativeButton(getResources().getString(R.string.negative_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog.show();
    }


    @Override
    public void onClick(View v) {
        System.out.println("ONCLICK");
        // TODO Auto-generated method stub
        if (v.getId() == R.id.overlay_bg) {
            ((LinearLayout) findViewById(R.id.overlay_bg)).setVisibility(View.GONE);
            ApplicationEx.getInstance().helpDone = true;

        } else if (v == bite) {
            Log.d("ViewActivity", "bite button");
            if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.MANUAL) {
                if (hasStarted)
                    updateManualServing();
                else
                    Toast.makeText(this, getResources().getString(R.string.click_start_button_first), Toast.LENGTH_LONG).show();
//				Toast.makeText(this, "Please click the Start button first to start the meal" , Toast.LENGTH_LONG).show();
            }
        } else if (v == start_done) {
            Log.d("ViewActivity", "start/done button" + hasStarted + "\nmode: " + ApplicationEx.getInstance().statsData.getAppMode());

            if (!hasStarted) { // meal button is START
                if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
                    startBluetoothWorkingModeServing();
                } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
                    Toast.makeText(this, getResources().getString(R.string.connect_hapifork_first), Toast.LENGTH_LONG).show();
                } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.MANUAL) {
                    hasStarted = true;
                    start_done.setImageResource(R.drawable.ble_live_done);
                    resetValues();
//                    startCountdownTimer();
                }
            } else if (hasStarted) { // meal button is DONE
                if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT) {
                    stopBluetoothWorkingModeServing();
                } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
                    Toast.makeText(this, getResources().getString(R.string.connect_hapifork_first), Toast.LENGTH_LONG).show();
//					Toast.makeText(this, "Please connect to your hapifork first" , Toast.LENGTH_LONG).show();
                } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.MANUAL) {
                    hasStarted = false;
                    start_done.setImageResource(R.drawable.ble_live_start_v2);
                    stopCountdownTimer();
                }
            }

        } else if (v == reset) {
            if (!hasStarted) { //
                return;
            }
            Log.d(TAG, "reset");
            showResetDialog();

        } else if (v == reset_btn) {
            Log.d(TAG, "reset btn");
            showResetDialog();
        } else if (v == quick_action) {
            //showquickaction
            dwLeft = new DemoPopupWindow(v, R.layout.add_quickaction/*pass the view of the quick action*/, this);
            dwLeft.showLikePopDownMenu(true);
        } else if (v.getId() == R.id.quickaction_bluetooth) {

            //working/real time mode, bluetooth
//            if (DeviceState.instance().mDevice == null || !ApplicationEx.getInstance().hasHAPIforkPairedToTheApp) {
//
//                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_DISCONNECT);
//                if (ApplicationEx.getInstance().hasHAPIforkPairedToTheApp)
//                    displayDeviceList();
//                updateHeaderStatus();
//
//            } else {
//
//                ApplicationEx.getInstance().statsData.setAppMode(MODE.BLUETOOTH_CONNECT);
//
//                BluetoothDevice localBDevice = DeviceState.instance().mDevice;
//
//                if (localBDevice != null)
//                    ApplicationEx.getInstance().statsData.deviceID = localBDevice.getAddress();
//
//                if (mDevice != null) {
//                    mHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            mService.enableCustomNotification(mDevice);
//                        }
//                    }, 1000L);
//
//
//                    // Unlock BLE, send time from hapifork
//                    mHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            synchronized (mProxy) {
//                                //unlock BLE
//
//                                try {
//                                    mProxy.sendCommand31();
//                                } catch (Exception e) {
//
//                                }
//
//                                //TIME command
//                                try {
//                                    mProxy.sendCommand4();
//                                } catch (Exception e) {
//
//                                }
//
//                                //working/real time mode, bluetooth
//                                try {
//                                    mProxy.sendCommand8(1);
//                                } catch (Exception e) {
//
//                                }
//
//
//                            }
//                        }
//                    }, 2000L);
//
//                }
//                updateHeaderStatus();
//
//
//            }

            dwLeft.dismiss();
            dwLeft.dismiss();
            onClickQuickAction(0);

        } else if (v.getId() == R.id.quickaction_sync) {
            //manual
            dwLeft.dismiss();
            onClickQuickAction(2);
        } else if (v.getId() == R.id.quickaction_manual) {
            //manual
            dwLeft.dismiss();
            onClickQuickAction(1);
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
        } else if (v.getId() == R.id.quickaction_mealList) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(7);
        } else if (v.getId() == R.id.quickaction_mealDashboard) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(8);
        } else if (v.getId() == R.id.quickaction_logout) {
            //connect to bluetooth
            dwLeft.dismiss();
            onClickQuickAction(9);
        } else if (v == headerSecondary) {
            //working/real time mode, bluetooth
            if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
                if (ApplicationEx.getInstance().hasHAPIforkPairedToTheApp) {
                    displayDeviceList();
                }
                updateHeaderStatus();
            }
        }
    }

    private void resetValues() {
        // TODO Auto-generated method stub
        MEAL_START = "0:00:00";
        DURATION_START = "0:00:00";
//		TARGET_INTERVAL = "10 sec";
//		TARGET_INTERVAL =  ApplicationEx.getInstance().userprofile.getHapifork_interval()+" sec";
        TARGET_INTERVAL = ApplicationEx.getInstance().userprofile.getHapifork_interval() + " " + getResources().getString(R.string.seconds_unit);

        targetInterval_int = ApplicationEx.getInstance().userprofile.getHapifork_interval();
        last_interval = "0 detik";
        fork_serving = 0;
        success_i = 0;
        last_interval_i = 0;

        //set stats data
        ApplicationEx.getInstance().statsData.resetDt_mealDuration();
        ApplicationEx.getInstance().statsData.setStr_mealStart(MEAL_START);

//        updateStatsValues(false);
        resetStatsValues();

        if (hasStarted) {
            stopCountdownTimer();
            startCountdownTimer();
        }

//		bite_counter.setText((targetInterval_int-last_interval_i) + "sec");
        bite_counter.setText((targetInterval_int - last_interval_i) + getResources().getString(R.string.seconds_unit));
        bite_counter.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_RED));
        livemode.setBackgroundColor(Color.parseColor(ApplicationEx.COLOR_BG_RED));


    }

    private CountDownTimer messageTimer = new CountDownTimer(2000, 2) {

        public void onFinish() {
            updateStatsValues(false);
            cancel();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub

        }
    };


    private void startCountdownTimer() {
        System.out.println("startCountdownTimer");
        newCountdownTimer.start();
    }

    private CountDownTimer newCountdownTimer = new CountDownTimer(99900000, 1000) {

        public void onTick(long millisUntilFinished) {
            if (last_interval_i < 999) {
                last_interval_i++;

                if (last_interval_i > targetInterval_int) {
//					bite_counter.setText(last_interval_i + " sec");
                    bite_counter.setText(last_interval_i + getResources().getString(R.string.seconds_unit));
                    //bite_counter.setTextColor(0xff0000); //red
                    bite_counter.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_GREEN));
                    livemode.setBackgroundColor(Color.parseColor(ApplicationEx.COLOR_BG_GREEN));


                } else {
//					bite_counter.setText(((targetInterval_int+1)-last_interval_i) + "sec");
                    bite_counter.setText(((targetInterval_int + 1) - last_interval_i) + getResources().getString(R.string.seconds_unit));
                    bite_counter.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_RED));
                    livemode.setBackgroundColor(Color.parseColor(ApplicationEx.COLOR_BG_RED));
                }
            }
            ApplicationEx.getInstance().statsData.setDt_mealDuration();
            ((TextView) duration.findViewById(R.id.value)).setText(ApplicationEx.getInstance().statsData.getStr_mealDuration());

        }

        public void onFinish() {
            bite_counter.setText(getResources().getString(R.string.max_counter));
        }
    };


    private void stopCountdownTimer() {
        newCountdownTimer.cancel();
        last_interval_i = 0;

    }

    private void onClickQuickAction(int position) {
        System.out.println(" position  " + position);
        switch (position) {
            case 0: //bluetooth
                //updateUI();
                break;

            case 1:  //manual
                ApplicationEx.getInstance().statsData.setAppMode(MODE.MANUAL);
                updateHeaderStatus();
                //reset all

                resetValues();
                break;
            case 2: {//sync
                if (hasStarted) {
                    Toast.makeText(mContext, getResources().getString(R.string.sync_will_stop_meal), Toast.LENGTH_LONG).show();
                }
                Intent mainIntent = new Intent(this, SyncActivity.class);
                startActivity(mainIntent);
                finish();
            }
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
            case 6: { //5 diet rules
                Intent mainIntent = new Intent(this, WebKitActivity.class);
                mainIntent.putExtra("URL", ApplicationEx.URL_DIET_RULES);
                startActivityForResult(mainIntent, REQUEST_LOADWEBKIT);
            }
            break;
            case 9: { //log out
                finish();
//                ((TourPageActivity)(getBaseContext())).removeUserData();

                resetValues();
                if (mProxy != null) {
                    mProxy.disconnectBLE();
                }

                try {
                    unbindService(mServiceConnection);

                    unregisterReceiver(mBroadcastReceiver);
                    stopService(new Intent(this, CustomService.class));
                } catch (Exception e) {

                }

                Intent mainIntent = new Intent(this, TourPageActivity.class);
                mainIntent.putExtra("STATUS", TourPageActivity.STATUS_LOGOUT);
                startActivity(mainIntent);
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
            break;

        }//end switch

    }

    private void updateLog() {
        DeviceData data = DeviceData.instance();
        if (data.blastDataReceived) {
            String tmp = "";
            int iTmp = 1;
            Log.d(TAG, "Data Received (" + data.lastDataReceived + "):");
            for (int z = 0; z < data.lastDataReceived.length; z++) {
                if (((z % 14) == 0) && (tmp != "")) {
                    Log.d(TAG + "inside for loop", "  " + tmp + "--" + iTmp++);
                    tmp = "";
                }
                tmp += " " + String.format("%02x", data.lastDataReceived[z]);
            }
            Log.d("Response: ", tmp + " -- " + iTmp++);
            if ((data.lastDataCHK != data.lastDataCHKCalc) || (data.lastDataRecCmd != data.lastDataSendCmd)) {
                Log.d("mAdapter.insert( CHK: Rec(" + data.lastDataCHK + ")", "Calc (" + data.lastDataCHKCalc + ")" + iTmp++);
                Log.d("  CMD: Rec(" + data.lastDataRecCmd + ")", "- Send (" + data.lastDataSendCmd + ")" + iTmp++);
            }
            //			mAdapter.notifyDataSetChanged();
        }
    }

    //##########  UI DISPLAYS ###############

    private void initUI() {
        quick_action = (ImageView) findViewById(R.id.header_left);
        quick_action.setOnClickListener(this);

        start_done = (ImageView) findViewById(R.id.start_btn);
        start_done.setOnClickListener(this);

        reset_btn = (ImageView) findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(this);

        statsIcon = (ImageView) findViewById(R.id.header_ble);

        headerMessage = (TextView) findViewById(R.id.header_message);
        headerMessageStatus = (TextView) findViewById(R.id.header_message_status);

        headerSecondary = (LinearLayout) findViewById(R.id.headersecondary);
        headerSecondary.setOnClickListener(this);

        ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.GONE);

        bite_counter = (TextView) findViewById(R.id.timecounter);
        bite_counter.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_RED));

        LinearLayout header = (LinearLayout) findViewById(R.id.headermain);
        reset = ((Button) header.findViewById(R.id.header_right));

        //set REset color to gray if ble is disconnected
        if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
            reset.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_GRAY));
        } else
            reset.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_WHITE));

        reset.setOnClickListener(this);

        livemode = (LinearLayout) findViewById(R.id.livemode);
        livemode.setBackgroundColor(Color.parseColor(ApplicationEx.COLOR_BG_RED));

        bite = ((ImageView) livemode.findViewById(R.id.bite_btn));
        bite.setVisibility(View.INVISIBLE);

        bite.setOnClickListener(this);

        hasStarted = false; //start button not yet pressed
    }

    private void updateHeaderStatus() {

        System.out.println("ViewActivity updateHeaderStatus: " + ApplicationEx.getInstance().statsData.getAppMode() + " hasHAPIforkPaired: " + ApplicationEx.getInstance().hasHAPIforkPairedToTheApp);
        if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_CONNECT && ApplicationEx.getInstance().hasHAPIforkPairedToTheApp) {
            statsIcon.setImageResource(R.drawable.blemode_connected);
            headerMessage.setText(getResources().getString(R.string.ble_message));

            headerMessageStatus.setText(getResources().getString(R.string.ble_connect_message) + " HAPIFork " + ApplicationEx.getInstance().statsData.deviceIDinHex);

            headerMessageStatus.setTextColor(Color.parseColor(ApplicationEx.COLOR_BG_GREEN));
            bite.setVisibility(View.INVISIBLE);
            bite_counter.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.GONE);

            reset.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_WHITE));
            //if blue tooth is connected but start is not yet pressed
            start_done.setImageResource(R.drawable.ble_live_start_v2);

            sendConnection();


        } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.BLUETOOTH_DISCONNECT) {
            statsIcon.setImageResource(R.drawable.blemode_disconnected);
            headerMessage.setText(getResources().getString(R.string.ble_message));
            headerMessageStatus.setText(getResources().getString(R.string.ble_disconnect_message));
            if (ApplicationEx.getInstance().hasHAPIforkPairedToTheApp) {
                ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.VISIBLE);
            } else {
                ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.GONE);
            }

            headerMessageStatus.setTextColor(0x000000);
            headerMessageStatus.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_BLACK));

            bite.setVisibility(View.INVISIBLE);
            bite_counter.setVisibility(View.VISIBLE);

            reset.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_GRAY));

            //if blue tooth is connected but start is not yet pressed
            start_done.setImageResource(R.drawable.ble_live_start_v2);

            sendDisconnection();


        } else if (ApplicationEx.getInstance().statsData.getAppMode() == MODE.MANUAL) {
            ((TextView) findViewById(R.id.header_message_help)).setVisibility(View.GONE);

            statsIcon.setImageResource(R.drawable.manualmode_connected);
            headerMessage.setText(getResources().getString(R.string.manual_message));
            headerMessageStatus.setText("");
            bite.setVisibility(View.VISIBLE);
            bite_counter.setVisibility(View.VISIBLE);
            reset.setTextColor(Color.parseColor(ApplicationEx.COLOR_TEXT_WHITE));

            //if blue tooth is connected but start is not yet pressed
            start_done.setImageResource(R.drawable.ble_live_start_v2);

        }

    }

    private void sendConnection() {
        XMLHelper helper = new XMLHelper();
        String data = helper.createSendConnectionsXML("", "");

        Connection connection = new Connection();
        connection.send_connection_new(ApplicationEx.getInstance().statsData.deviceIDinHex, sendConnectionHandler, data);
    }

    private void sendDisconnection() {

        Connection connection = new Connection();
        connection.send_disconnection_new(ApplicationEx.getInstance().statsData.deviceIDinHex, sendConnectionHandler, "");
    }

    private final Handler sendConnectionHandler = new Handler() {
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

                    System.out.println("SUCCESS HERE@ getDeviceStatusHandler " + " " + ApplicationEx.getInstance().deviceStatus);

                    if (ApplicationEx.getInstance().deviceStatus.equalsIgnoreCase("Successful")) {

                    }
//                    getDeviceProfile();

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
                    if (msg.obj != null && msg.obj instanceof String)
                        //                	Toast.makeText(context, "Error syncing your fork:  "+msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
            }//end switch
        }
    };


    private void updateStatsValues(boolean isMessageDisplay) {
        //update values for stats and others

        //stats UI

        LinearLayout mealStart = (LinearLayout) findViewById(R.id.mstart);
        ((ImageView) mealStart.findViewById(R.id.icon)).setImageResource(R.drawable.mealstart_48);
        ((TextView) mealStart.findViewById(R.id.label)).setText(getResources().getString(R.string.dashboard_meal_start));
        ((TextView) mealStart.findViewById(R.id.value)).setText(MEAL_START);

        duration = (LinearLayout) findViewById(R.id.duration);

        ((ImageView) duration.findViewById(R.id.icon)).setImageResource(R.drawable.duration_48);
        ((TextView) duration.findViewById(R.id.label)).setText(getResources().getString(R.string.dashboard_duration));
        System.out.println("duration: " + ApplicationEx.getInstance().statsData.getStr_mealDuration());
        ((TextView) duration.findViewById(R.id.value)).setText(ApplicationEx.getInstance().statsData.getStr_mealDuration());

        timer_duration = (TextView) duration.findViewById(R.id.value);

        LinearLayout targetInterval = (LinearLayout) findViewById(R.id.targetinterval);
        ((ImageView) targetInterval.findViewById(R.id.icon)).setImageResource(R.drawable.icon_stats_interval48);
        ((TextView) targetInterval.findViewById(R.id.label)).setText(getResources().getString(R.string.dashboard_target_interval));
        ((TextView) targetInterval.findViewById(R.id.value)).setText(TARGET_INTERVAL);

        lastInterval = (LinearLayout) findViewById(R.id.lastinterval);
        ((ImageView) lastInterval.findViewById(R.id.icon)).setImageResource(R.drawable.icon_stats_interval48);
        ((TextView) lastInterval.findViewById(R.id.label)).setText(getResources().getString(R.string.dashboard_last_interval));
        ((TextView) lastInterval.findViewById(R.id.value)).setText(last_interval);

        LinearLayout forkServing = (LinearLayout) findViewById(R.id.fs);
        ((ImageView) forkServing.findViewById(R.id.icon)).setImageResource(R.drawable.icon_stat_fs48);
        ((TextView) forkServing.findViewById(R.id.label)).setText(getResources().getString(R.string.dashboard_fork_serving));
        ((TextView) forkServing.findViewById(R.id.value)).setText("" + fork_serving);

        LinearLayout successRate = (LinearLayout) findViewById(R.id.success);
        ((ImageView) successRate.findViewById(R.id.icon)).setImageResource(R.drawable.icon_stats_successrate48);
        ((TextView) successRate.findViewById(R.id.label)).setText(getResources().getString(R.string.dashboard_success));
        ((TextView) successRate.findViewById(R.id.value)).setText("" + success_i);


        LinearLayout messageContainer = (LinearLayout) findViewById(R.id.messagemode); //change green or red

        //check if we need to display the message


        if (isMessageDisplay) {
            ((LinearLayout) findViewById(R.id.livemode)).setVisibility(View.GONE);
            messageContainer.setVisibility(View.VISIBLE);

            if (ApplicationEx.getInstance().statsData.isSuccess) {
                ((TextView) messageContainer.findViewById(R.id.message_success)).setText(getResources().getString(R.string.success_message));
                messageContainer.setBackgroundColor(Color.parseColor(ApplicationEx.COLOR_BG_SUCCESS));
            } else {
                ((TextView) messageContainer.findViewById(R.id.message_success)).setText(getResources().getString(R.string.failed_message));
                messageContainer.setBackgroundColor(Color.parseColor(ApplicationEx.COLOR_BG_FAILED));

            }
            messageTimer.start();

        } else {
            ((LinearLayout) findViewById(R.id.livemode)).setVisibility(View.VISIBLE);
            messageContainer.setVisibility(View.GONE);
        }
    }

    private void resetStatsValues() {
        //update values for stats and others

        //stats UI

        duration = (LinearLayout) findViewById(R.id.duration);
        ((TextView) duration.findViewById(R.id.value)).setText(DURATION_START);

        timer_duration = (TextView) duration.findViewById(R.id.value);
        LinearLayout targetInterval = (LinearLayout) findViewById(R.id.targetinterval);
        ((TextView) targetInterval.findViewById(R.id.value)).setText(TARGET_INTERVAL);

        lastInterval = (LinearLayout) findViewById(R.id.lastinterval);
        ((TextView) lastInterval.findViewById(R.id.value)).setText(last_interval);

        LinearLayout forkServing = (LinearLayout) findViewById(R.id.fs);
        ((TextView) forkServing.findViewById(R.id.value)).setText("" + fork_serving);

        LinearLayout successRate = (LinearLayout) findViewById(R.id.success);
        ((TextView) successRate.findViewById(R.id.value)).setText("" + success_i);

    }

    /**UNUSED METHODS**/
//	private void disconnect(){
//	if (mProxy != null) {
//		mProxy.disconnectBLE();
//	}
//	unbindService(mServiceConnection);
//	unregisterReceiver(mBroadcastReceiver);
//	stopService(new Intent(this, CustomService.class));
//
//}

}
