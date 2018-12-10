package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.model.UserOpinion;
import com.rolnik.alcoholapp.restUtils.RetrofitCreator;
import com.rolnik.alcoholapp.rest.UserOpinionRest;

import java.util.List;

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

    public Observable<List<UserOpinion>> getUserOpinions(){
        return client.getUserOpinions();
    }

    public Observable<Response<Void>> sendLike(User user){
        return client.sendLike(user.getId(), user.getId());
    }

    public Observable<Response<Void>> sendDislike(User user){
        return client.sendDislike(user.getId(), user.getId());
    }
}
