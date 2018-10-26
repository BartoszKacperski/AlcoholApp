package com.rolnik.alcoholapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rolnik.alcoholapp.model.User;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public class UserRestDao implements Dao<User> {
    private static UserRestDao INSTANCE = null;

    private static final String GET_ALL_ADDRESS = RestService.BASE_ADDRESS + "/users";
    private static final String POST_ADD_OBJECT = RestService.BASE_ADDRESS;

    private RestService<User> restService;

    private UserRestDao() {
        restService = new RestService<>(User.class);
    }


    public synchronized static UserRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRestDao();
        }

        return INSTANCE;
    }

    @Override
    public List<User> getAll() throws HttpClientErrorException {
        return restService.getForAll(GET_ALL_ADDRESS, new ParameterizedTypeReference<List<User>>() {
        });
    }

    @Override
    public User get(int id) throws HttpClientErrorException {
        return restService.getForObject(GET_ALL_ADDRESS, id);
    }

    @Override
    public Integer add(User object) throws HttpClientErrorException {
        return restService.postAddObject(POST_ADD_OBJECT, object);
    }

    @Override
    public void update(User object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.putUpdate(URL, object);
    }

    @Override
    public void remove(User object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.deleteRemove(URL, object);
    }

    public Integer register(User user){
        String URL = RestService.BASE_ADDRESS + "/register";

        return restService.postAddObject(URL, user);
    }

    public Integer login(User user) throws HttpClientErrorException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> entity = new HttpEntity<>(user, headers);

        ResponseEntity<Integer> loginResponse = restService.getRestTemplate().exchange(RestService.BASE_ADDRESS + "/login", HttpMethod.POST, entity, Integer.class);

        return loginResponse.getBody();
    }

    public Boolean resendEmail(User user) throws HttpClientErrorException{
        String URL = RestService.BASE_ADDRESS + "/mail/resend/" + user.getId();

        System.out.println(URL);
        ResponseEntity<Boolean> loginResponse = restService.getRestTemplate().exchange(URL, HttpMethod.GET, null, Boolean.class);

        return loginResponse.getBody();
    }
}
