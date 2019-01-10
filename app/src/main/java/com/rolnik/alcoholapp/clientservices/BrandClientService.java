package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.Brand;
import com.rolnik.alcoholapp.rests.BrandRest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class BrandClientService {
    private BrandRest brandRest;

    public BrandClientService(BrandRest brandRest){
        this.brandRest = brandRest;
    }


    public Observable<List<Brand>> getAll() {
        return brandRest.getAll();
    }

    public Observable<Brand> get(int Id) {
        return brandRest.get(Id);
    }

    public Observable<Integer> add(Brand brand) {
        return brandRest.add(brand);
    }

    public Observable<Response<Void>> update(Brand brand) {
        return brandRest.update(brand.getId(), brand);
    }

    public Observable<Response<Void>> remove(Brand brand) {
       return brandRest.delete(brand.getId());
    }

}
