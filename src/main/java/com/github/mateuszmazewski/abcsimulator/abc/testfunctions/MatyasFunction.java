package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class MatyasFunction extends AbstractTestFunction {

    public MatyasFunction() {
        super(2,
                new double[]{-10.0, -10.0},
                new double[]{10.0, 10.0},
                new double[]{0.0, 0.0},
                0.0,
                true,
                "matyasFunction.name");
    }

    @Override
    public double getValue(double[] pos) {
        validatePos(pos);
        double x = pos[0];
        double y = pos[1];
        return 0.26 * (Math.pow(x, 2) + Math.pow(y, 2)) - 0.48 * x * y;
    }
}
