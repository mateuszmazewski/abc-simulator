package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class BealeFunction extends AbstractTestFunction {

    public BealeFunction() {
        super(2,
                new double[]{-4.0, -4.0},
                new double[]{4.0, 4.0},
                new double[]{3.0, 0.5},
                0.0,
                true,
                "bealeFunction.name");
    }

    @Override
    protected double calculateValue(double[] pos) {
        double x = pos[0];
        double y = pos[1];
        return Math.pow(1.5 - x + x * y, 2) + Math.pow(2.25 - x + x * Math.pow(y, 2), 2) + Math.pow(2.625 - x + x * Math.pow(y, 3), 2);
    }
}
