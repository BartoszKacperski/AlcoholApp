package com.rolnik.alcoholapp.serviceapi;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthorizationRest {
    @POST("/login")
    Observable<Response<Void>> login(@Header("Authorization") String credentials);
    @POST("/login")
    Call<Response<Void>> renewCookie(@Header("Authorization") String credentials);
}
