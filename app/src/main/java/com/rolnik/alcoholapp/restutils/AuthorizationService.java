package com.rolnik.alcoholapp.restutils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.clientservices.AuthorizationClientService;
import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.sharedpreferenceservices.CookieSharedPreferencesService;
import com.rolnik.alcoholapp.sharedpreferenceservices.UserSharedPreferencesService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthorizationService {
    private CookieSharedPreferencesService cookieSharedPreferencesService;
    private UserSharedPreferencesService userService;
    private Retrofit retrofit;

    public AuthorizationService(Retrofit retrofit){
        this.cookieSharedPreferencesService = new CookieSharedPreferencesService(MyApplication.getAppContext());
        this.userService = new UserSharedPreferencesService(MyApplication.getAppContext());
        this.retrofit = retrofit;
    }

    public void renewCookie(){
        AuthorizationClientService authorizationClientService = new AuthorizationClientService(retrofit);

        User loggedUser = userService.getLoggedUser();

        authorizationClientService.renewCookie(loggedUser).enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(@NonNull Call<Response<Void>> call, @NonNull Response<Response<Void>> response) {
                String cookie = response.headers().get("Set-Cookie");
                if(cookie == null || cookie.isEmpty()){
                    Log.e("Renew cookie", "Cookie doesn't exist");
                } else {
                    cookieSharedPreferencesService.saveCookie(cookie);
                    Log.i("Renew cookie", "Cookie renewed, cookie = " + cookie);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response<Void>> call, @NonNull Throwable t) {
                Log.e("Renew cookie", "Exception occurs");
            }
        });
    }

}
