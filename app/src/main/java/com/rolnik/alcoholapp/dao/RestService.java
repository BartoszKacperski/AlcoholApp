package com.rolnik.alcoholapp.dao;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;



public class RestService<T> {
    public static final String BASE_ADDRESS = "https://alco-app.herokuapp.com";

    @Getter
    private RestTemplate restTemplate;
    private Class<T> type;


    public RestService(Class<T> type) {
        restTemplate = new RestTemplate(getConverters());
        this.type = type;
    }

    private List<HttpMessageConverter<?>> getConverters(){
        List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<>();

        httpMessageConverters.add(new MappingJackson2HttpMessageConverter());

        return httpMessageConverters;
    }

    public List<T> getForAll(String URL, ParameterizedTypeReference<List<T>> parameterizedTypeReference) throws HttpClientErrorException {
        ResponseEntity<List<T>> response = restTemplate.exchange(URL, HttpMethod.GET, HttpEntity.EMPTY, parameterizedTypeReference);

        return response.getBody();
    }

    public T getForObject(String baseURL, int id) throws HttpClientErrorException {
        String URL = MessageFormat.format(baseURL + "/{0}", id);

        ResponseEntity<T> response = restTemplate.exchange(URL, HttpMethod.GET, null, type);

        return response.getBody();
    }


    public Integer postAddObject(String URL, T object) throws HttpClientErrorException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> entity = new HttpEntity<>(object, httpHeaders);

        ResponseEntity<Integer> response = restTemplate.exchange(URL, HttpMethod.POST, entity, Integer.class);

        return response.getBody();
    }

    public void putUpdate(String URL, T object) throws HttpClientErrorException{
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> entity = new HttpEntity<>(object, httpHeaders);


        restTemplate.put(URL, entity);
    }

    public void deleteRemove(String URL, T object) throws HttpClientErrorException{
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> entity = new HttpEntity<>(object, httpHeaders);

        restTemplate.delete(URL, entity);
    }
}
