package com.rolnik.alcoholapp.views;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.rolnik.alcoholapp.R;

import java.text.NumberFormat;
import java.util.Locale;

public class PricePickerDialog extends Dialog {
    private Button okButton;
    private Button cancelButton;
    private PricePicker pricePicker;
    private ImageView coins;

    public PricePickerDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.price_picker_dialog);

        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);
        pricePicker = findViewById(R.id.pricePicker);
        coins = findViewById(R.id.coins);

        initialize();
    }

    private void initialize(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });
    }

    public void setOkButtonActionListener(View.OnClickListener okButtonActionListener){
        okButton.setOnClickListener(okButtonActionListener);
    }

    public double getPriceDoubleValue(){
        return pricePicker.getDoublePrice();
    }

    public String getCurrencyFormattedPriceValue(Locale locale){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        return numberFormat.format(getPriceDoubleValue());
    }


    private void closeDialog(){
        dismiss();
    }

}
