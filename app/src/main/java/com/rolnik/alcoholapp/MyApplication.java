package com.rolnik.alcoholapp;

import android.app.Application;
import android.content.Context;

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

        if(BuildConfig.DEBUG){
            LeakCanary.install(this);
        }
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public ApiComponent getNetComponent(){
        if(apiComponent == null){
            apiComponent = DaggerApiComponent.builder()
                    .appModule(new AppModule(this))
                    .apiModule(new ApiModule())
                    .build();
        }

        return apiComponent;
    }
}
