package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Kind;
import com.rolnik.alcoholapp.rest.KindRest;
import com.rolnik.alcoholapp.rest.RetrofitCreator;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class KindRestDao implements Dao<Kind> {
    private static KindRestDao INSTANCE = null;

    private KindRest client;

    private KindRestDao(){
        client = RetrofitCreator.createService(KindRest.class);
    }

    public synchronized static KindRestDao getInstance(){
        if(INSTANCE == null){
            INSTANCE = new KindRestDao();
        }

        return INSTANCE;
    }

    @Override
    public Observable<List<Kind>> getAll() throws HttpClientErrorException {
        return client.getAll();
    }

    @Override
    public Observable<Kind> get(int Id) throws HttpClientErrorException {
        return client.get(Id);
    }

    @Override
    public Observable<Integer> add(Kind kind) throws HttpClientErrorException {
        return client.add(kind);
    }

    @Override
    public Observable<Response<Void>> update(Kind kind) throws HttpClientErrorException {
        return client.update(kind.getId(), kind);
    }

    @Override
    public Observable<Response<Void>> remove(Kind kind) throws HttpClientErrorException {
        return client.delete(kind.getId());
    }
}
