package com.rolnik.alcoholapp.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application myApp;

    public AppModule(Application myApp){
        this.myApp = myApp;
    }

    @Provides
    @Singleton
    Application providesApplication(){
        return myApp;
    }
}
