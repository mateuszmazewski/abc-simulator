package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.abc.ABCResultsIO;
import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.doubleToString;


public class ResultsController {

    private Stage stage;

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

    @FXML
    private void initialize() {
    }

    @FXML
    private void onActionSaveResultsButton() {
        ABCResultsIO resultsIO = new ABCResultsIO(stage);
        try {
            resultsIO.saveResults(results);
        } catch (IOException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }

    @FXML
    private void onActionReadButton() {
        ABCResultsIO resultsIO = new ABCResultsIO(stage);
        try {
            results = resultsIO.readResults();
        } catch (IOException | NumberFormatException e) {
            DialogUtils.errorDialog(e.getMessage());
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setResults(ABCResults results) {
        this.results = results;
    }

}
