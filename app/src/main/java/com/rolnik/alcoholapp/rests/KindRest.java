package com.rolnik.alcoholapp.rests;

import com.rolnik.alcoholapp.dto.Kind;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface KindRest {
    @GET("kinds")
    Observable<List<Kind>> getAll();
    @GET("kinds/{Id}")
    Observable<Kind> get(@Path("Id") int Id);
    @POST("kinds")
    Observable<Integer> add(@Body Kind kind);
    @PUT("kinds/{Id}")
    Observable<Response<Void>> update(@Path("Id") int Id, @Body Kind kind);
    @DELETE("kinds/{Id}")
    Observable<Response<Void>> delete(@Path("Id") int Id);
}
