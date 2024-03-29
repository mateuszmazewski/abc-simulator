package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

import com.github.mateuszmazewski.abcsimulator.utils.MathUtils;
import com.github.mateuszmazewski.abcsimulator.utils.ObservableResourceFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class AbstractTestFunction {

    public final static double MIN_X = -10000.0;
    public final static double MIN_Y = -10000.0;
    public final static double MAX_X = 10000.0;
    public final static double MAX_Y = 10000.0;

    private final int dim;
    private final double[] lowerBoundaries, upperBoundaries;
    private final double[] defaultLowerBoundaries, defaultUpperBoundaries;
    private final double[] globalMinValuePos;
    private double[] minValuePos;
    private final double globalMinValue;
    private double minValue;
    private final StringProperty name = new SimpleStringProperty();
    private final boolean chartInLogScale;

    public AbstractTestFunction(int dim,
                                double[] defaultLowerBoundaries,
                                double[] defaultUpperBoundaries,
                                double[] globalMinValuePos,
                                double globalMinValue,
                                boolean chartInLogScale,
                                String nameBindingKey) {

        validateArgs(dim, defaultLowerBoundaries, defaultUpperBoundaries, globalMinValuePos);

        this.dim = dim;
        this.defaultLowerBoundaries = defaultLowerBoundaries;
        this.defaultUpperBoundaries = defaultUpperBoundaries;
        this.lowerBoundaries = defaultLowerBoundaries.clone();
        this.upperBoundaries = defaultUpperBoundaries.clone();
        this.globalMinValuePos = globalMinValuePos;
        this.minValuePos = globalMinValuePos.clone();
        this.globalMinValue = globalMinValue;
        this.minValue = globalMinValue;
        this.chartInLogScale = chartInLogScale;

        ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();
        nameProperty().bind(messagesFactory.getStringBinding(nameBindingKey));
    }

    private void validateArgs(int dim,
                              double[] lowerBoundaries,
                              double[] upperBoundaries,
                              double[] minValuePos) throws IllegalArgumentException {
        if (dim <= 0) {
            throw new IllegalArgumentException("dim must be positive");
        }
        if (lowerBoundaries == null || upperBoundaries == null) {
            throw new IllegalArgumentException("boundaries cannot be null");
        }
        if (minValuePos == null) {
            throw new IllegalArgumentException("minValuePos cannot be null");
        }
        if (lowerBoundaries.length != dim || upperBoundaries.length != dim) {
            throw new IllegalArgumentException("lower and upper boundaries must have length equal to the function's dimension");
        }
        if (minValuePos.length != dim) {
            throw new IllegalArgumentException("minValuePos length must be equal to the function's dimension");
        }
        for (int i = 0; i < dim; i++) {
            if (lowerBoundaries[i] >= upperBoundaries[i]) {
                throw new IllegalArgumentException("lower bound must be less than upper bound");
            }
        }
    }

    public double getValue(double[] pos) {
        validatePos(pos);
        return calculateValue(pos);
    }

    protected abstract double calculateValue(double[] pos);

    public double getLog10Value(double[] pos) {
        double value = getValue(pos);
        if (value < 1e-16) {
            // In order to avoid log10(0) == -Infinity or log10(negative) == NaN
            return -16.0;
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

    public void setMinimum(double[] minValuePos, double minValue) {
        if (isGlobalMinimumInCurrentRanges()) {
            this.minValuePos = globalMinValuePos;
            this.minValue = globalMinValue;
        } else {
            this.minValuePos = minValuePos;
            this.minValue = minValue;
        }

    }

    public boolean isGlobalMinimumInCurrentRanges() {
        return globalMinValuePos[0] >= lowerBoundaries[0] && globalMinValuePos[0] <= upperBoundaries[0]
                && globalMinValuePos[1] >= lowerBoundaries[1] && globalMinValuePos[1] <= upperBoundaries[1];
    }


    @Override
    public String toString() {
        return name.getValue();
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

    public double[] getMinValuePos() {
        return minValuePos;
    }

    public double getMinValue() {
        return minValue;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public boolean isChartInLogScale() {
        return chartInLogScale;
    }
}
