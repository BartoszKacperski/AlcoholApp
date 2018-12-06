package com.rolnik.alcoholapp.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.rolnik.alcoholapp.model.Sale;
import com.rolnik.alcoholapp.model.UserOpinion;

import java.util.List;


@Dao
public interface UserOpinionDao {
    @Query("SELECT COUNT(*) FROM useropinion WHERE opinion= 1 AND saleId = :sale")
    int wasSaleLiked(Sale sale);

    @Query("SELECT COUNT(*) FROM useropinion WHERE opinion= -1 AND saleId = :sale")
    int wasSaleDisliked(Sale sale);

    @Insert
    void insertAll(List <UserOpinion> userOpinions);

    @Insert
    void insert(UserOpinion userOpinions);
}
