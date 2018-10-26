package com.rolnik.alcoholapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rolnik.alcoholapp.model.Shop;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public class ShopRestDao implements Dao<Shop> {
    private static ShopRestDao INSTANCE = null;

    private static final String GET_ALL_ADDRESS = RestService.BASE_ADDRESS + "/shops";
    private static final String POST_ADD_OBJECT = GET_ALL_ADDRESS;

    private RestService<Shop> restService;

    private ShopRestDao() {
        restService = new RestService<>(Shop.class);
    }

    public synchronized static ShopRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShopRestDao();
        }

        return INSTANCE;
    }

    @Override
    public List<Shop> getAll() throws HttpClientErrorException {
        return restService.getForAll(GET_ALL_ADDRESS, new ParameterizedTypeReference<List<Shop>>() {
        });
    }

    @Override
    public Shop get(int id) throws HttpClientErrorException {
        return restService.getForObject(GET_ALL_ADDRESS, id);
    }

    @Override
    public Integer add(Shop object) throws HttpClientErrorException {
        return restService.postAddObject(POST_ADD_OBJECT, object);
    }

    @Override
    public void update(Shop object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.putUpdate(URL, object);
    }

    @Override
    public void remove(Shop object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.deleteRemove(URL, object);
    }
}
