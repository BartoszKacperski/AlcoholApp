package com.rolnik.alcoholapp.mock;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.components.ApiComponent;
import com.rolnik.alcoholapp.components.DaggerApiComponent;
import com.rolnik.alcoholapp.modules.AppModule;

public class MockApplication extends MyApplication {
    @Override
    public ApiComponent getNetComponent(){
        return DaggerApiComponent.builder().apiModule(new MockApiModule()).appModule(new AppModule(this)).build();
    }
}
