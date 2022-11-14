package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.RastriginFunction;
import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

import java.util.Optional;

public class MainController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Slider iterSlider;

    @FXML
    private ParametersController parametersController;

    @FXML
    private ResultsController resultsController;

    public FunctionChart2D getCenterChart() {
        return (FunctionChart2D) borderPane.getCenter();
    }

    @FXML
    private void initialize() {
        FunctionChart2D functionChart2D = new FunctionChart2D(new RastriginFunction());
        BorderPane.setMargin(functionChart2D, new Insets(10, 10, 10, 10));
        borderPane.setCenter(functionChart2D);
        parametersController.setMainController(this);

        iterSlider.setDisable(true);
        iterSlider.setShowTickLabels(false);
        iterSlider.setShowTickMarks(false);
        parametersController.setIterSlider(iterSlider);

        AbstractTestFunction func = parametersController.getFunc();
        resultsController.showFuncBest(func.getGlobalMinPos(), func.getGlobalMinValue());
        resultsController.setResultsVisible(false);
    }

    @FXML
    private void closeApp() {
        Optional<ButtonType> buttonType = DialogUtils.showConfirmDialog(
                "exitDialog.title",
                "exitDialog.header",
                null);
        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void showAboutDialog() {
        DialogUtils.showAboutDialog();
    }

    public ResultsController getResultsController() {
        return resultsController;
    }
}
