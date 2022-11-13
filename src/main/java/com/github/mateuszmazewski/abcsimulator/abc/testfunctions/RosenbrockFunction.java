package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class RosenbrockFunction extends AbstractTestFunction {

    public RosenbrockFunction() {
        super(2,
                new double[]{-2.0, -1.0},
                new double[]{2.0, 3.0},
                new double[]{1.0, 1.0},
                0.0);
    }

    @Override
    public double getValue(double[] pos) {
        validatePos(pos);
        double x = pos[0];
        double y = pos[1];
        return 100 * Math.pow(y - Math.pow(x, 2), 2) + Math.pow(1 - x, 2);
    }
}
