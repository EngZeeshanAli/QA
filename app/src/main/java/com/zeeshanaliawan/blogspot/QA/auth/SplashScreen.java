package com.zeeshanaliawan.blogspot.QA.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.material.snackbar.Snackbar;
import com.zeeshanaliawan.blogspot.QA.R;

public class SplashScreen extends AppCompatActivity {
    private final static int SPLASH_TIME = 2000;
    private boolean firstTime = true;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.main_splash_layout);
        connection();
    }

    private void connection() {
        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                // TODO: Handle the connection...
                if (isConnected) {
                    runSplash();
                    if (!firstTime) {
                        final Snackbar bar = snackbar("Internet Connected", Snackbar.LENGTH_LONG);
                        bar.show();
                    }
                    firstTime = false;
                } else {
                    Snackbar bar = snackbar("internet Connection Fails!", Snackbar.LENGTH_INDEFINITE);
                    bar.show();
                }
            }
        });
    }

    private void runSplash() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashScreen.this, Login.class));
            }
        };
        new Handler().postDelayed(run, SPLASH_TIME);
    }

    @Override
    protected void onStop() {
        Tovuti.from(this).stop();
        super.onStop();
    }

    private Snackbar snackbar(String msg, int length) {
        return Snackbar.make(mainLayout, msg, length)
                .setAction("Close", null)
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
    }

}
