package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.Sale;
import com.rolnik.alcoholapp.rests.SaleRest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class SaleClientService {
    private SaleRest saleRest;

    public SaleClientService(SaleRest saleRest) {
        this.saleRest = saleRest;
    }

    public Observable<List<Sale>> getAll() {
        return saleRest.getAll();
    }

    public Observable<Sale> get(int Id) {
        return saleRest.get(Id);
    }

    public Observable<Integer> add(Sale sale) {
        return saleRest.add(sale);
    }

    public Observable<Response<Void>> update(Sale sale) {
        return saleRest.update(sale.getId(), sale);
    }

    public Observable<Response<Void>> remove(Sale sale) {
        return saleRest.delete(sale.getId());
    }

    public Observable<List<Sale>> getAllWhere(int shopId, int kindId) {
        return saleRest.getAllWhere(shopId, kindId);
    }

    public Observable<List<Sale>> getAllWhereShop(int shopId) {
        return saleRest.getAllWhereShop(shopId);
    }

    public Observable<List<Sale>> getAllWhereKind(int kindId) {
        return saleRest.getAllWhereKind(kindId);
    }

    public Observable<List<Sale>> getUserSales() {
        return saleRest.getUserSales();
    }
}
