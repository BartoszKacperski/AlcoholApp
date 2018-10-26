package com.rolnik.alcoholapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public interface Dao<T> {
    List<T> getAll() throws HttpClientErrorException;
    T get(int id) throws HttpClientErrorException;
    Integer add(T object) throws HttpClientErrorException;
    void update(T object) throws HttpClientErrorException;
    void remove(T object) throws HttpClientErrorException;
}
