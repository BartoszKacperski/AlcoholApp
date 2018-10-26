package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Alcohol;
import com.rolnik.alcoholapp.model.Brand;
import com.rolnik.alcoholapp.model.Kind;
import com.rolnik.alcoholapp.model.Rate;
import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.Shop;
import com.rolnik.alcoholapp.model.User;

public class RestDaoFactory {

    private RestDaoFactory() {

    }


    public static Dao<Alcohol> getAlcoholDao() {
        return AlcoholRestDao.getInstance();
    }

    public static Dao<Brand> getBrandDao() {
        return BrandRestDao.getInstance();
    }

    public static Dao<Kind> getKindDao() {
        return KindRestDao.getInstance();
    }

    public static Dao<Sale> getSaleDao() {
        return SaleRestDao.getInstance();
    }

    public static Dao<Shop> getShopDao() {
        return ShopRestDao.getInstance();
    }

    public static Dao<User> getUserDao() {
        return UserRestDao.getInstance();
    }
}
