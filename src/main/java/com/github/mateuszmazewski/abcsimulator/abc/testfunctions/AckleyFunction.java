package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public class AckleyFunction extends AbstractTestFunction {

    public AckleyFunction() {
        super(2,
                new double[]{-5.0, -5.0},
                new double[]{5.0, 5.0},
                new double[]{0.0, 0.0},
                0.0,
                false,
                "ackleyFunction.name");
    }

    @Override
    public double getValue(double[] pos) {
        validatePos(pos);
        double x = pos[0];
        double y = pos[1];
        return -20 * Math.exp(-0.2 * Math.sqrt(0.5 * (Math.pow(x, 2) + Math.pow(y, 2))))
                - Math.exp(0.5 * (Math.cos(2 * Math.PI * x) + Math.cos(2 * Math.PI * y)))
                + Math.E + 20.0;
    }
}
