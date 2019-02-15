package com.rolnik.alcoholapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.rolnik.alcoholapp.components.ApiComponent;
import com.rolnik.alcoholapp.components.DaggerApiComponent;
import com.rolnik.alcoholapp.modules.ApiModule;
import com.rolnik.alcoholapp.modules.AppModule;
import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {
    private static Context context;
    private ApiComponent apiComponent;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        initPortraitMode();

        if(BuildConfig.DEBUG){
            LeakCanary.install(this);
        }
    }

    public void initPortraitMode(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleAdapter() {
            @Override
            public void onActivityCreated(Activity a, Bundle savedInstanceState) {
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public ApiComponent getNetComponent(){
        if(apiComponent == null){
            apiComponent = DaggerApiComponent.builder()
                    .apiModule(new ApiModule())
                    .build();
        }

        return apiComponent;
    }
}
