package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;

import java.util.Arrays;
import java.util.Random;

public class ArtificialBeeColony {

    public static final int MIN_FOOD_SOURCES_COUNT = 1;
    public static final int MAX_FOOD_SOURCES_COUNT = 1000000;
    public static final int MAX_ITER_LOWER_LIMIT = 1;
    public static final int MAX_ITER_UPPER_LIMIT = 1000000;
    public static final int MIN_TRIALS_LIMIT = 0;
    public static final int MAX_TRIALS_LIMIT = 1000000;

    // -------------------------INPUT PARAMETERS-------------------------
    private final int foodSourcesCount;
    private final int maxIter;
    private final int trialsLimit; // Max number of trials to improve the solution
    private final AbstractTestFunction func;

    // ------------------------FUNCTION PARAMETERS------------------------
    private int dim;
    private double[] lb;
    private double[] ub;

    // -------------------------CURRENT SOLUTIONS-------------------------
    private double[][] foodSources;
    private double[] fx;
    private double[] fitness;
    private int[] trials;

    // --------------OUTPUT: SOLUTIONS IN EACH ITERATION--------------
    private double[][][] allFoodSources;
    private double[][] allFx;
    private double[][] bestFoodSources;
    private double[] bestFx;
    // -------------------------------------------------------------------

    private final Random rng = new Random();

    public ArtificialBeeColony(int foodSourcesCount, int maxIter, AbstractTestFunction func, int trialsLimit) {
        validateArgs(foodSourcesCount, maxIter, func, trialsLimit);

        this.foodSourcesCount = foodSourcesCount;
        this.maxIter = maxIter;
        this.func = func;
        this.trialsLimit = trialsLimit;
    }

    private void validateArgs(int foodSourcesCount, int maxIter, AbstractTestFunction func, int limit) {
        if (foodSourcesCount < MIN_FOOD_SOURCES_COUNT) {
            throw new IllegalArgumentException("number of food sources must be positive");
        }
        if (maxIter < MAX_ITER_LOWER_LIMIT) {
            throw new IllegalArgumentException("number of iterations must be positive");
        }
        if (func == null) {
            throw new IllegalArgumentException("function cannot be null");
        }
        if (limit < MIN_TRIALS_LIMIT) {
            throw new IllegalArgumentException("limit of trials to improve the solution must be positive");
        }
    }

    public void run() {
        init();
        rememberFoodSources(0); // Remember initial random solutions

        for (int i = 1; i <= maxIter; i++) {
            employedBeePhase();
            onlookerBeePhase();
            rememberFoodSources(i);
            scoutBeePhase();
        }
    }

    private void init() {
        dim = func.getDim();
        lb = func.getLowerBoundaries();
        ub = func.getUpperBoundaries();

        foodSources = new double[foodSourcesCount][dim];
        fx = new double[foodSourcesCount];
        fitness = new double[foodSourcesCount];
        trials = new int[foodSourcesCount];
        Arrays.fill(trials, 0);

        // maxIter + 1 because we also save initial random solutions
        allFoodSources = new double[maxIter + 1][foodSourcesCount][dim];
        allFx = new double[maxIter + 1][foodSourcesCount];
        bestFoodSources = new double[maxIter + 1][dim];
        bestFx = new double[maxIter + 1];

        for (int i = 0; i < foodSourcesCount; i++) {
            generateRandomFoodSource(i);
        }
    }

    private void generateRandomFoodSource(int i) {
        for (int j = 0; j < dim; j++) {
            foodSources[i][j] = lb[j] + rng.nextDouble() * (ub[j] - lb[j]);
        }
        trials[i] = 0;
        fx[i] = func.getValue(foodSources[i]);
        fitness[i] = calculateFitness(fx[i]);
    }

    private void employedBeePhase() {
        for (int i = 0; i < foodSourcesCount; i++) {
            updateFoodSource(i);
        }
    }

    private void updateFoodSource(int i) {
        int varToChange = rng.nextInt(dim);
        double xNew = updateSelectedVariable(i, varToChange);
        double[] newPos = foodSources[i].clone();
        newPos[varToChange] = xNew;
        greedySelection(i, newPos);
    }

    private double updateSelectedVariable(int i, int varToChange) {
        int partner;
        double x, xp, fi, xNew;

        // Partner bee must be different from current bee
        do {
            partner = rng.nextInt(foodSourcesCount);
        } while (partner == i);

        x = foodSources[i][varToChange]; // current bee's x_i
        xp = foodSources[partner][varToChange]; // partner's x_i
        fi = 2 * rng.nextDouble() - 1.0;
        xNew = x + fi * (x - xp);
        xNew = checkBoundaries(varToChange, xNew);
        return xNew;
    }

    private void greedySelection(int i, double[] newPos) {
        double newFx = func.getValue(newPos);
        double newFitness = calculateFitness(newFx);

        if (newFitness > fitness[i]) {
            foodSources[i] = newPos;
            fx[i] = newFx;
            fitness[i] = newFitness;
            trials[i] = 0;
        } else {
            trials[i]++;
        }
    }

    private void onlookerBeePhase() {
        double fitnessSum = Arrays.stream(fitness).sum();
        double prob;

        for (int i = 0; i < foodSourcesCount; i++) {
            prob = fitness[i] / fitnessSum;

            if (rng.nextDouble() < prob) {
                updateFoodSource(i);
            }
        }
    }

    private void rememberFoodSources(int iter) {
        double bestFitness = Double.MIN_VALUE;

        for (int i = 0; i < foodSourcesCount; i++) {
            allFoodSources[iter][i] = foodSources[i].clone();
            allFx[iter][i] = fx[i];

            if (fitness[i] > bestFitness) {
                bestFitness = fitness[i];
                bestFoodSources[iter] = foodSources[i].clone();
                bestFx[iter] = fx[i];
            }
        }
    }

    private void scoutBeePhase() {
        for (int i = 0; i < foodSourcesCount; i++) {
            if (trials[i] > trialsLimit) {
                generateRandomFoodSource(i);
            }
        }
    }

    private double calculateFitness(double fx) {
        return fx >= 0.0 ? 1 / (1 + fx) : 1 + Math.abs(fx);
    }

    private double checkBoundaries(int varToChange, double xNew) {
        xNew = Math.min(xNew, ub[varToChange]);
        xNew = Math.max(xNew, lb[varToChange]);
        return xNew;
    }

    public double[][][] getAllFoodSources() {
        return allFoodSources;
    }

    public double[][] getAllFx() {
        return allFx;
    }

    public double[][] getBestFoodSources() {
        return bestFoodSources;
    }

    public double[] getBestFx() {
        return bestFx;
    }

    public int getFoodSourcesCount() {
        return foodSourcesCount;
    }

    public int getMaxIter() {
        return maxIter;
    }

    public int getTrialsLimit() {
        return trialsLimit;
    }

    public AbstractTestFunction getFunc() {
        return func;
    }
}
