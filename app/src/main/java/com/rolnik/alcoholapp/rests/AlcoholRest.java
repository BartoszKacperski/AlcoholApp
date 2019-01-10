package com.rolnik.alcoholapp.rests;

import com.rolnik.alcoholapp.dto.Alcohol;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AlcoholRest {
    @GET("alcohols")
    Observable<List<Alcohol>> getAll();
    @GET("alcohols/{Id}")
    Observable<Alcohol> get(@Path("Id") int Id);
    @POST("alcohols")
    Observable<Integer> add(@Body Alcohol alcohol);
    @PUT("alcohols/{Id}")
    Observable<Response<Void>> update(@Path("Id") int Id, @Body Alcohol alcohol);
    @DELETE("alcohols/{Id}")
    Observable<Response<Void>> delete(@Path("Id") int Id);
    @GET("alcohols/kind/{Id}")
    Observable<List<Alcohol>> getAllOfKind(@Path("Id") int Id);
}
