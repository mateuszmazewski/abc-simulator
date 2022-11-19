package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;

public class ABCResults {

    private String testFunctionName;
    private double[] minValuePos, foundMinValuePos;
    private double minValue, foundMinValue;
    private double[] lowerBoundaries, upperBoundaries;
    private int maxIter, swarmSize, trialsLimit;
    private double[][][] allFoodSources;
    private double[][] bestFoodSources;
    private double[] bestFx;
    private double[][] allFx;

    public ABCResults(ArtificialBeeColony abc) {
        createResults(abc);
    }

    private void createResults(ArtificialBeeColony abc) {
        AbstractTestFunction func = abc.getFunc();
        minValue = func.getMinValue();
        minValuePos = func.getMinValuePos();
        testFunctionName = func.getClass().getSimpleName();
        lowerBoundaries = func.getLowerBoundaries();
        upperBoundaries = func.getUpperBoundaries();
        maxIter = abc.getMaxIter();
        swarmSize = abc.getSwarmSize();
        trialsLimit = abc.getTrialsLimit();
        allFoodSources = abc.getAllFoodSources();
        bestFoodSources = abc.getBestFoodSources();
        bestFx = abc.getBestFx();
        allFx = abc.getAllFx();
        foundMinValue = abc.getBestFx()[maxIter];
        foundMinValuePos = abc.getBestFoodSources()[maxIter];
    }

    public String getTestFunctionName() {
        return testFunctionName;
    }

    public double[] getMinValuePos() {
        return minValuePos;
    }

    public double[] getFoundMinValuePos() {
        return foundMinValuePos;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getFoundMinValue() {
        return foundMinValue;
    }

    public double[] getLowerBoundaries() {
        return lowerBoundaries;
    }

    public double[] getUpperBoundaries() {
        return upperBoundaries;
    }

    public int getMaxIter() {
        return maxIter;
    }

    public int getSwarmSize() {
        return swarmSize;
    }

    public int getTrialsLimit() {
        return trialsLimit;
    }

    public double[][][] getAllFoodSources() {
        return allFoodSources;
    }

    public double[][] getBestFoodSources() {
        return bestFoodSources;
    }

    public double[] getBestFx() {
        return bestFx;
    }

    public double[][] getAllFx() {
        return allFx;
    }
}
