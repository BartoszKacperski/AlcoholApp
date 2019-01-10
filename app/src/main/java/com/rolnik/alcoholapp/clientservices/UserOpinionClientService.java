package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.Sale;
import com.rolnik.alcoholapp.dto.UserOpinion;
import com.rolnik.alcoholapp.rests.UserOpinionRest;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Response;

public class UserOpinionClientService {
    private UserOpinionRest userOpinionRest;

    public UserOpinionClientService(UserOpinionRest userOpinionRest) {
        this.userOpinionRest = userOpinionRest;
    }

    public Observable<HashMap<Integer, UserOpinion>> getUserOpinions(){
        return userOpinionRest.getUserOpinions();
    }

    public Observable<Response<Void>> sendLike(Sale sale){
        return userOpinionRest.sendLike(sale.getId());
    }

    public Observable<Response<Void>> sendDislike(Sale sale){
        return userOpinionRest.sendDislike(sale.getId());
    }
}
