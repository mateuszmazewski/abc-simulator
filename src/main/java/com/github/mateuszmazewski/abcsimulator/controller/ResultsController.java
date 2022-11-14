package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.utils.MathUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.doubleToString;


public class ResultsController {

    @FXML
    private Label foundMinimumLabel;

    @FXML
    private Label foundMinimumLabelValue;

    @FXML
    private Label minimumPosLabel;

    @FXML
    private Label iterNumberLabel;

    @FXML
    private Label iterNumberLabelValue;

    @FXML
    private Label minimumLabel;

    @FXML
    private Label foundMinimumLabelXY;

    @FXML
    private Button saveResultsButton;

    @FXML
    private void initialize() {
    }

    @FXML
    void onActionSaveResultsButton() {

    }

    public void showFuncBest(double[] globalMinPos, double globalMinValue) {
        minimumPosLabel.setText("x = " + doubleToString(globalMinPos[0])
                + ", y = " + doubleToString(globalMinPos[1]));
        minimumLabel.setText("f(x, y) = " + doubleToString(globalMinValue));
    }

    public void showResults(int iterNumber, double[] bestFoodSource, double bestFx) {
        setResultsVisible(true);

        foundMinimumLabelValue.setText("x = " + doubleToString(bestFoodSource[0])
                + ", y = " + doubleToString(bestFoodSource[1]));
        foundMinimumLabelXY.setText("f(x, y) = " + doubleToString(bestFx));
        iterNumberLabelValue.setText(String.valueOf(iterNumber));
    }

    public void setResultsVisible(boolean visible) {
        foundMinimumLabel.setVisible(visible);
        foundMinimumLabelXY.setVisible(visible);
        foundMinimumLabelValue.setVisible(visible);
        iterNumberLabel.setVisible(visible);
        iterNumberLabelValue.setVisible(visible);
        saveResultsButton.setVisible(visible);
    }
}
