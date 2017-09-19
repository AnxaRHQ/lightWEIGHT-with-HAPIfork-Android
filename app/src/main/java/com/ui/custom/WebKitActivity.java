package com.ui.custom;

import com.controllers.ApplicationEx;
import com.hapilabs.lightweight.R;
import com.protocol.WebServices;


import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WebKitActivity extends CommonActivity implements OnClickListener {

    private WebView webView;
    private ImageView quick_action;
    private Button reset;
    private String url_override = null;
    private String authTicket;
    private String url;
    private RelativeLayout progressBar_rl;

    private com.ui.custom.DemoPopupWindow dwLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webkit);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = (WebView) findViewById(R.id.webView1);

        quick_action = (ImageView) findViewById(R.id.header_left);
        quick_action.setOnClickListener(this);

        reset = ((Button) findViewById(R.id.header_right));
        reset.setVisibility(View.INVISIBLE);

        progressBar_rl = (RelativeLayout) findViewById(R.id.dummy_view);

        //check if quick action is activiated
        if (!getIntent().getBooleanExtra("QUICKACTION", true)) {
            //remove header icons
            try {
                ((ImageView) findViewById(R.id.header_left)).setVisibility(View.INVISIBLE);
            } catch (Exception e) {
            }
        }

        url = getIntent().getStringExtra("URL").trim();

        authTicket = ApplicationEx.getInstance().userprofile.getTicket();

        // workaround so that the default browser doesn't take over
        webView.setWebViewClient(new MyWebViewClient());
        webView.setVerticalScrollbarOverlay(true);
        webView.setFocusableInTouchMode(true);
        webView.setFocusable(true);
        webView.setBackgroundColor(0);
        webView.requestFocus(View.FOCUS_DOWN);


        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);

        setCookies(authTicket);

        System.out.println("url: " + url);

        webView.loadUrl(url);
    }


    private void setCookies(String cookie_str) {
        CookieSyncManager syncManager = CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        if (authTicket==null || authTicket.equalsIgnoreCase("null")) {
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptFileSchemeCookies(true);

            if (Build.VERSION.SDK_INT >= 21)
                cookieManager.setAcceptThirdPartyCookies(webView, true);

            System.out.println("setAcceptCookie: " );
        }else {
            cookieManager.setCookie("https://www.hapi.com", WebServices.COOKIE_NAME + "=" + cookie_str);
            System.out.println("setCookies: " + url + " cookie_str: " + WebServices.COOKIE_NAME + "=" + cookie_str);
        }
        syncManager.sync();
    }

    @Override
    protected void onPause() {
        // TODO record here the last time or fork counting
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO record here the current time resume the counter based onPause time and ONResumeTime
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == quick_action) {
            //showquickaction
            dwLeft = new DemoPopupWindow(v, R.layout.add_quickaction/*pass the view of the quick action*/, this);
            dwLeft.showLikePopDownMenu(true);
        } else if (v.getId() == R.id.quickaction_bluetooth) {
            //working/real time mode, bluetooth
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
        }
    }

    private void onClickQuickAction(int position) {
        dwLeft.dismiss();
        switch (position) {
            case 0: {//bluetooth{
                //need to add this processing
                Intent intent = new Intent();
                intent.putExtra("quickaction", 0);
                setResult(RESULT_OK, intent);
                finish();
            }
            break;

            case 1: { //manual
                Intent intent = new Intent();
                intent.putExtra("quickaction", 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            break;
            case 2: {//sync
                Intent mainIntent = new Intent(this, SyncActivity.class);
                startActivity(mainIntent);
                finish();
            }
            case 3: {//about
                url = ApplicationEx.URL_HELP;
                progressBar_rl.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
            break;
            case 4: {//about
                url = ApplicationEx.URL_ABOUT;
                progressBar_rl.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
            break;
            case 5: { //contact us
                url = ApplicationEx.URL_CONTACTUS;
                progressBar_rl.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
            break;
            case 6: { //5dietrules
                url = ApplicationEx.URL_DIET_RULES;
                progressBar_rl.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
            break;
            case 7: { //meal list
                url = ApplicationEx.URL_MEAL_LIST;
                setCookies(authTicket);
                progressBar_rl.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
            break;
            case 8: { //meal dashboard
                url = ApplicationEx.URL_MEAL_DASHBOARD;
                setCookies(authTicket);
                progressBar_rl.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
            break;
            case 9: { //logout
                finish();
                Intent mainIntent = new Intent(this, TourPageActivity.class);
                mainIntent.putExtra("STATUS", TourPageActivity.STATUS_LOGOUT);
                startActivity(mainIntent);
            }
            break;
        }//end switch
    }

    //showquickaction
    private synchronized void showToast() {
        Toast.makeText(this, getResources().getString(R.string.create_account_success), Toast.LENGTH_SHORT).show();
        finish();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String urlFinished) {
            System.out.println("onPageFinished: " + urlFinished);

            progressBar_rl.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String urlToLoad) {
            System.out.println("url: " + urlToLoad);
            progressBar_rl.setVisibility(View.VISIBLE);

            if (urlToLoad.endsWith("/InApp/RegisterSuccess")){
                showToast();
                return true;

            }

            return false;
        }
    }
}
