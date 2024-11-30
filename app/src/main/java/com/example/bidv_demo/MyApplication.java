package com.example.bidv_demo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Đăng ký callback để theo dõi vòng đời của tất cả Activity trong ứng dụng
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", false);
                boolean hasReloaded = sharedPreferences.getBoolean("hasReloaded", false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hasReloaded", false);
                editor.putBoolean("isFirstLogin", false);
                editor.apply();
            }
        });

    }
}
