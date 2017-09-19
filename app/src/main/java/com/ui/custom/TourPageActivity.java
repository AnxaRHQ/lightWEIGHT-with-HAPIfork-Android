package com.ui.custom;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.controllers.ApplicationEx;
import com.controllers.StatsData;
import com.hapilabs.lightweight.R;
import com.storage.HAPIforkDAO;
import com.controllers.LoginController;


public class TourPageActivity extends CommonActivity implements OnPageChangeListener, OnClickListener {

    public static final byte STATUS_SETUP = 1;
    public static final byte STATUS_CREATENEW = 2;
    public static final byte STATUS_LOGIN = 3;
    public static final byte STATUS_TOUR = 4;
    public static final byte STATUS_WELCOME = 5;
    public static final byte STATUS_LOGOUT = 6;
    public static final int MAX_PAGE = 2/*0-1-2*/;
    private byte status = STATUS_LOGIN; //for first time launch

    ViewPager pager;
    MyPagerAdapter adapter;

    ImageView[] pages;
    boolean hasHapitrack = false;
    boolean hasHapitrackLogin = false;

    ImageView user_iv;

    LoginController controller = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("SAVE SYNC DAte timezone: " + TimeZone.getDefault());


        status = getIntent().getByteExtra("STATUS", STATUS_SETUP);
        upDateScreen();
    }

    private void upDateScreen() {
        upDateScreen(null);
    }

    private void upDateScreen(String name) {

        System.out.println("updateScreen: " + name + status);

        switch (status) {
            case STATUS_SETUP:
                if (!retrieveUserName().equals("")) {
                    loadLogin();

                    ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.VISIBLE);

                    connectTologin(retrieveUserName(), retrieveUserPassword());
                } else {
//				loadSetupPage();
                    status = STATUS_LOGIN;
                    loadLogin();
                }
                break;

            case STATUS_LOGIN:
                loadLogin();
                break;

            case STATUS_TOUR:
                loadTourPage();
                break;

            case STATUS_WELCOME:
                if (ApplicationEx.getInstance().userprofile != null) {
                    boolean hasHAPIforkLinked;

                    try {
                        HAPIforkDAO forkDAO = new HAPIforkDAO(this, null);
                        ApplicationEx.getInstance().statsData.deviceIDinHex = ApplicationEx.getInstance().retrieveDeviceId(forkDAO.getForkAddress());
                    } catch (NullPointerException e) {
                        hasHAPIforkLinked = false;
                    }

                    try {
                        if (ApplicationEx.getInstance().userprofile.getDevice_code().equalsIgnoreCase(ApplicationEx.getInstance().statsData.deviceIDinHex)) {
                            hasHAPIforkLinked = true;
                        } else {
                            hasHAPIforkLinked = false;
                        }
                    } catch (NullPointerException e) {
                        hasHAPIforkLinked = false;
                    }

                    ApplicationEx.getInstance().hasHAPIforkPairedToTheApp = hasHAPIforkLinked;
                    loadWelcomePage(name, ApplicationEx.getInstance().userprofile.isHasHapifork(), hasHAPIforkLinked);
                } else
                    loadWelcomePage(name, false, false);
                break;

            case STATUS_CREATENEW:
//                loadCreateNew();
                loadLogin();
                break;

            case STATUS_LOGOUT:
                removeUserData();
                loadLogin();
                break;
        }
    }

    private void removeHeaderIcon(boolean removeAll) {
        if (removeAll) {
            try {
                ((LinearLayout) findViewById(R.id.headermain)).setVisibility(View.GONE);
            } catch (Exception e) {

            }
        } else {
            try {
                ((ImageView) findViewById(R.id.header_left)).setVisibility(View.INVISIBLE);
            } catch (Exception e) {

            }
        }
    }


    private void loadTourPage() {
        setContentView(R.layout.tourpage);
        removeHeaderIcon(true);

        pager = (ViewPager) findViewById(R.id.horiz);
        adapter = new MyPagerAdapter(this, this);

        ArrayList<Bitmap> items = new ArrayList<Bitmap>();

        items.add(BitmapFactory.decodeResource(getResources(), R.drawable.page1));
        items.add(BitmapFactory.decodeResource(getResources(), R.drawable.page2));
        items.add(BitmapFactory.decodeResource(getResources(), R.drawable.page3));
        adapter.setImages(items);

        pager.setSelected(true);
        pager.setOnPageChangeListener(this);
        pager.setAdapter(adapter);

        pages = new ImageView[items.size()];
        pages[0] = (ImageView) findViewById(R.id.horiz_page1);
        pages[1] = (ImageView) findViewById(R.id.horiz_page2);
        pages[2] = (ImageView) findViewById(R.id.horiz_page3);

        items = null;
        System.gc();
    }

    public void dismissProgress() {
        try {
            ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    public void updatePage(byte status, String username) {
        this.status = status;
        upDateScreen(username);
    }

    private void setupButtons(String btn1, String btn2) {
        System.out.println(btn1 + " " + btn2);

        if (btn1 != null) {
            RelativeLayout btn_rel_1 = (RelativeLayout) findViewById(R.id.btnrel_1);
            btn_rel_1.setOnClickListener(this);
            TextView btn_text_1 = (TextView) findViewById(R.id.btntext_1);
            btn_text_1.setText(btn1);
        } else {
            try {
                RelativeLayout btn_rel_1 = (RelativeLayout) findViewById(R.id.btnrel_1);
                btn_rel_1.setVisibility(View.INVISIBLE);
            } catch (Exception e) {

            }
        }

        if (btn2 != null) {
            RelativeLayout btn_rel_2 = (RelativeLayout) findViewById(R.id.btnrel_2);
            btn_rel_2.setOnClickListener(this);

            TextView btn_text_2 = (TextView) findViewById(R.id.btntext_2);
            btn_text_2.setText(btn2);
        } else {
            try {
                RelativeLayout btn_rel_2 = (RelativeLayout) findViewById(R.id.btnrel_2);
                btn_rel_2.setVisibility(View.INVISIBLE);
            } catch (Exception e) {

            }
        }
    }

//    private void loadSetupPage() {
//        setContentView(R.layout.setup_login);
//        removeHeaderIcon(true);
//
//        setupButtons(getResources().getString(R.string.set_up), getResources().getString(R.string.sign_in));
//        ((TextView) findViewById(R.id.welcomemessage)).setOnClickListener(this);
//
//    }

    private void loadWelcomePage(String name, boolean hasHAPIforkLinked, boolean hasHAPIforkBLE) {

        //response from login
        hasHapitrackLogin = hasHAPIforkLinked;
        //fork paired to the app
        hasHapitrack = hasHAPIforkBLE;

        setContentView(R.layout.welcome);
        removeHeaderIcon(false);
        setupButtons(getResources().getString(R.string.replace), getResources().getString(R.string.dont_replace));

        user_iv = (ImageView) findViewById(R.id.user_imgView);

        //download profile pic
        new DownloadImageTask(user_iv)
                .execute(ApplicationEx.getInstance().userprofile.getUrl_picMed());

        //getUsername
        if (((TextView) findViewById(R.id.hello)).getText() != null) {
            String text = ((TextView) findViewById(R.id.hello)).getText().toString();
            text = text.replace("%s", name);
            ((TextView) findViewById(R.id.hello)).setText(text);
        }

        //getUserPhoto
        if (!hasHAPIforkLinked) {
            //replace the content with setup
            ((TextView) findViewById(R.id.welcomemessage)).setText(getResources().getString(R.string.no_hapifork_linked));
            setupButtons(getResources().getString(R.string.view_tour), getResources().getString(R.string.cancel_setup));

        } else {
            if (!hasHAPIforkBLE) {
                String text = getResources().getString(R.string.hapifork_linked_not_bluetooth);
                if (ApplicationEx.getInstance().userprofile != null) {
                    text = text.replace("%s", "" + ApplicationEx.getInstance().userprofile.getHapifork_interval());
                }
                ((TextView) findViewById(R.id.welcomemessage)).setText(text);

                setupButtons(getResources().getString(R.string.connect_hapifork), getResources().getString(R.string.cancel_setup));


            } else {
                String text = ((TextView) findViewById(R.id.welcomemessage)).getText().toString();

                if (ApplicationEx.getInstance().userprofile != null) {
                    text = text.replace("%s", "" + ApplicationEx.getInstance().userprofile.getHapifork_interval());
                }
                ((TextView) findViewById(R.id.welcomemessage)).setText(text);
                setupButtons(getResources().getString(R.string.replace), getResources().getString(R.string.dont_replace));
            }
        }
    }

//    private void loadCreateNew() {
//        setContentView(R.layout.setup_login);
//        removeHeaderIcon(true);
//        setupButtons(getResources().getString(R.string.new_to_hapifork), getResources().getString(R.string.existing_user));
//        ((TextView) findViewById(R.id.welcomemessage)).setOnClickListener(this);
//    }

    private void loadLogin() {
        setContentView(R.layout.login);
        removeHeaderIcon(true);
        dismissProgress();
        setupButtons(getResources().getString(R.string.sign_in), null);
        ((TextView) findViewById(R.id.welcomemessage)).setOnClickListener(this);
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case 0:
                pages[0].setImageResource(R.drawable.selected);
                pages[1].setImageResource(R.drawable.circle_filled);
                pages[2].setImageResource(R.drawable.circle_filled);
                break;
            case 1:
                pages[1].setImageResource(R.drawable.selected);
                pages[0].setImageResource(R.drawable.circle_filled);
                pages[2].setImageResource(R.drawable.circle_filled);
                break;
            case 2:
                pages[2].setImageResource(R.drawable.selected);
                pages[1].setImageResource(R.drawable.circle_filled);
                pages[0].setImageResource(R.drawable.circle_filled);
                break;
        }
    }

    @Override
    public void onDestroy() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
        dismissProgress();
        super.onDestroy();
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub

    }

    private void connectTologin(String username, String password) {
        if (controller == null) {
            controller = new LoginController(this);
        }
        controller.login(username, password);
    }

    private void validateInput() {


        EditText username = (EditText) findViewById(R.id.ed_username);
        EditText password = (EditText) findViewById(R.id.ed_password);

        String myUsername = username.getText().toString();
        String myPassword = password.getText().toString();

        if (myUsername.contains(" ") == true) {
            username.setText(myUsername.toString().replace(" ", ""));
        }

        if (myPassword.contains(" ") == true) {
            password.setText(myPassword.toString().replace(" ", ""));
        }

        if (username.getText() != null && username.getText().toString() != null && username.getText().toString().trim().length() > 0) {
            if (password.getText() != null && password.getText().toString() != null && password.getText().toString().trim().length() > 0) {
                ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
                connectTologin(username.getText().toString(), password.getText().toString());
            } else {
                ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);

                Toast.makeText(this, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
            }
        } else {
            ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        System.out.println("TourPageActivity: onClick status: " + status);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }

        if (v.getId() == R.id.container && status == STATUS_TOUR) {
            if (pager.getCurrentItem() >= MAX_PAGE) {
                loadPairingScreen();
            }
        } else if (v.getId() == R.id.btnrel_1) {
            System.out.println("TourPageActivity: onClick btn1: " + status);

            switch (status) {
                case STATUS_SETUP:
                    //click SET UP
                    status = STATUS_LOGIN;
                    upDateScreen();
                    break;
                case STATUS_LOGIN:
                case STATUS_LOGOUT:
                    //click login
                    ((LinearLayout) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
                    validateInput();
                    break;
                case STATUS_WELCOME:
                    //click replace
                    //click dont replace
                    if (hasHapitrackLogin) {
                        loadPairingScreen();
                    } else {
                        status = STATUS_TOUR;
                        upDateScreen();
                    }
                    break;
                case STATUS_CREATENEW:
                    //click new to HAPIfork
                    loadWebkit();
                    break;
            }

        } else if (v.getId() == R.id.btnrel_2) {
            System.out.println("TourPageActivity: onClick btn2");

            switch (status) {
                case STATUS_SETUP:
                    //click Sign In
                    status = STATUS_LOGIN;
                    upDateScreen();
                    break;
                case STATUS_CREATENEW:
                    //click Existing User
                    status = STATUS_LOGIN;
                    upDateScreen();
                    break;
                case STATUS_WELCOME:
                    //click dont replace
                    if (hasHapitrack) {
                        System.out.println("TourPageActivity: onClick btn 2 has hapitrack");
                        jumpToMainPageDontReplace();
                    } else {
                        System.out.println("TourPageActivity: onClick btn 2 no hapitrack");
                        jumpToMainPage();
                    }

                    break;
            }

        } else if (v.getId() == R.id.welcomemessage) {
            if (status == STATUS_LOGIN || status == STATUS_SETUP || status == STATUS_CREATENEW || status == STATUS_LOGOUT) {
                //do sign up
                //call webkit for registration
                loadWebkit();

            }
        }
    }

    private void jumpToMainPage() {
        Intent mainIntent = new Intent(this, ViewActivity.class);
        mainIntent.putExtra("STATUS", "CANCEL_SETUP");
        ApplicationEx.getInstance().statsData.setAppMode(StatsData.MODE.BLUETOOTH_DISCONNECT);
        System.out.println("TourPageActivity: onClick btn 2 jumpToMaiAPage: " + ApplicationEx.getInstance().statsData.getAppMode());

        startActivity(mainIntent);
        finish();
    }

    private void jumpToMainPageDontReplace() {
//        ApplicationEx.getInstance().statsData.setAppMode(StatsData.MODE.BLUETOOTH_CONNECT);
        Intent mainIntent = new Intent(this, ViewActivity.class);
        mainIntent.putExtra("STATUS", "CONNECT");
        startActivity(mainIntent);
        finish();
    }

    private void loadPairingScreen() {
        Intent mainIntent = new Intent(this, PairingActivity.class);
        startActivity(mainIntent);
        dismissProgress();
        finish();
    }

    private void loadWebkit() {
        Intent mainIntent = new Intent(this, WebKitActivity.class);
        mainIntent.putExtra("URL", ApplicationEx.URL_REGISTER);
        mainIntent.putExtra("QUICKACTION", false);
        startActivity(mainIntent);
        dismissProgress();
    }

    @Override
    public void onBackPressed() {

        switch (status) {
            case STATUS_LOGIN:
            case STATUS_LOGOUT:
//			status = STATUS_CREATENEW;
//			upDateScreen();
                return;
            case STATUS_WELCOME:
                status = STATUS_LOGIN;
                upDateScreen();
                return;
            case STATUS_CREATENEW:
                //click new to HAPIfork
                status = STATUS_SETUP;
                upDateScreen();
                return;
        }
        super.onBackPressed();  // optional depending on your needs
    }

    public void saveUserData(String username, String password) {
        SharedPreferences userSettings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("userName", username);
        editor.putString("userPassword", password);
        editor.commit();
    }

    public void removeUserData() {
        SharedPreferences userSettings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("userName", "");
        editor.putString("userPassword", "");

        editor.commit();

//        HAPIforkDAO forkDAO = new HAPIforkDAO(this, null);
//        forkDAO.clearTable();
    }

    public String retrieveUserName() {
        // Restore preferences
//		SharedPreferences settings = getSharedPreferences("com.anxa.hapifork", 0);
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        String userName = settings.getString("userName", "");
        return userName;
    }

    public String retrieveUserPassword() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        String password = settings.getString("userPassword", "");
        return password;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
                mIcon11 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}