package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.Kind;
import com.rolnik.alcoholapp.rest.AlcoholRest;
import com.rolnik.alcoholapp.restUtils.RetrofitCreator;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class AlcoholRestDao implements Dao<Alcohol> {
    private static AlcoholRestDao INSTANCE = null;


    private AlcoholRest client;

    private AlcoholRestDao(){
        client = RetrofitCreator.createServiceWithCookieService(AlcoholRest.class);
    }

    public synchronized static AlcoholRestDao getInstance(){
        if(INSTANCE == null){
            INSTANCE = new AlcoholRestDao();
        }

        return INSTANCE;
    }

    @Override
    public Observable<List<Alcohol>> getAll() throws HttpClientErrorException {
        return client.getAll();
    }

    @Override
    public Observable<Alcohol> get(int Id) throws HttpClientErrorException {
        return client.get(Id);
    }

    @Override
    public Observable<Integer> add(Alcohol alcohol) throws HttpClientErrorException {
        return client.add(alcohol);
    }

    @Override
    public Observable<Response<Void>> update(Alcohol alcohol) throws HttpClientErrorException {
        return client.update(alcohol.getId(), alcohol);
    }

    @Override
    public Observable<Response<Void>> remove(Alcohol alcohol) throws HttpClientErrorException {
        return client.delete(alcohol.getId());
    }

    public Observable<List<Alcohol>> getAllOfKind(Kind kind) {
        return client.getAllOfKind(kind.getId());
    }

}
