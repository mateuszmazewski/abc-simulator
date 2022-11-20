package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.abc.ABCResultsIO;
import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.doubleToString;

public class ResultsController {

    @FXML
    private Label minimumValueLabel;

    @FXML
    private Label foundMinimumLabel;

    @FXML
    private Label foundMinimumValueLabel;

    @FXML
    private Label iterNumberLabel;

    @FXML
    private Label iterNumberLabelValue;

    @FXML
    private Button saveResultsButton;
    private ABCResults results;

    // --------------------Injected by MainController--------------------
    private MainController mainController;

    @FXML
    private void initialize() {
    }

    @FXML
    private void onActionSaveResultsButton() {
        ABCResultsIO resultsIO = new ABCResultsIO(mainController.getStage());
        try {
            resultsIO.saveResults(results);
        } catch (Exception e) {
            DialogUtils.errorDialog(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @FXML
    private void onActionReadButton() {
        ABCResultsIO resultsIO = new ABCResultsIO(mainController.getStage());
        try {
            results = resultsIO.readResults();
            if (results == null) {
                return;
            }

            mainController.getParametersController().initResults(results);
        } catch (Exception e) {
            DialogUtils.errorDialog(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public void showFuncBest(double[] globalMinPos, double globalMinValue) {
        String x = doubleToString(globalMinPos[0]);
        String y = doubleToString(globalMinPos[1]);
        String fx = doubleToString(globalMinValue);
        minimumValueLabel.setText("f(" + x + ", " + y + ") = " + fx);
    }

    public void showResults(int iterNumber) {
        setResultsVisible(true);
        double[] bestFoodSource = results.getBestFoodSources()[iterNumber];
        String x = doubleToString(bestFoodSource[0]);
        String y = doubleToString(bestFoodSource[1]);
        String fx = doubleToString(results.getBestFx()[iterNumber]);

        foundMinimumValueLabel.setText("f(" + x + ", " + y + ") = " + fx);
        iterNumberLabelValue.setText(String.valueOf(iterNumber));
    }

    public void setResultsVisible(boolean visible) {
        foundMinimumLabel.setVisible(visible);
        foundMinimumValueLabel.setVisible(visible);
        iterNumberLabel.setVisible(visible);
        iterNumberLabelValue.setVisible(visible);
        saveResultsButton.setVisible(visible);
    }

    public void setResults(ABCResults results) {
        this.results = results;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
