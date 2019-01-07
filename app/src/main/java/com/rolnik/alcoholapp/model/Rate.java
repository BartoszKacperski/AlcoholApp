package com.rolnik.alcoholapp.model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

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

@JsonPropertyOrder({"positiveRates", "negativeRates"})
public class Rate extends BaseObservable implements Serializable {
    private int positiveRates;
    private int negativeRates;

    @Bindable
    public int getPositiveRates() {
        return positiveRates;
    }

    public void setPositiveRates(int positiveRates) {
        this.positiveRates = positiveRates;
        notifyPropertyChanged(BR.positiveRates);
    }

    @Bindable
    public int getNegativeRates() {
        return negativeRates;
    }

    public void setNegativeRates(int negativeRates) {
        this.negativeRates = negativeRates;
        notifyPropertyChanged(BR.negativeRates);
    }
}
