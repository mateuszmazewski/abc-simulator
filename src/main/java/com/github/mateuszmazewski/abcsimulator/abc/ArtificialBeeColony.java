package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;

import java.util.Arrays;
import java.util.Random;

public class ArtificialBeeColony {

    public static final int MIN_FOOD_SOURCES_COUNT = 2;
    public static final int MAX_FOOD_SOURCES_COUNT = 1000;
    public static final int MAX_ITER_LOWER_LIMIT = 1;
    public static final int MAX_ITER_UPPER_LIMIT = 10000;
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
    private double[] bestFoodSourceSoFar;
    private double bestFxSoFar;
    private int bestIter;
    private double bestFitnessSoFar = -Double.MAX_VALUE;
    private final Random rng = new Random();

    public ArtificialBeeColony(int foodSourcesCount, int maxIter, AbstractTestFunction func, int trialsLimit) {
        this.foodSourcesCount = foodSourcesCount;
        this.maxIter = maxIter;
        this.func = func;
        this.trialsLimit = trialsLimit;

        validateArgs();
    }

    private void validateArgs() {
        if (foodSourcesCount < MIN_FOOD_SOURCES_COUNT || foodSourcesCount > MAX_FOOD_SOURCES_COUNT) {
            throw new IllegalArgumentException("number of food sources must be in range <" + MIN_FOOD_SOURCES_COUNT + ", " + MAX_FOOD_SOURCES_COUNT + ">");
        }
        if (maxIter < MAX_ITER_LOWER_LIMIT || maxIter > MAX_ITER_UPPER_LIMIT) {
            throw new IllegalArgumentException("number of iterations must be in range <" + MAX_ITER_LOWER_LIMIT + ", " + MAX_ITER_UPPER_LIMIT + ">");
        }
        if (func == null) {
            throw new IllegalArgumentException("function cannot be null");
        }
        if (trialsLimit < MIN_TRIALS_LIMIT || trialsLimit > MAX_TRIALS_LIMIT) {
            throw new IllegalArgumentException("limit of trials to improve the solution must be in range <" + MIN_TRIALS_LIMIT + ", " + MAX_TRIALS_LIMIT + ">");
        }
    }

    public void run() {
        init();
        rememberFoodSources(0); // Remember initial random solutions

        for (int i = 1; i <= maxIter; i++) {
            employedBeePhase();
            onlookerBeePhase();
            scoutBeePhase();
            rememberFoodSources(i);
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

        bestFoodSourceSoFar = new double[2];
        bestFxSoFar = Double.MAX_VALUE;
        bestFitnessSoFar = -Double.MAX_VALUE;

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

        // Partner food source must be different from the current food source
        do {
            partner = rng.nextInt(foodSourcesCount);
        } while (partner == i);

        x = foodSources[i][varToChange]; // current food source's x_i
        xp = foodSources[partner][varToChange]; // partner food source's x_i
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
        double[] probs = getProbabilities();
        double rand, cumulativeProb;
        int foodSource;

        for (int i = 0; i < foodSourcesCount; i++) {
            rand = rng.nextDouble();
            foodSource = 0;
            cumulativeProb = probs[foodSource];

            // roulette
            while (rand > cumulativeProb) {
                foodSource++;
                cumulativeProb += probs[foodSource];
            }

            foodSource = Math.min(foodSource, foodSourcesCount - 1);
            updateFoodSource(foodSource);
        }
    }

    private double[] getProbabilities() {
        double fitnessSum = Arrays.stream(fitness).sum();
        double[] probs = new double[foodSourcesCount];

        for (int i = 0; i < foodSourcesCount; i++) {
            probs[i] = fitness[i] / fitnessSum;
        }

        return probs;
    }

    private void rememberFoodSources(int iter) {
        int bestFoodSourceIdx = -1;

        for (int i = 0; i < foodSourcesCount; i++) {
            allFoodSources[iter][i] = foodSources[i].clone();
            allFx[iter][i] = fx[i];

            if (fitness[i] > bestFitnessSoFar) {
                bestFitnessSoFar = fitness[i];
                bestFoodSourceIdx = i;
            }
        }

        if (bestFoodSourceIdx >= 0) {
            bestFoodSourceSoFar = foodSources[bestFoodSourceIdx].clone();
            bestFxSoFar = fx[bestFoodSourceIdx];
            bestIter = iter;
        }

        bestFoodSources[iter] = bestFoodSourceSoFar.clone();
        bestFx[iter] = bestFxSoFar;
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

    public int getBestIter() {
        return bestIter;
    }

    public AbstractTestFunction getFunc() {
        return func;
    }
}
