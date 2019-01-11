package com.rolnik.alcoholapp.rests;

import com.rolnik.alcoholapp.dto.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Observable<Response<Void>> register(@Body User user);
    @GET("mail/resend")
    Observable<Response<Void>> resendEmail(@Query("email") String email);
}
