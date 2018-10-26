package com.rolnik.alcoholapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rolnik.alcoholapp.model.Kind;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public class KindRestDao implements Dao<Kind> {
    private static KindRestDao INSTANCE = null;

    private static final String GET_ALL_ADDRESS = RestService.BASE_ADDRESS + "/kinds";
    private static final String POST_ADD_OBJECT = GET_ALL_ADDRESS;

    private RestService<Kind> restService;

    private KindRestDao(){
        restService = new RestService<>(Kind.class);
    }

    public synchronized static KindRestDao getInstance(){
        if(INSTANCE == null){
            INSTANCE = new KindRestDao();
        }

        return INSTANCE;
    }

    @Override
    public List<Kind> getAll() throws HttpClientErrorException {
        return restService.getForAll(GET_ALL_ADDRESS, new ParameterizedTypeReference<List<Kind>>() {});
    }

    @Override
    public Kind get(int id) throws HttpClientErrorException {
        return restService.getForObject(GET_ALL_ADDRESS, id);
    }

    @Override
    public Integer add(Kind object) throws HttpClientErrorException {
        return restService.postAddObject(POST_ADD_OBJECT, object);
    }

    @Override
    public void update(Kind object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.putUpdate(URL, object);
    }

    @Override
    public void remove(Kind object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.deleteRemove(URL, object);
    }
}
