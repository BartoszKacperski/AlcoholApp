package com.rolnik.alcoholapp.utils;

import android.databinding.InverseMethod;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class BindingConverters {
    private static Locale locale;

    private BindingConverters() {

    }

    static {
        locale = new Locale("pl", "PL");
    }

    @InverseMethod("currencyStringToDouble")
    public static String currencyDoubleToString(double price) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        return numberFormat.format(price);
    }

    public static double currencyStringToDouble(String price) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        try {
            return numberFormat.parse(price).doubleValue();
        } catch (ParseException e) {
            return 0.0;
        }
    }

    @InverseMethod("rateStringToInt")
    public static String rateToString(int rate) {
        return String.format("(%s)", rate);
    }

    public static int rateStringToInt(String rateString) {
        String rate = rateString.substring(1, rateString.length() - 1);
        if (rate.length() > 0) {
            return Integer.valueOf(rate);
        } else {
            return 0;
        }
    }

    @InverseMethod("alcoholicStrengthFromString")
    public static String alcoholicStrengthToString(double alcoholicStrength){
        return Double.toString(alcoholicStrength) + " %";
    }

    public static double alcoholicStrengthFromString(String alcoholicStrength){
        String tmp = alcoholicStrength.substring(0, alcoholicStrength.length() - 2);

        return Double.valueOf(tmp);
    }

    @InverseMethod("volumeFromString")
    public static String volumeToString(int volume){
        return String.format("%s ml", volume);
    }

    public static int volumeFromString(String volume){
        String tmp = volume.substring(0, volume.length() - 3);

        if(tmp.length() > 0){
            return Integer.valueOf(tmp);
        } else {
            return 0;
        }
    }

}
