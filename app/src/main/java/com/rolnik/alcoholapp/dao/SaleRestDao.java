package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.restUtils.RetrofitCreator;
import com.rolnik.alcoholapp.rest.SaleRest;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class SaleRestDao implements Dao<Sale> {
    private static SaleRestDao INSTANCE = null;

    private SaleRest client;

    private SaleRestDao() {
        client = RetrofitCreator.createServiceWithCookieService(SaleRest.class);
    }

    public synchronized static SaleRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SaleRestDao();
        }

        return INSTANCE;
    }

    @Override
    public Observable<List<Sale>> getAll() throws HttpClientErrorException {
        return client.getAll();
    }

    @Override
    public Observable<Sale> get(int Id) throws HttpClientErrorException {
        return client.get(Id);
    }

    @Override
    public Observable<Integer> add(Sale sale) throws HttpClientErrorException {
        return client.add(sale);
    }

    @Override
    public Observable<Response<Void>> update(Sale sale) throws HttpClientErrorException {
        return client.update(sale.getId(), sale);
    }

    @Override
    public Observable<Response<Void>> remove(Sale sale) throws HttpClientErrorException {
        return client.delete(sale.getId());
    }

    public Observable<List<Sale>> getAllWhere(int shopId, int kindId) throws HttpClientErrorException {
        return client.getAllWhere(shopId, kindId);
    }

    public Observable<List<Sale>> getAllWhereShop(int shopId) throws HttpClientErrorException {
        return client.getAllWhereShop(shopId);
    }

    public Observable<List<Sale>> getAllWhereKind(int kindId) throws HttpClientErrorException {
        return client.getAllWhereKind(kindId);
    }

    public Observable<List<Sale>> getUserSales() throws HttpClientErrorException {
        return client.getUserSales();
    }
}
