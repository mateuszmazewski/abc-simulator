package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerMediator implements IControllerMediator {

    private static ControllerMediator instance;
    private MainController mainController;
    private ParametersController parametersController;
    private ResultsController resultsController;

    private ControllerMediator() {
    }

    public static ControllerMediator getInstance() {
        if (instance == null) {
            instance = new ControllerMediator();
        }
        return instance;
    }

    @Override
    public void registerMainController(MainController controller) {
        mainController = controller;
    }

    @Override
    public void registerParametersController(ParametersController controller) {
        parametersController = controller;
    }

    @Override
    public void registerResultsController(ResultsController controller) {
        resultsController = controller;
    }

    @Override
    public FunctionChart2D mainControllerGetFunctionChart() {
        return mainController.getCenterChart();
    }

    @Override
    public void resultsControllerShowFuncBest(double[] globalMinPos, double globalMinValue) {
        resultsController.showFuncBest(globalMinPos, globalMinValue);
    }

    @Override
    public Slider mainControllerGetIterSlider() {
        return mainController.getIterSlider();
    }

    @Override
    public void resultsControllerSetResultsVisible(boolean visible) {
        resultsController.setResultsVisible(visible);
    }

    @Override
    public void resultsControllerSetResults(ABCResults results) {
        resultsController.setResults(results);
    }

    @Override
    public void resultsControllerShowResults(int iterNumber) {
        resultsController.showResults(iterNumber);
    }

    @Override
    public boolean isMainControllerRegistered() {
        return mainController != null;
    }

    @Override
    public Stage mainControllerGetStage() {
        return mainController.getStage();
    }

    @Override
    public void parameterControllerInitResults(ABCResults results) throws IOException {
        parametersController.initResults(results);
    }
}
