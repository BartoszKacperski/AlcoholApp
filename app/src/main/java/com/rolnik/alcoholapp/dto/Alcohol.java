package com.rolnik.alcoholapp.dto;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rolnik.alcoholapp.BR;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

@JsonPropertyOrder({ "id", "name", "brand", "kind", "alcoholicStrength", "volume" })
public class Alcohol extends BaseObservable implements GetNameProvider, Serializable {
    private int id;
    private String name;
    private Brand brand;
    private Kind kind;
    private double alcoholicStrength;
    private int volume;

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
        notifyPropertyChanged(BR.brand);
    }

    @Bindable
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
        notifyPropertyChanged(BR.kind);
    }

    @Bindable
    public double getAlcoholicStrength() {
        return alcoholicStrength;
    }

    public void setAlcoholicStrength(double alcoholicStrength) {
        this.alcoholicStrength = alcoholicStrength;
        notifyPropertyChanged(BR.alcoholicStrength);
    }

    @Bindable
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        notifyPropertyChanged(BR.volume);
    }

    @NonNull
    @Override
    public String toString(){
        return name + ", " + volume + "ml " + alcoholicStrength + "%";
    }
}
