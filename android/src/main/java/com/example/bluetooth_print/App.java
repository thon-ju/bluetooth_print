package com.example.bluetooth_print;

import android.app.Application;
import android.content.Context;


public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
