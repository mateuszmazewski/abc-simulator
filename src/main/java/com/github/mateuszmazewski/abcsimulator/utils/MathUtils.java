package com.github.mateuszmazewski.abcsimulator.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MathUtils {

    public static final double EPS = 1e-8;
    public static final DecimalFormat decimalFormat2 = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
    public static final DecimalFormat decimalFormat4 = new DecimalFormat("0.0000", DecimalFormatSymbols.getInstance(Locale.US));

    public static String doubleToStringDecimal2(double d) {
        return removeMinusWhenNegativeZero(decimalFormat2.format(d));
    }

    public static String doubleToStringDecimal4(double d) {
        return removeMinusWhenNegativeZero(decimalFormat4.format(d));
    }

    private static String removeMinusWhenNegativeZero(String s) {
        return s.replaceAll("^-(?=0(\\.0*)?$)", ""); // to avoid -0, -0.0, -0.00 etc.
    }

}
