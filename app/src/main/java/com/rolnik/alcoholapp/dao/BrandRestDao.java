package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Brand;
import com.rolnik.alcoholapp.rest.BrandRest;
import com.rolnik.alcoholapp.restUtils.RetrofitCreator;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class BrandRestDao implements Dao<Brand> {
    private static BrandRestDao INSTANCE = null;

    private BrandRest client;

    private BrandRestDao(){
        client = RetrofitCreator.createServiceWithCookieService(BrandRest.class);
    }

    public synchronized static BrandRestDao getInstance(){
        if(INSTANCE == null){
            INSTANCE = new BrandRestDao();
        }

        return INSTANCE;
    }

    @Override
    public Observable<List<Brand>> getAll() throws HttpStatusCodeException {
        return client.getAll();
    }

    @Override
    public Observable<Brand> get(int Id) throws HttpStatusCodeException {
        return client.get(Id);
    }

    @Override
    public Observable<Integer> add(Brand brand) throws HttpClientErrorException {
        return client.add(brand);
    }

    @Override
    public Observable<Response<Void>> update(Brand brand) throws HttpClientErrorException {
        return client.update(brand.getId(), brand);
    }

    @Override
    public Observable<Response<Void>> remove(Brand brand) throws HttpClientErrorException {
       return client.delete(brand.getId());
    }

}
