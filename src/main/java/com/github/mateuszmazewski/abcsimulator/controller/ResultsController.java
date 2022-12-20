package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.abc.ABCResultsIO;
import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import com.github.mateuszmazewski.abcsimulator.utils.ObservableResourceFactory;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.doubleToStringDecimal4;

public class ResultsController {

    private static final int MIN_FOOD_SOURCE_SIZE = 1;
    private static final int MAX_FOOD_SOURCE_SIZE = 30;

    private final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();

    @FXML
    private Label minimumValueLabel;

    @FXML
    private Label minimumLabel;

    @FXML
    private Label foundMinimumLabel;

    @FXML
    private Label foundMinimumValueLabel;

    @FXML
    private Label orderOfMagnitudeOfErrorLabel;

    @FXML
    private Label orderOfMagnitudeOfErrorValueLabel;

    @FXML
    private Label iterNumberLabel;

    @FXML
    private Label iterNumberLabelValue;

    @FXML
    private Button saveResultsButton;

    @FXML
    private Button readResultsButton;

    @FXML
    private Slider foodSourceSizeSlider;

    @FXML
    private Label foodSourceSizeLabel;

    private ABCResults results;

    // --------------------Injected by MainController--------------------
    private MainController mainController;

    // ------------------------------------------------------------------
    private double globalMinValue = 0.0;

    @FXML
    private void initialize() {
        initLanguageBindings();
        initFoodSourceSizeSlider();
    }

    private void initFoodSourceSizeSlider() {
        foodSourceSizeLabel.setVisible(false);
        foodSourceSizeSlider.setVisible(false);
        foodSourceSizeSlider.setMin(MIN_FOOD_SOURCE_SIZE);
        foodSourceSizeSlider.setMax(MAX_FOOD_SOURCE_SIZE);
        foodSourceSizeSlider.setValue(FunctionChart2D.INITIAL_FOOD_SOURCE_SIZE);
        foodSourceSizeSlider.setShowTickMarks(false);
        foodSourceSizeSlider.setShowTickLabels(false);
        foodSourceSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainController.getCenterChart().setFoodSourceSize(foodSourceSizeSlider.getValue());
            try {
                mainController.getCenterChart().redrawFoodSources();
            } catch (IllegalStateException ignored) {
            }
        });
    }

    private void initLanguageBindings() {
        minimumLabel.textProperty().bind(messagesFactory.getStringBinding("results.minimum"));
        foundMinimumLabel.textProperty().bind(messagesFactory.getStringBinding("results.foundMinimum"));
        orderOfMagnitudeOfErrorLabel.textProperty().bind(messagesFactory.getStringBinding("results.orderOfMagnitudeOfError"));
        iterNumberLabel.textProperty().bind(messagesFactory.getStringBinding("results.iterNumber"));
        saveResultsButton.textProperty().bind(messagesFactory.getStringBinding("results.saveButton"));
        readResultsButton.textProperty().bind(messagesFactory.getStringBinding("results.readResultsButton"));
        foodSourceSizeLabel.textProperty().bind(messagesFactory.getStringBinding("results.foodSourceSizeLabel"));
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
        this.globalMinValue = globalMinValue;
        String x = doubleToStringDecimal4(globalMinPos[0]);
        String y = doubleToStringDecimal4(globalMinPos[1]);
        String fx = doubleToStringDecimal4(globalMinValue);
        minimumValueLabel.setText("f(" + x + ", " + y + ") = " + fx);
    }

    public void showResults(int iterNumber) {
        setResultsVisible(true);
        double[] bestFoodSource = results.getBestFoodSources()[iterNumber];
        double fx = results.getBestFx()[iterNumber];

        String xString = doubleToStringDecimal4(bestFoodSource[0]);
        String yString = doubleToStringDecimal4(bestFoodSource[1]);
        String fxString = doubleToStringDecimal4(fx);

        double error = Math.abs(globalMinValue - fx);
        String orderOfMagnitudeOfErrorString = "";

        if (error > 0.0) {
            orderOfMagnitudeOfErrorValueLabel.textProperty().unbind();
            int orderOfMagnitudeOfError = (int) Math.floor(Math.log10(error));
            orderOfMagnitudeOfErrorString = "10^"
                    + (orderOfMagnitudeOfError >= 0 ? orderOfMagnitudeOfError : "(" + orderOfMagnitudeOfError + ")");
        } else {
            orderOfMagnitudeOfErrorValueLabel.textProperty().bind(messagesFactory.getStringBinding("results.maxPrecision"));
        }

        foundMinimumValueLabel.setText("f(" + xString + ", " + yString + ") = " + fxString);
        if (error > 0.0) {
            orderOfMagnitudeOfErrorValueLabel.setText(orderOfMagnitudeOfErrorString);
        }
        iterNumberLabelValue.setText(String.valueOf(iterNumber));
    }

    public void setResultsVisible(boolean visible) {
        foundMinimumLabel.setVisible(visible);
        foundMinimumValueLabel.setVisible(visible);
        orderOfMagnitudeOfErrorLabel.setVisible(visible);
        orderOfMagnitudeOfErrorValueLabel.setVisible(visible);
        iterNumberLabel.setVisible(visible);
        iterNumberLabelValue.setVisible(visible);
        saveResultsButton.setVisible(visible);
        foodSourceSizeSlider.setVisible(visible);
        foodSourceSizeLabel.setVisible(visible);
    }

    public void setResults(ABCResults results) {
        this.results = results;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
