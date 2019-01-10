package com.rolnik.alcoholapp.testutils;

import com.rolnik.alcoholapp.dto.Alcohol;
import com.rolnik.alcoholapp.dto.Brand;
import com.rolnik.alcoholapp.dto.Kind;
import com.rolnik.alcoholapp.dto.Rate;
import com.rolnik.alcoholapp.dto.Sale;
import com.rolnik.alcoholapp.dto.Shop;
import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.dto.UserOpinion;

public interface ModelCreator {
    Alcohol createAlcohol(int id);
    Brand createBrand(int id);
    Kind createKind(int id);
    Rate createRate();
    Sale createSale(int id);
    Shop createShop(int id);
    User createUser(int id);
    UserOpinion createUserOpinion(int id, int saleId);
}
