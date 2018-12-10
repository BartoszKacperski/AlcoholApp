package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.restUtils.RetrofitCreator;
import com.rolnik.alcoholapp.rest.ShopRest;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class ShopRestDao implements Dao<Shop> {
    private static ShopRestDao INSTANCE = null;

    private ShopRest client;

    private ShopRestDao() {
        client = RetrofitCreator.createServiceWithCookieService(ShopRest.class);
    }

    public synchronized static ShopRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShopRestDao();
        }

        return INSTANCE;
    }

    @Override
    public Observable<List<Shop>> getAll() throws HttpClientErrorException {
        return client.getAll();
    }

    @Override
    public Observable<Shop> get(int Id) throws HttpClientErrorException {
        return client.get(Id);
    }

    @Override
    public Observable<Integer> add(Shop shop) throws HttpClientErrorException {
        return client.add(shop);
    }

    @Override
    public Observable<Response<Void>> update(Shop shop) throws HttpClientErrorException {
        return client.update(shop.getId(), shop);
    }

    @Override
    public Observable<Response<Void>> remove(Shop shop) throws HttpClientErrorException {
        return client.delete(shop.getId());
    }
}
