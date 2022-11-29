package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class BoothFunction extends AbstractTestFunction {

    public BoothFunction() {
        super(2,
                new double[]{-10.0, -10.0},
                new double[]{10.0, 10.0},
                new double[]{1.0, 3.0},
                0.0,
                true,
                "boothFunction.name");
    }

    @Override
    public double getValue(double[] pos) {
        validatePos(pos);
        double x = pos[0];
        double y = pos[1];
        return Math.pow(x + 2 * y - 7.0, 2) + Math.pow(2 * x + y - 5.0, 2);
    }
}
