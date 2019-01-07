package com.rolnik.alcoholapp.rest;



import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.model.UserOpinion;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserOpinionRest {
    @GET("opinions/")
    Observable<HashMap<Integer, UserOpinion>> getUserOpinions();
    @PUT("sales/{Id}/like")
    Observable<Response<Void>> sendLike(@Path("Id") int Id);
    @PUT("sales/{Id}/dislike")
    Observable<Response<Void>> sendDislike(@Path("Id") int Id);

}
