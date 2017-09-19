package com.ui.custom;

import com.crashlytics.android.Crashlytics;
import com.hapilabs.lightweight.R;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends CommonActivity {


    private final int SPLASH_DISPLAY_LENGTH = 3000; //1 sec splash

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Fabric.with(this, new Crashlytics());


        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.splash);
        ((RelativeLayout) findViewById(R.id.splashweb)).setBackgroundResource(R.drawable.splash);
    
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent;

                mainIntent = new Intent(SplashActivity.this, TourPageActivity.class);
                mainIntent.putExtra("STATUS", TourPageActivity.STATUS_SETUP);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
