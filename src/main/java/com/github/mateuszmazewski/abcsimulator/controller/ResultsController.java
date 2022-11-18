package com.github.mateuszmazewski.abcsimulator.controller;

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

    @FXML
    private void initialize() {
    }

    @FXML
    void onActionSaveResultsButton() {

    }

    public void showFuncBest(double[] globalMinPos, double globalMinValue) {
        String x = doubleToString(globalMinPos[0]);
        String y = doubleToString(globalMinPos[1]);
        String fx = doubleToString(globalMinValue);
        minimumValueLabel.setText("f(" + x + ", " + y + ") = " + fx);
    }

    public void showResults(int iterNumber, double[] bestFoodSource, double bestFx) {
        setResultsVisible(true);
        String x = doubleToString(bestFoodSource[0]);
        String y = doubleToString(bestFoodSource[1]);
        String fx = doubleToString(bestFx);

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
}
