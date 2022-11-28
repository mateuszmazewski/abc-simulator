package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class ThreeHumpCamelFunction extends AbstractTestFunction {

    public ThreeHumpCamelFunction() {
        super(2,
                new double[]{-3.4, -3.4},
                new double[]{3.4, 3.4},
                new double[]{0.0, 0.0},
                0.0,
                true);
    }

    @Override
    public double getValue(double[] pos) {
        validatePos(pos);
        double x = pos[0];
        double y = pos[1];
        return 2 * Math.pow(x, 2) - 1.05 * Math.pow(x, 4) + Math.pow(x, 6) / 6.0 + x * y + Math.pow(y, 2);
    }
}
