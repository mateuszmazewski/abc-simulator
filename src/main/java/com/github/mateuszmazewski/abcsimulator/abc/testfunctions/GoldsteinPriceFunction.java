package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class GoldsteinPriceFunction extends AbstractTestFunction {

    public GoldsteinPriceFunction() {
        super(2,
                new double[]{-2.0, -3.0},
                new double[]{2.0, 1.0},
                new double[]{0.0, -1.0},
                3.0,
                true,
                "goldsteinPriceFunction.name");
    }

    @Override
    protected double calculateValue(double[] pos) {
        double x = pos[0];
        double y = pos[1];
        return (1 + Math.pow(x + y + 1, 2) * (19 - 14 * x + 3 * Math.pow(x, 2) - 14 * y + 6 * x * y + 3 * Math.pow(y, 2)))
                * (30 + Math.pow(2 * x - 3 * y, 2) * (18 - 32 * x + 12 * Math.pow(x, 2) + 48 * y - 36 * x * y + 27 * Math.pow(y, 2)));
    }
}
