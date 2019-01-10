package com.rolnik.alcoholapp.serviceapi;

import com.rolnik.alcoholapp.model.Brand;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BrandRest {
    @GET("brands")
    Observable<List<Brand>> getAll();
    @GET("brands/{Id}")
    Observable<Brand> get(@Path("Id") int Id);
    @POST("brands")
    Observable<Integer> add(@Body Brand brand);
    @PUT("brands/{Id}")
    Observable<Response<Void>> update(@Path("Id") int Id, @Body Brand brand);
    @DELETE("brands/{Id}")
    Observable<Response<Void>> delete(@Path("Id") int Id);
}
