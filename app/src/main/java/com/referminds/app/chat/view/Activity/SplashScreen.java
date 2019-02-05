package com.referminds.app.chat.view.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.referminds.app.chat.R;

//Displays splash screen on opening of application
public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    // Splash screen timer
    public static int SPLASH_TIME_OUT = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, SPLASH_TIME_OUT);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

}
