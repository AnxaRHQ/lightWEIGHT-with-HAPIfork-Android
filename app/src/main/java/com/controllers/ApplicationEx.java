package com.controllers;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.hapilabs.lightweight.R;
import com.obj.UserProfile;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;

public class ApplicationEx extends Application {
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static ApplicationEx instance = null;
    public StatsData statsData;

//    public static final String URL_CONTACTUS = "https://www.hapi.com/apps/contact.asp";
//    public static String URL_ABOUT = "https://www.hapi.com/apps/about-hapifork.asp";
//    public static String URL_PRIVACY = "https://www.hapi.com/apps/privacy.asp";
//    public static String URL_HELP = "https://www.hapi.com/apps/help-hapifork.asp";
//    public static String URL_PROFILE = "https://www.hapi.com/account/mobile/inapp/hapiforkprofile";
//    public static String URL_MEAL_LIST = "https://www.hapi.com/account/mobile/inapp/hapiforkmeals";
//    public static String URL_MEAL_DASHBOARD = "https://www.hapi.com/account/mobile/inapp/hapiforkgraph";
    public static final String URL_CONTACTUS = "http://api.hapilabs.com/hapicoach/webkit/v1/help/contacthapi?culture=id";
    public static String URL_ABOUT = "http://api.hapilabs.com/hapicoach/webkit/v1/help/abouthapifork?culture=id";
    public static String URL_PRIVACY = "https://www.hapi.com/apps/privacy.asp";
    public static String URL_HELP = "http://api.hapilabs.com/hapicoach/webkit/v1/help/helphapifork?culture=id";
    public static String URL_PROFILE = "https://www.hapi.com/account/mobile/inapp/hapiforkprofile";
    public static String URL_DIET_RULES = "http://api.hapilabs.com/hapicoach/webkit/v1/help/DietRules?culture=id";
    public static String URL_MEAL_LIST = "https://www.hapi.com/account/mobile/inapp/hapiforkmeals";
    public static String URL_MEAL_DASHBOARD = "https://www.hapi.com/account/mobile/inapp/hapiforkgraph";
    public static String URL_REGISTER = "http://www.hapi.com/account/mobile/inapp/register?lang=id";

    public static final String COLOR_TEXT_GREEN = "#468D01";
    public static final String COLOR_TEXT_RED = "#E61E21";
    public static final String COLOR_BG_GREEN = "#E2FFC6";
    public static final String COLOR_BG_RED = "#FFDBDC";
    public static final String COLOR_BG_FAILED = "#ff0000";
    public static final String COLOR_BG_SUCCESS = "#6AAC28";
    public static final String COLOR_TEXT_BLACK = "#000000";
    public static final String COLOR_TEXT_WHITE = "#FFFFFF";
    public static final String COLOR_TEXT_GRAY = "#CACACA";

    public static String sharedKey_sendData = "UDp8 1Cp7 a1Rw"; //
    public static String sharedKey_deviceInfo = "T1Zs 5PtH Q48n"; //
//	public static String sharedKey="An2x A3ct 306i7e";//mobile


    public String deviceStatus = "";
    public String sendAccountLinkStatus = "";

    public static String sharedKey = "A7x4 809ile 7aRk";//mobilehapifork

    public BluetoothDevice bluetoothDevice;

    public boolean helpDone = false;
    public boolean hasHAPIforkPairedToTheApp = false;

    public UserProfile userprofile = null;

    public ApplicationEx() {
        super();
        statsData = new StatsData();
        userprofile = new UserProfile();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ApplicationEx getInstance() {
        return instance;
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return asHex(sha1hash);
    }

    public static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    public String retrieveDeviceId(String deviceAddress) {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        String userName = settings.getString(deviceAddress, "");
        return userName;
    }

    public void saveDeviceId(String deviceAddress, String deviceIdToSave) {

        System.out.println("saveDeviceId: " + deviceAddress + " :" + deviceIdToSave);
        SharedPreferences userSettings = getSharedPreferences(getResources().getString(R.string.preference_filename), 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(deviceAddress, deviceIdToSave);
        editor.commit();
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
}
