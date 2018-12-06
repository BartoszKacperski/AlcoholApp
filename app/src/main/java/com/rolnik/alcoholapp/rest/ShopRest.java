package com.rolnik.alcoholapp.rest;

import android.databinding.ObservableList;

import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.Shop;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ShopRest {
    @GET("shops")
    Observable<List<Shop>> getAll();
    @GET("shops/{Id}")
    Observable<Shop> get(@Path("Id") int Id);
    @POST("shops")
    Observable<Integer> add(@Body Shop shop);
    @PUT("shops/{Id}")
    Observable<Response<Void>> update(@Path("Id") int Id, @Body Shop shop);
    @DELETE("shops/{Id}")
    Observable<Response<Void>> delete(@Path("Id") int Id);
}
