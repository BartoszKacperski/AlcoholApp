package com.rolnik.alcoholapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rolnik.alcoholapp.R;
import com.rolnik.alcoholapp.model.User;


public class UserService {
    private SharedPreferences sharedPreferences;
    private String userLoginKey;
    private String userIdKey;
    private String userEmailKey;
    private String userPasswordKey;

    public UserService(Context context) {
        String sharedPreferencesName = context.getString(R.string.preference_login);
        this.sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        this.userIdKey = context.getString(R.string.user_id);
        this.userLoginKey = context.getString(R.string.user_login);
        this.userEmailKey = context.getString(R.string.user_email);
        this.userPasswordKey = context.getString(R.string.user_password);
    }

    public void logOutUser() {
        if (checkIfUserLogged()) {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.remove(userIdKey);
            sharedPreferencesEditor.remove(userLoginKey);
            sharedPreferencesEditor.remove(userEmailKey);

            sharedPreferencesEditor.apply();
        }
    }

    public void logInUser(User user) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.putString(userLoginKey, user.getLogin());
        sharedPreferencesEditor.putInt(userIdKey, user.getId());
        sharedPreferencesEditor.putString(userEmailKey, user.getEmail());
        sharedPreferencesEditor.putString(userPasswordKey, user.getPassword());

        sharedPreferencesEditor.apply();
    }

    public boolean checkIfUserLogged() {
        return sharedPreferences.contains(userIdKey) && sharedPreferences.contains(userLoginKey);
    }

    public String getLoggedUserLogin(){
        return sharedPreferences.getString(userLoginKey, "");
    }

    public String getLoggedUserEmail() { return sharedPreferences.getString(userEmailKey, "");
    }

    public int getLoggedUserId(){
        return sharedPreferences.getInt(userIdKey, -1);
    }

    public String getLoggedUserPassword(){
        return sharedPreferences.getString(userPasswordKey, "");
    }

    public User getLoggedUser(){
        User user = new User();

        user.setId(getLoggedUserId());
        user.setLogin(getLoggedUserLogin());
        user.setEmail(getLoggedUserEmail());
        user.setPassword(getLoggedUserPassword());

        return user;
    }

}
