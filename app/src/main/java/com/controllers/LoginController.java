package com.controllers;

import com.blespp.client.DeviceData;
import com.controllers.ApplicationEx;
import com.hapilabs.lightweight.R;
import com.obj.PhotoDownloadObj;
import com.obj.UserProfile;
import com.protocol.Connection;
import com.protocol.xml.XMLHelper;
import com.ui.custom.PairingActivity;
import com.ui.custom.TourPageActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class LoginController {
    Handler handler;
    Context context;
    public DeviceData mData;

    public LoginController(Context context) {
        this.context = context;
        handler = new Handler();

    }

    public void login(String username, String password) {
        XMLHelper helper = new XMLHelper();
        String data = helper.createLoginXML(username, password);
        Connection connection = new Connection();
        connection.loginServices(username, password, data, loginHandler);

        ((TourPageActivity) (context)).saveUserData(username, password);
    }

    final Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case Connection.REQUEST_START:
                    //show progress bar here

                    break;
                case Connection.REQUEST_SUCCESS:
                    //dismiss progress bar here

                    ((TourPageActivity) (context)).dismissProgress();

                    if (msg.obj instanceof UserProfile) {
                        ApplicationEx.getInstance().userprofile = (UserProfile) msg.obj;
                        downloadProfilePic(ApplicationEx.getInstance().userprofile.getUrl_picMed());

                        System.out.println("REQUEST_SUCCESS: " + ((UserProfile) msg.obj).getFirstName());
                        System.out.println("REQUEST_SUCCESS: " + ((UserProfile) msg.obj).isHasHapifork());
                        System.out.println("REQUEST_SUCCESS: " + ((UserProfile) msg.obj).getDevice_code());

                        ((TourPageActivity) (context)).updatePage(TourPageActivity.STATUS_WELCOME,
                                ApplicationEx.getInstance().userprofile.getFirstName());
                    } else {

//                        Toast.makeText(context, "Login Failed: " + ((String) msg.obj), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, context.getString(R.string.login_failed), Toast.LENGTH_SHORT).show();

                    }
                    break;
                case Connection.REQUEST_ERROR:
                    System.out.println("REQUEST_ERROR: ");
                    if(!((TourPageActivity)context).isFinishing()) {
                        AlertDialog alertDialog = new AlertDialog.Builder((TourPageActivity)context).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage(msg.obj.toString());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }

                    //dismiss progress here
                    ((TourPageActivity) (context)).dismissProgress();
//                    if (msg.obj != null && msg.obj instanceof String)
//                        Toast.makeText(context, "Login Failed: " + ((String) msg.obj), Toast.LENGTH_SHORT).show();

                    break;

            }//end switch
        }

    };

    public void downloadProfilePic(String url) {

        Connection connection = new Connection(bitmapHandler);
        connection.getBitmap(url, url, url);

    }

    final Handler bitmapHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Connection.REQUEST_START:
                    //show progress bar here
                    break;
                case Connection.REQUEST_SUCCESS:
                    //dismiss progress bar here

                    if (msg.obj instanceof PhotoDownloadObj) {
                        PhotoDownloadObj obj = (PhotoDownloadObj) msg.obj;
                        obj = null;
                    }
                    break;
                case Connection.REQUEST_ERROR:
                    //dismiss progress here
                    break;

            }//end switch
        }
    };

}
