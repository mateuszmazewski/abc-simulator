package com.github.mateuszmazewski.abcsimulator.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MathUtils {

    public static final double EPS = 10e-8; // Double numbers precision
    public static final DecimalFormat decimalFormat3 = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.US));

    public static double roundToTwoDecimalPlaces(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

    public static String doubleToString(double d) {
        String s = MathUtils.decimalFormat3.format(d);
        if (s.equals("-0.000")) {
            s = "0.000";
        }
        return s;
    }

}
