package com.rolnik.alcoholapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rolnik.alcoholapp.model.Alcohol;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public class AlcoholRestDao implements Dao<Alcohol> {
    private static AlcoholRestDao INSTANCE = null;

    private static final String GET_ALL_ADDRESS = RestService.BASE_ADDRESS + "/alcohols";
    private static final String POST_ADD_ADDRESS = GET_ALL_ADDRESS;

    private RestService<Alcohol> restService;

    private AlcoholRestDao(){
        restService = new RestService<>(Alcohol.class);
    }

    public synchronized static AlcoholRestDao getInstance(){
        if(INSTANCE == null){
            INSTANCE = new AlcoholRestDao();
        }

        return INSTANCE;
    }

    @Override
    public List<Alcohol> getAll() throws HttpClientErrorException {
        return restService.getForAll(GET_ALL_ADDRESS, new ParameterizedTypeReference<List<Alcohol>>() {});
    }

    @Override
    public Alcohol get(int id) throws HttpClientErrorException {
        return restService.getForObject(GET_ALL_ADDRESS, id);
    }

    @Override
    public Integer add(Alcohol object) throws HttpClientErrorException {
        return  restService.postAddObject(POST_ADD_ADDRESS, object);
    }

    @Override
    public void update(Alcohol object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.putUpdate(URL, object);
    }

    @Override
    public void remove(Alcohol object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.deleteRemove(URL, object);
    }

}
