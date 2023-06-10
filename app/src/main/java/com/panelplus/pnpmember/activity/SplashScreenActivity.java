package com.panelplus.pnpmember.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
//import android.support.v7.app.Ap/pCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.panelplus.pnpmember.R;

public class SplashScreenActivity extends Activity {

    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 2000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}