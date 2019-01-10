package com.rolnik.alcoholapp.restUtils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.rolnik.alcoholapp.MyApplication;
import com.rolnik.alcoholapp.dao.UserRestDao;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.utils.CookieService;
import com.rolnik.alcoholapp.utils.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationService {
    private CookieService cookieService;
    private UserService userService;

    public AuthorizationService(){
        this.cookieService = new CookieService(MyApplication.getAppContext());
        this.userService = new UserService(MyApplication.getAppContext());
    }

    public void renewCookie(){
        UserRestDao userRestDao = UserRestDao.getInstance();

        User loggedUser = userService.getLoggedUser();

        userRestDao.renewCookie(loggedUser).enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(@NonNull Call<Response<Void>> call, @NonNull Response<Response<Void>> response) {
                String cookie = response.headers().get("Set-Cookie");
                if(cookie == null || cookie.isEmpty()){
                    Log.e("Renew cookie", "Cookie doesn't exist");
                } else {
                    cookieService.saveCookie(cookie);
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
