package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class SphereFunction extends AbstractTestFunction {

    public SphereFunction() {
        super(2,
                new double[]{-2.0, -2.0},
                new double[]{2.0, 2.0},
                new double[]{0.0, 0.0},
                0.0,
                false);
    }

    @Override
    public double getValue(double[] pos) {
        validatePos(pos);
        double x = pos[0];
        double y = pos[1];
        return Math.pow(x, 2) + Math.pow(y, 2);
    }
}