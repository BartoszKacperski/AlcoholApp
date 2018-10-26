package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.Rate;
import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.model.UserOpinion;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import lombok.Getter;

public class UserOpinionRestDao {
    private static UserOpinionRestDao INSTANCE = null;

    private static final String GET_ALL_ADDRESS = RestService.BASE_ADDRESS + "/opinions";

    private RestService<UserOpinion> restService;

    private UserOpinionRestDao() {
        restService = new RestService<>(UserOpinion.class);
    }

    public synchronized static UserOpinionRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserOpinionRestDao();
        }

        return INSTANCE;
    }

    public List<UserOpinion> getUserOpinions(User user){
        String URL = GET_ALL_ADDRESS + "/" + user.getId();

        return restService.getForAll(URL, new ParameterizedTypeReference<List<UserOpinion>>() {});
    }

}
