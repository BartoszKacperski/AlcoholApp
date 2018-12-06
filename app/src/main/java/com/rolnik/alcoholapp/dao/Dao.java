package com.rolnik.alcoholapp.dao;

import android.databinding.ObservableList;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public interface Dao<T> {
    Observable<List<T>> getAll() throws HttpClientErrorException;
    Observable<T> get(int id) throws HttpClientErrorException;
    Observable<Integer> add(T object) throws HttpClientErrorException;
    Observable<Response<Void>> update(T object) throws HttpClientErrorException;
    Observable<Response<Void>> remove(T object) throws HttpClientErrorException;
}
