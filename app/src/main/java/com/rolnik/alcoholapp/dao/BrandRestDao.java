package com.rolnik.alcoholapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rolnik.alcoholapp.model.Brand;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

public class BrandRestDao implements Dao<Brand> {
    private static BrandRestDao INSTANCE = null;

    private static final String GET_ALL_ADDRESS = RestService.BASE_ADDRESS + "/brands";
    private static final String POST_ADD_ADDRESS = GET_ALL_ADDRESS;

    private RestService<Brand> restService;

    private BrandRestDao(){
        restService = new RestService<>(Brand.class);
    }

    public synchronized static BrandRestDao getInstance(){
        if(INSTANCE == null){
            INSTANCE = new BrandRestDao();
        }

        return INSTANCE;
    }

    @Override
    public List<Brand> getAll() throws HttpStatusCodeException {
        return restService.getForAll(GET_ALL_ADDRESS, new ParameterizedTypeReference<List<Brand>>() {});
    }

    @Override
    public Brand get(int id) throws HttpStatusCodeException {
        return restService.getForObject(GET_ALL_ADDRESS, id);
    }

    @Override
    public Integer add(Brand object) throws HttpClientErrorException {
        return restService.postAddObject(POST_ADD_ADDRESS, object);
    }

    @Override
    public void update(Brand object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.putUpdate(URL, object);
    }

    @Override
    public void remove(Brand object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.deleteRemove(URL, object);
    }

}
