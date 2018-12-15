package com.rolnik.alcoholapp.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.rolnik.alcoholapp.R;

public class PricePicker extends LinearLayout {
    private NumberPicker ones;
    private NumberPicker digits;

    private int onesMin = 0;
    private int onesMax = 1000;
    private int digitsMin = 0;
    private int digitsMax = 99;

    public PricePicker(Context context) {
        super(context);
        initialize(context);
    }

    public PricePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public PricePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){
        initializePickers(context);
        initializeValues();
    }

    private void initializePickers(Context context) {
        LayoutInflater.from(context).inflate(R.layout.price_picker_layout, this);

        ones = findViewById(R.id.ones);
        digits = findViewById(R.id.digits);
    }


    private void initializeValues() {
        ones.setMinValue(onesMin);
        ones.setMaxValue(onesMax);

        digits.setMinValue(digitsMin);
        digits.setMaxValue(digitsMax);
    }

    public void setPrice(int onesValue, int digitsValue){
        ones.setValue(onesValue);
        digits.setValue(digitsValue);
    }

    public double getDoublePrice(){
        double value = ones.getValue() * 100.0 + digits.getValue();

        return value/100.0;
    }

}
