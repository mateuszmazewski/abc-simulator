package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

public abstract class AbstractTestFunction {

    public final static double MIN_X = -10000.0;
    public final static double MIN_Y = -10000.0;
    public final static double MAX_X = 10000.0;
    public final static double MAX_Y = 10000.0;
    private final int dim;
    private final double[] lowerBoundaries, upperBoundaries;
    private final double[] globalMinPos;
    private final double globalMinValue;
    private String name;

    public AbstractTestFunction(int dim,
                                double[] lowerBoundaries,
                                double[] upperBoundaries,
                                double[] globalMinPos,
                                double globalMinValue) {

        validateArgs(dim, lowerBoundaries, upperBoundaries, globalMinPos);

        this.dim = dim;
        this.lowerBoundaries = lowerBoundaries;
        this.upperBoundaries = upperBoundaries;
        this.globalMinPos = globalMinPos;
        this.globalMinValue = globalMinValue;
    }

    private void validateArgs(int dim,
                              double[] lowerBoundaries,
                              double[] upperBoundaries,
                              double[] globalMinPos) throws IllegalArgumentException {
        if (dim <= 0) {
            throw new IllegalArgumentException("dim must be positive");
        }
        if (lowerBoundaries == null || upperBoundaries == null) {
            throw new IllegalArgumentException("boundaries cannot be null");
        }
        if (globalMinPos == null) {
            throw new IllegalArgumentException("globalMinPos cannot be null");
        }
        if (lowerBoundaries.length != dim || upperBoundaries.length != dim) {
            throw new IllegalArgumentException("lower and upper boundaries must have length equal to the function's dimension");
        }
        if (globalMinPos.length != dim) {
            throw new IllegalArgumentException("globalMinPos length must be equal to the function's dimension");
        }
    }

    public abstract double getValue(double[] pos);

    protected void validatePos(double[] pos) throws IllegalArgumentException {
        if (pos == null || pos.length != dim) {
            throw new IllegalArgumentException("position length must be equal to function's dimension");
        }

        for (int i = 0; i < dim; i++) {
            if (pos[i] < lowerBoundaries[i] || pos[i] > upperBoundaries[i]) {
                throw new IllegalArgumentException("variable is out of boundaries");
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public int getDim() {
        return dim;
    }

    public double[] getLowerBoundaries() {
        return lowerBoundaries;
    }

    public double[] getUpperBoundaries() {
        return upperBoundaries;
    }

    public double[] getGlobalMinPos() {
        return globalMinPos;
    }

    public double getGlobalMinValue() {
        return globalMinValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
