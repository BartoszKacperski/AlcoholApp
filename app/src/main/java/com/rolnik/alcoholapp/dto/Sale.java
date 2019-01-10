package com.rolnik.alcoholapp.dto;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rolnik.alcoholapp.BR;

import java.io.Serializable;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)

@JsonPropertyOrder({"id", "alcohol", "shop", "price", "rate", "user"})
public class Sale extends BaseObservable implements Serializable {
    private int id;
    private Alcohol alcohol;
    private Shop shop;
    private double price;
    private Rate rate;
    @JsonIgnore
    private boolean wasLiked;
    @JsonIgnore
    private boolean wasDisliked;

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public Alcohol getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(Alcohol alcohol) {
        this.alcohol = alcohol;
        notifyPropertyChanged(BR.alcohol);
    }

    @Bindable
    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
        notifyPropertyChanged(BR.shop);
    }

    @Bindable
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
        notifyPropertyChanged(BR.rate);
    }

    @Bindable
    public boolean isWasLiked() {
        return wasLiked;
    }

    public void setWasLiked(boolean wasLiked) {
        this.wasLiked = wasLiked;
        notifyPropertyChanged(BR.wasLiked);
    }

    @Bindable
    public boolean isWasDisliked() {
        return wasDisliked;
    }

    public void setWasDisliked(boolean wasDisliked) {
        this.wasDisliked = wasDisliked;
        notifyPropertyChanged(BR.wasDisliked);
    }

    public void addLike(){
        this.wasLiked = true;
        this.rate.setPositiveRates(this.rate.getPositiveRates() + 1);
        if(this.wasDisliked) {
            this.wasDisliked = false;
            this.rate.setNegativeRates(this.rate.getNegativeRates() - 1);
        }
        notifyPropertyChanged(BR.wasLiked);
        notifyPropertyChanged(BR.wasDisliked);
        notifyPropertyChanged(BR.positiveRates);
        notifyPropertyChanged(BR.negativeRates);
    }

    public void addDislike(){
        this.wasDisliked = true;
        this.rate.setNegativeRates(this.rate.getNegativeRates() + 1);
        if(this.wasLiked) {
            this.wasLiked = false;
            this.rate.setPositiveRates(this.rate.getPositiveRates() - 1);
        }
        notifyPropertyChanged(BR.wasDisliked);
        notifyPropertyChanged(BR.wasLiked);
        notifyPropertyChanged(BR.negativeRates);
        notifyPropertyChanged(BR.positiveRates);
    }
}
