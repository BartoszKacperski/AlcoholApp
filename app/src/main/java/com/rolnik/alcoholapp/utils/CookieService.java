package com.rolnik.alcoholapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rolnik.alcoholapp.R;

public class CookieService {
    private SharedPreferences sharedPreferences;
    private String cookieKey;

    public CookieService(Context context) {
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_auth), Context.MODE_PRIVATE);
        this.cookieKey = context.getString(R.string.cookie);
    }

    public void saveCookie(String cookie) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.putString(cookieKey, cookie);

        sharedPreferencesEditor.apply();
    }

    public void deleteCookie() {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.remove(cookieKey);

        sharedPreferencesEditor.apply();
    }

    public String getCookie(){
        return sharedPreferences.getString(cookieKey, "");
    }

    public boolean isCookieExists(){
        return sharedPreferences.contains(cookieKey);
    }

    public boolean isCookieValid(){
        return this.isCookieExists() && !getCookie().isEmpty();
    }
}
