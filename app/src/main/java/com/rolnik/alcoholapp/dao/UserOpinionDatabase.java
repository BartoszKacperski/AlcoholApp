package com.rolnik.alcoholapp.dao;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.rolnik.alcoholapp.DatabaseTypeConverters;
import com.rolnik.alcoholapp.model.UserOpinion;

@Database(entities = {UserOpinion.class}, version = 1, exportSchema = false)
@TypeConverters({DatabaseTypeConverters.class})
public abstract class UserOpinionDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "db";

    private static UserOpinionDatabase INSTANCE;

    public abstract UserOpinionDao userOpinionDao();

    public static UserOpinionDatabase getUserOpinionDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserOpinionDatabase.class, DATABASE_NAME).build();
        }

        return INSTANCE;
    }

}
