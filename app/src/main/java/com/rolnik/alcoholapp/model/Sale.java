package com.rolnik.alcoholapp.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rolnik.alcoholapp.BR;

import java.io.Serializable;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode

@JsonPropertyOrder({"id", "alcohol", "shop", "price", "rate", "user"})
public class Sale extends BaseObservable implements Serializable {
    private int id;
    private Alcohol alcohol;
    private Shop shop;
    private double price;
    private Rate rate;
    private User user;
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
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        notifyPropertyChanged(BR.user);
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
}
