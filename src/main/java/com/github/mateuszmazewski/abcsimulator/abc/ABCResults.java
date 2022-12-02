package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;

public class ABCResults {

    private String testFunctionName;
    private double[] minValuePos, foundMinValuePos;
    private double minValue, foundMinValue;
    private double[] lowerBoundaries, upperBoundaries;
    private int maxIter, foodSourcesCount, onlookerBeesCount, trialsLimit;
    private double[][][] allFoodSources;
    private double[][] bestFoodSources;
    private double[] bestFx;
    private double[][] allFx;

    public ABCResults(ArtificialBeeColony abc) {
        createResults(abc);
    }

    public ABCResults() {
        minValuePos = new double[2];
        foundMinValuePos = new double[2];
        lowerBoundaries = new double[2];
        upperBoundaries = new double[2];
    }

    private void createResults(ArtificialBeeColony abc) {
        AbstractTestFunction func = abc.getFunc();
        minValue = func.getMinValue();
        minValuePos = func.getMinValuePos();
        testFunctionName = func.getClass().getSimpleName();
        lowerBoundaries = func.getLowerBoundaries();
        upperBoundaries = func.getUpperBoundaries();
        maxIter = abc.getMaxIter();
        foodSourcesCount = abc.getFoodSourcesCount();
        onlookerBeesCount = abc.getOnlookerBeesCount();
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

    public void setTestFunctionName(String testFunctionName) {
        this.testFunctionName = testFunctionName;
    }

    public double[] getMinValuePos() {
        return minValuePos;
    }

    public void setMinValuePos(double[] minValuePos) {
        this.minValuePos = minValuePos;
    }

    public double[] getFoundMinValuePos() {
        return foundMinValuePos;
    }

    public void setFoundMinValuePos(double[] foundMinValuePos) {
        this.foundMinValuePos = foundMinValuePos;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getFoundMinValue() {
        return foundMinValue;
    }

    public void setFoundMinValue(double foundMinValue) {
        this.foundMinValue = foundMinValue;
    }

    public double[] getLowerBoundaries() {
        return lowerBoundaries;
    }

    public void setLowerBoundaries(double[] lowerBoundaries) {
        this.lowerBoundaries = lowerBoundaries;
    }

    public double[] getUpperBoundaries() {
        return upperBoundaries;
    }

    public void setUpperBoundaries(double[] upperBoundaries) {
        this.upperBoundaries = upperBoundaries;
    }

    public int getMaxIter() {
        return maxIter;
    }

    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    public int getFoodSourcesCount() {
        return foodSourcesCount;
    }

    public void setFoodSourcesCount(int foodSourcesCount) {
        this.foodSourcesCount = foodSourcesCount;
    }

    public int getOnlookerBeesCount() {
        return onlookerBeesCount;
    }

    public void setOnlookerBeesCount(int onlookerBeesCount) {
        this.onlookerBeesCount = onlookerBeesCount;
    }

    public int getTrialsLimit() {
        return trialsLimit;
    }

    public void setTrialsLimit(int trialsLimit) {
        this.trialsLimit = trialsLimit;
    }

    public double[][][] getAllFoodSources() {
        return allFoodSources;
    }

    public void setAllFoodSources(double[][][] allFoodSources) {
        this.allFoodSources = allFoodSources;
    }

    public double[][] getBestFoodSources() {
        return bestFoodSources;
    }

    public void setBestFoodSources(double[][] bestFoodSources) {
        this.bestFoodSources = bestFoodSources;
    }

    public double[] getBestFx() {
        return bestFx;
    }

    public void setBestFx(double[] bestFx) {
        this.bestFx = bestFx;
    }

    public double[][] getAllFx() {
        return allFx;
    }

    public void setAllFx(double[][] allFx) {
        this.allFx = allFx;
    }
}
