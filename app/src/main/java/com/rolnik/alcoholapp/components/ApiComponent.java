package com.rolnik.alcoholapp.components;

import com.rolnik.alcoholapp.activities.AddSaleActivity;
import com.rolnik.alcoholapp.activities.EditSaleActivity;
import com.rolnik.alcoholapp.activities.FilterSalesActivity;
import com.rolnik.alcoholapp.activities.LoginActivity;
import com.rolnik.alcoholapp.activities.MySalesActivity;
import com.rolnik.alcoholapp.activities.RegisterActivity;
import com.rolnik.alcoholapp.activities.SearchSalesActivity;
import com.rolnik.alcoholapp.activities.StartActivity;
import com.rolnik.alcoholapp.modules.ApiModule;
import com.rolnik.alcoholapp.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApiModule.class, AppModule.class})
public interface ApiComponent {
    void inject(StartActivity startActivity);
    void inject(LoginActivity loginActivity);
    void inject(AddSaleActivity addSaleActivity);
    void inject(EditSaleActivity editSaleActivity);
    void inject(FilterSalesActivity filterSalesActivity);
    void inject(MySalesActivity mySalesActivity);
    void inject(RegisterActivity registerActivity);
    void inject(SearchSalesActivity searchSalesActivity);
}
