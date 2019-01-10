package com.rolnik.alcoholapp.serviceapi;

import com.rolnik.alcoholapp.model.Sale;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SaleRest {
    @GET("sales")
    Observable<List<Sale>> getAll();
    @GET("sales/{Id}")
    Observable<Sale> get(@Path("Id") int Id);
    @POST("sales")
    Observable<Integer> add(@Body Sale sale);
    @PUT("sales/{Id}")
    Observable<Response<Void>> update(@Path("Id") int Id, @Body Sale sale);
    @DELETE("sales/{Id}")
    Observable<Response<Void>> delete(@Path("Id") int Id);
    @GET("sales/shop/{shopId}/kind/{kindId}")
    Observable<List<Sale>> getAllWhere(@Path("shopId") int shopId, @Path("kindId") int kindId);
    @GET("sales/shop/{shopId}")
    Observable<List<Sale>> getAllWhereShop(@Path("shopId") int shopId);
    @GET("sales/kind/{kindId}")
    Observable<List<Sale>> getAllWhereKind(@Path("kindId") int kindId);
    @GET("sales/user/")
    Observable<List<Sale>> getUserSales();
}
