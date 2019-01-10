package com.rolnik.alcoholapp.clientservices;

import com.rolnik.alcoholapp.dto.Alcohol;
import com.rolnik.alcoholapp.dto.Kind;
import com.rolnik.alcoholapp.rests.AlcoholRest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class AlcoholClientService {
    private AlcoholRest alcoholDao;

    public AlcoholClientService(AlcoholRest alcoholDao) {
        this.alcoholDao = alcoholDao;
    }


    public Observable<List<Alcohol>> getAll() {
        return alcoholDao.getAll();
    }

    public Observable<Alcohol> get(int Id) {
        return alcoholDao.get(Id);
    }

    public Observable<Integer> add(Alcohol alcohol) {
        return alcoholDao.add(alcohol);
    }

    public Observable<Response<Void>> update(Alcohol alcohol) {
        return alcoholDao.update(alcohol.getId(), alcohol);
    }

    public Observable<Response<Void>> remove(Alcohol alcohol) {
        return alcoholDao.delete(alcohol.getId());
    }

    public Observable<List<Alcohol>> getAllOfKind(Kind kind) {
        return alcoholDao.getAllOfKind(kind.getId());
    }

}
