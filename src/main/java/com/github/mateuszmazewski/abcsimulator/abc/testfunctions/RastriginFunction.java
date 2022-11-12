package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class RastriginFunction extends AbstractTestFunction {

    public RastriginFunction() {
        super(2,
                new double[]{-5.12, -5.12},
                new double[]{5.12, 5.12},
                new double[]{0.0, 0.0},
                0.0);
    }

    @Override
    public double getValue(double[] pos) {
        validatePos(pos);
        double x = pos[0];
        double y = pos[1];
        double A = 10.0, n = 2;
        return A * n
                + (Math.pow(x, 2) - A * Math.cos(2 * Math.PI * x))
                + (Math.pow(y, 2) - A * Math.cos(2 * Math.PI * y));
    }
}
