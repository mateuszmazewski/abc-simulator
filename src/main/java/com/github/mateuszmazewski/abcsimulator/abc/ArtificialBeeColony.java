package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;

import java.util.Arrays;
import java.util.Random;

public class ArtificialBeeColony {

    public static final int MIN_SWARM_SIZE = 2;
    public static final int MAX_SWARM_SIZE = 1000000;
    public static final int MAX_ITER_LOWER_LIMIT = 1;
    public static final int MAX_ITER_UPPER_LIMIT = 1000000;
    public static final int MIN_TRIALS_LIMIT = 1;
    public static final int MAX_TRIALS_LIMIT = 1000000;

    // -------------------------INPUT PARAMETERS-------------------------
    private final int swarmSize;
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

    public ArtificialBeeColony(int swarmSize, int maxIter, AbstractTestFunction func, int trialsLimit) {
        validateArgs(swarmSize, maxIter, func, trialsLimit);

        this.swarmSize = swarmSize;
        this.maxIter = maxIter;
        this.func = func;
        this.trialsLimit = trialsLimit;
    }

    private void validateArgs(int swarmSize, int maxIter, AbstractTestFunction func, int limit) {
        if (swarmSize < MIN_SWARM_SIZE) {
            throw new IllegalArgumentException("swarm must contain at least " + MIN_SWARM_SIZE + " bees");
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

        for (int i = 0; i < maxIter; i++) {
            employedBeePhase();
            onlookerBeePhase();
            rememberBestFoodSource(i);
            scoutBeePhase();
        }
    }

    private void init() {
        dim = func.getDim();
        lb = func.getLowerBoundaries();
        ub = func.getUpperBoundaries();

        foodSources = new double[swarmSize][dim];
        fx = new double[swarmSize];
        fitness = new double[swarmSize];
        trials = new int[swarmSize];
        Arrays.fill(trials, 0);

        allFoodSources = new double[maxIter][swarmSize][dim];
        allFx = new double[maxIter][swarmSize];
        bestFoodSources = new double[maxIter][dim];
        bestFx = new double[maxIter];

        for (int i = 0; i < swarmSize; i++) {
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
        for (int i = 0; i < swarmSize; i++) {
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
            partner = rng.nextInt(swarmSize);
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
        } else {
            trials[i]++;
        }
    }

    private void onlookerBeePhase() {
        double fitnessSum = Arrays.stream(fitness).sum();
        double prob;

        for (int i = 0; i < swarmSize; i++) {
            prob = fitness[i] / fitnessSum;

            if (rng.nextDouble() < prob) {
                updateFoodSource(i);
            }
        }
    }

    private void rememberBestFoodSource(int iter) {
        double bestFitness = Double.MIN_VALUE;

        for (int i = 0; i < swarmSize; i++) {
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
        for (int i = 0; i < swarmSize; i++) {
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
}
