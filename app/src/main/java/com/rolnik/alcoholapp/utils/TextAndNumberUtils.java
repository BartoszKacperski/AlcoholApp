package com.rolnik.alcoholapp.utils;

import java.text.Normalizer;

public class TextAndNumberUtils {
    private TextAndNumberUtils() {

    }

    public static boolean isNumber(String value) {
        if (value == null || value.length() == 0) {
            return false;
        } else {
            try {
                Integer.valueOf(value);
            } catch (NumberFormatException e1) {
                return false;
            }
            return true;
        }
    }

    public static String NEDNormalization(String string){
        return Normalizer
                .normalize(string, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public static boolean areNFDEqual(String first, String second) {
        String firstNormalized = NEDNormalization(first);
        String secondNormalized = NEDNormalization(second);

        return firstNormalized.equals(secondNormalized);
    }

    public static boolean NFDcontains(String string, String contains){
        String stringNormalized = NEDNormalization(string.toLowerCase());
        String containsNormalized = NEDNormalization(contains.toLowerCase());


        return stringNormalized.contains(containsNormalized);
    }
}
