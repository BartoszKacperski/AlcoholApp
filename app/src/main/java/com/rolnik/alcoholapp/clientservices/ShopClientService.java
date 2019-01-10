package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.Shop;
import com.rolnik.alcoholapp.rests.ShopRest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class ShopClientService {
    private ShopRest shopRest;

    public ShopClientService(ShopRest shopRest) {
        this.shopRest = shopRest;
    }

    public Observable<List<Shop>> getAll() {
        return shopRest.getAll();
    }

    public Observable<Shop> get(int Id)  {
        return shopRest.get(Id);
    }

    public Observable<Integer> add(Shop shop)  {
        return shopRest.add(shop);
    }

    public Observable<Response<Void>> update(Shop shop)   {
        return shopRest.update(shop.getId(), shop);
    }

    public Observable<Response<Void>> remove(Shop shop)   {
        return shopRest.delete(shop.getId());
    }
}
