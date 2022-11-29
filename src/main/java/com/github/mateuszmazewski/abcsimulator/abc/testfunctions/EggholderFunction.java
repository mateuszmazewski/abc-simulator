package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class EggholderFunction extends AbstractTestFunction {

    public EggholderFunction() {
        super(2,
                new double[]{-512.0, -512.0},
                new double[]{512.0, 512.0},
                new double[]{512.0, 404.2319},
                -959.6407,
                false,
                "eggholderFunction.name");
    }

    @Override
    protected double calculateValue(double[] pos) {
        double x = pos[0];
        double y = pos[1];
        return -(y + 47.0) * Math.sin(Math.sqrt(Math.abs(x / 2.0 + (y + 47.0))))
                - x * Math.sin(Math.sqrt(Math.abs(x - (y + 47.0))));
    }
}
