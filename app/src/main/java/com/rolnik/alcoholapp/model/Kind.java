package com.rolnik.alcoholapp.model;



import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rolnik.alcoholapp.BR;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

@JsonPropertyOrder({"id", "name"})
public class Kind extends BaseObservable implements GetNameProvider, Serializable {
    private int id;
    private String name;

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @NonNull
    @Override
    public String toString(){
        return name;
    }
}
