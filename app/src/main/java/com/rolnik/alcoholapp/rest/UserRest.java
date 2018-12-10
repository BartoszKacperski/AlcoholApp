package com.rolnik.alcoholapp.rest;

import com.rolnik.alcoholapp.model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserRest {
    @GET("users")
    Observable<List<User>> getAll();
    @GET("users/{Id}")
    Observable<User> get(@Path("Id") int Id);
    @POST("users")
    Observable<Integer> add(@Body User user);
    @PUT("users/{Id}")
    Observable<Response<Void>> update(@Path("Id") int Id, @Body User user);
    @DELETE("users/{Id}")
    Observable<Response<Void>> delete(@Path("Id") int Id);
    @POST("/register")
    Observable<Integer> register(@Body User user);
    @POST("/login")
    Observable<Response<Void>> login(@Header("Authorization") String credentials);
    @GET("mail/resend/{Id}")
    Observable<Boolean> resendEmail(@Path("Id") int Id);
    @POST("/login")
    Call<Response<Void>> renewCookie(@Header("Authorization") String credentials);
}
