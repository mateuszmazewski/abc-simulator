package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

import com.github.mateuszmazewski.abcsimulator.utils.MathUtils;

public abstract class AbstractTestFunction {

    public final static double MIN_X = -10000.0;
    public final static double MIN_Y = -10000.0;
    public final static double MAX_X = 10000.0;
    public final static double MAX_Y = 10000.0;
    private final int dim;
    private final double[] lowerBoundaries, upperBoundaries;
    private final double[] defaultLowerBoundaries, defaultUpperBoundaries;
    private final double[] globalMinPos;
    private final double globalMinValue;
    private String name;
    private final boolean chartInLogScale;

    public AbstractTestFunction(int dim,
                                double[] defaultLowerBoundaries,
                                double[] defaultUpperBoundaries,
                                double[] globalMinPos,
                                double globalMinValue,
                                boolean chartInLogScale) {

        validateArgs(dim, defaultLowerBoundaries, defaultUpperBoundaries, globalMinPos);

        this.dim = dim;
        this.defaultLowerBoundaries = defaultLowerBoundaries;
        this.defaultUpperBoundaries = defaultUpperBoundaries;
        this.lowerBoundaries = defaultLowerBoundaries.clone();
        this.upperBoundaries = defaultUpperBoundaries.clone();
        this.globalMinPos = globalMinPos;
        this.globalMinValue = globalMinValue;
        this.chartInLogScale = chartInLogScale;
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
        for (int i = 0; i < dim; i++) {
            if (lowerBoundaries[i] >= upperBoundaries[i]) {
                throw new IllegalArgumentException("lower bound must be less than upper bound");
            }
        }
    }

    public abstract double getValue(double[] pos);

    public double getLog10Value(double[] pos) {
        double value = getValue(pos);
        if (value >= 0.0 && value < MathUtils.EPS) {
            return -4.0;
        } else {
            return Math.log10(value);
        }
    }

    protected void validatePos(double[] pos) throws IllegalArgumentException {
        if (pos == null || pos.length != dim) {
            throw new IllegalArgumentException("position length must be equal to function's dimension");
        }

        for (int i = 0; i < dim; i++) {
            if (pos[i] < lowerBoundaries[i] - MathUtils.EPS || pos[i] > upperBoundaries[i] + MathUtils.EPS) {
                throw new IllegalArgumentException("variable is out of boundaries");
            }
        }
    }

    public void restoreDefaultRanges() {
        for (int i = 0; i < dim; i++) {
            lowerBoundaries[i] = defaultLowerBoundaries[i];
            upperBoundaries[i] = defaultUpperBoundaries[i];
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

    public boolean isChartInLogScale() {
        return chartInLogScale;
    }
}
