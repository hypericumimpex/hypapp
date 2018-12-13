package com.hypericum.hypapp.utils;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.onesignal.OneSignal;
import com.hypericum.hypapp.fcm.MyNotificationOpenedHandler;
import com.hypericum.hypapp.fcm.MyNotificationReceivedHandler;



public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler())
                .init();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
