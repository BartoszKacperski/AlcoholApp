package com.rolnik.alcoholapp.testutils;

import com.rolnik.alcoholapp.dto.Alcohol;
import com.rolnik.alcoholapp.dto.Brand;
import com.rolnik.alcoholapp.dto.Kind;
import com.rolnik.alcoholapp.dto.Rate;
import com.rolnik.alcoholapp.dto.Sale;
import com.rolnik.alcoholapp.dto.Shop;
import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.dto.UserOpinion;

public class PlainModelCreator implements ModelCreator {

    @Override
    public Alcohol createAlcohol(int id){
        Alcohol alcohol = new Alcohol();

        alcohol.setId(id);
        alcohol.setName("Alcohol " + id);
        alcohol.setBrand(createBrand(id));
        alcohol.setKind(createKind(id));
        alcohol.setAlcoholicStrength(id * 1.1);
        alcohol.setVolume(100 * id);

        return alcohol;
    }

    @Override
    public Brand createBrand(int id){
        Brand brand = new Brand();

        brand.setId(id);
        brand.setName("Brand " + id);

        return brand;
    }

    @Override
    public Kind createKind(int id){
        Kind kind = new Kind();

        kind.setId(id);
        kind.setName("Kind " + id);

        return kind;
    }

    @Override
    public Rate createRate() {
        return new Rate();
    }

    @Override
    public Sale createSale(int id){
        Sale sale = new Sale();

        sale.setId(id);
        sale.setAlcohol(createAlcohol(id));
        sale.setShop(createShop(id));
        sale.setPrice(id * 1.1);
        sale.setRate(createRate());

        return sale;
    }

    @Override
    public Shop createShop(int id){
        Shop shop = new Shop();

        shop.setId(id);
        shop.setName("Shop " + id);

        return shop;
    }

    @Override
    public User createUser(int id){
        User user = new User();

        user.setId(id);
        user.setLogin("User " + id);
        user.setPassword("Password " + id);
        user.setEmail(id + "Email@email.pl");

        return user;
    }

    @Override
    public UserOpinion createUserOpinion(int id, int saleId){
        UserOpinion userOpinion = new UserOpinion();

        userOpinion.setId(id);
        userOpinion.setSaleId(saleId);
        userOpinion.setOpinion(UserOpinion.LIKE);

        return userOpinion;
    }
}
