package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;

public interface IControllerMediator {
    void registerMainController(MainController controller);

    void registerParametersController(ParametersController controller);

    void registerResultsController(ResultsController controller);

    FunctionChart2D mainControllerGetFunctionChart();

    void resultsControllerShowFuncBest(double[] globalMinPos, double globalMinValue);

    Slider mainControllerGetIterSlider();

    void resultsControllerSetResultsVisible(boolean visible);

    void resultsControllerSetResults(ABCResults results);

    void resultsControllerShowResults(int iterNumber);

    boolean isMainControllerRegistered();

    Stage mainControllerGetStage();

    void parameterControllerInitResults(ABCResults results) throws IOException;
}
