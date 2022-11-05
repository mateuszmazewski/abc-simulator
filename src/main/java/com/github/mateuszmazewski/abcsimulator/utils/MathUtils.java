package com.github.mateuszmazewski.abcsimulator.utils;

public class MathUtils {

    public static double roundToTwoDecimalPlaces(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

}
