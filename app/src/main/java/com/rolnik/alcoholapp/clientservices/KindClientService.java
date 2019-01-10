package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.Kind;
import com.rolnik.alcoholapp.rests.KindRest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class KindClientService {
    private KindRest kindRest;

    public KindClientService(KindRest kindRest){
        this.kindRest = kindRest;
    }

    public Observable<List<Kind>> getAll()  {
        return kindRest.getAll();
    }

    public Observable<Kind> get(int Id)  {
        return kindRest.get(Id);
    }

    public Observable<Integer> add(Kind kind)   {
        return kindRest.add(kind);
    }

    public Observable<Response<Void>> update(Kind kind)  {
        return kindRest.update(kind.getId(), kind);
    }

    public Observable<Response<Void>> remove(Kind kind) {
        return kindRest.delete(kind.getId());
    }
}
