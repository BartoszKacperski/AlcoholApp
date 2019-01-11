package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.User;
import com.rolnik.alcoholapp.rests.UserRest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class UserClientService {
    private UserRest userRest;

    public UserClientService(UserRest userRest) {
        this.userRest = userRest;
    }

    public Observable<List<User>> getAll() {
        return userRest.getAll();
    }

    public Observable<User> get(int Id) {
        return userRest.get(Id);
    }

    public Observable<Integer> add(User user) {
        return userRest.add(user);
    }

    public Observable<Response<Void>> update(User user) {
        return userRest.update(user.getId(), user);
    }

    public Observable<Response<Void>> remove(User user) {
        return userRest.delete(user.getId());
    }

    public Observable<Response<Void>> register(User user) {
        return userRest.register(user);
    }

    public Observable<Response<Void>> resendEmail(String email) {
        return userRest.resendEmail(email);
    }
}
