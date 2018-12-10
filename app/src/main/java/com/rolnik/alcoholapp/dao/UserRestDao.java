package com.rolnik.alcoholapp.dao;

import com.rolnik.alcoholapp.model.User;
import com.rolnik.alcoholapp.rest.UserRest;
import com.rolnik.alcoholapp.restUtils.RetrofitCreator;

import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;

public class UserRestDao implements Dao<User> {
    private static UserRestDao INSTANCE = null;

    private UserRest client;

    private UserRestDao() {
        client = RetrofitCreator.createServiceWithCookieService(UserRest.class);
    }


    public synchronized static UserRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRestDao();
        }

        return INSTANCE;
    }

    @Override
    public Observable<List<User>> getAll() throws HttpClientErrorException {
        return client.getAll();
    }

    @Override
    public Observable<User> get(int Id) throws HttpClientErrorException {
        return client.get(Id);
    }

    @Override
    public Observable<Integer> add(User user) throws HttpClientErrorException {
        return client.add(user);
    }

    @Override
    public Observable<Response<Void>> update(User user) throws HttpClientErrorException {
        return client.update(user.getId(), user);
    }

    @Override
    public Observable<Response<Void>> remove(User user) throws HttpClientErrorException {
        return client.delete(user.getId());
    }

    public Observable<Integer> register(User user){
        return client.register(user);
    }

    public Observable<Response<Void>> login(User user) throws HttpClientErrorException {
        UserRest clientWithoutCookie = RetrofitCreator.createServiceWithoutCookieService(UserRest.class);

        String credentials = Credentials.basic(user.getLogin(), user.getPassword());

        return clientWithoutCookie.login(credentials);
    }

    public Observable<Boolean> resendEmail(User user) throws HttpClientErrorException{
        return client.resendEmail(user.getId());
    }

    public Call<Response<Void>> renewCookie(User user) throws HttpClientErrorException {
        UserRest clientWithoutCookie = RetrofitCreator.createServiceWithoutCookieService(UserRest.class);

        String credentials = Credentials.basic(user.getLogin(), user.getPassword());

        return clientWithoutCookie.renewCookie(credentials);
    }
}
