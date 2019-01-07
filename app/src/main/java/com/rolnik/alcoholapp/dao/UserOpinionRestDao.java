package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.UserOpinion;
import com.rolnik.alcoholapp.restUtils.RetrofitCreator;
import com.rolnik.alcoholapp.rest.UserOpinionRest;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Response;

public class UserOpinionRestDao {
    private static UserOpinionRestDao INSTANCE = null;

    private UserOpinionRest client;

    private UserOpinionRestDao() {
        client = RetrofitCreator.createServiceWithCookieService(UserOpinionRest.class);
    }

    public synchronized static UserOpinionRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserOpinionRestDao();
        }

        return INSTANCE;
    }

    public Observable<HashMap<Integer, UserOpinion>> getUserOpinions(){
        return client.getUserOpinions();
    }

    public Observable<Response<Void>> sendLike(Sale sale){
        return client.sendLike(sale.getId());
    }

    public Observable<Response<Void>> sendDislike(Sale sale){
        return client.sendDislike(sale.getId());
    }
}
