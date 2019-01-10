package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.rests.AuthorizationRest;

import io.reactivex.Observable;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthorizationClientService {
    private AuthorizationRest authorizationRest;

    public AuthorizationClientService(Retrofit retrofit){
        this.authorizationRest = retrofit.create(AuthorizationRest.class);
    }

    public Observable<Response<Void>> login(User user) {
        String credentials = getCredentials(user);

        return authorizationRest.login(credentials);
    }

    public Call<Response<Void>> renewCookie(User user) {
        String credentials = getCredentials(user);

        return authorizationRest.renewCookie(credentials);
    }

    private String getCredentials(User user){
        return Credentials.basic(user.getLogin(), user.getPassword());
    }
}
