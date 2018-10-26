package com.rolnik.alcoholapp;

import android.arch.persistence.room.TypeConverter;

import com.rolnik.alcoholapp.model.Sale;

public class DatabaseTypeConverters {
    private DatabaseTypeConverters(){

    }

    @TypeConverter
    public static int toInt(Sale sale){
        return sale == null ? -1 : sale.getId();
    }

    @TypeConverter
    public static Sale fromInt(int value){
        Sale sale = new Sale();

        sale.setId(value);

        return sale;
    }


}
