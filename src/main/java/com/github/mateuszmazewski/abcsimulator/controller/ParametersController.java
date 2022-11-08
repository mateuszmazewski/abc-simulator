package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ArtificialBeeColony;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.BealeFunction;
import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ParametersController {

    private static final int SLIDER_MAJOR_TICKS_COUNT = 20;
    private static final int BY_ONE_TICKS_LIMIT = 30;

    @FXML
    private ComboBox<AbstractTestFunction> funcComboBox;

    @FXML
    private TextField xRangeFromTextField;

    @FXML
    private TextField xRangeToTextField;

    @FXML
    private TextField yRangeFromTextField;

    @FXML
    private TextField yRangeToTextField;

    @FXML
    private TextField swarmSizeTextField;

    @FXML
    private TextField maxIterTextField;

    @FXML
    private TextField trialsLimitTextField;

    @FXML
    private Button startButton;

    private final ObservableList<AbstractTestFunction> funcList = FXCollections.observableArrayList();
    private final ObjectProperty<AbstractTestFunction> func = new SimpleObjectProperty<>();
    private final String textFieldDefaultStyle = new TextField().getStyle();
    private final String textFieldErrorStyle = "-fx-background-color: lightcoral;";
    private final BooleanProperty wrongParameters = new SimpleBooleanProperty(false);

    // --------------------Injected by MainController--------------------
    private MainController mainController;
    private Slider iterSlider;

    //-------------------------------------------------------------------
    private ChangeListener<Number> sliderValueChangeListener;

    @FXML
    private void initialize() {
        ResourceBundle messagesBundle = FxmlUtils.getResourceBundle();
        AbstractTestFunction beale = new BealeFunction();
        beale.setName(messagesBundle.getString("bealeFunction.name"));
        funcList.add(beale);

        funcComboBox.setItems(funcList);
        func.bind(funcComboBox.valueProperty());

        startButton.disableProperty().bind(wrongParameters);

        addValueChangeListenerToTextFields();
    }

    private void addValueChangeListenerToTextFields() {
        addRangeValueChangeListener(xRangeFromTextField);
        addRangeValueChangeListener(xRangeToTextField);
        addRangeValueChangeListener(yRangeFromTextField);
        addRangeValueChangeListener(yRangeToTextField);
        addIntValueChangeListener(swarmSizeTextField);
        addIntValueChangeListener(maxIterTextField);
        addIntValueChangeListener(trialsLimitTextField);
    }

    private void addIntValueChangeListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            int number;
            try {
                number = Integer.parseInt(newValue);
                textField.setStyle(textFieldDefaultStyle);
                validateTextFields();
            } catch (NumberFormatException e) {
                textField.setStyle(textFieldErrorStyle);
                wrongParameters.setValue(true);
                return;
            }


            if (textField == swarmSizeTextField) {
                textFieldValueInRange(number, ArtificialBeeColony.MIN_SWARM_SIZE, ArtificialBeeColony.MAX_SWARM_SIZE, textField);
            } else if (textField == maxIterTextField) {
                textFieldValueInRange(number, ArtificialBeeColony.MAX_ITER_LOWER_LIMIT, ArtificialBeeColony.MAX_ITER_UPPER_LIMIT, textField);
            } else if (textField == trialsLimitTextField) {
                textFieldValueInRange(number, ArtificialBeeColony.MIN_TRIALS_LIMIT, ArtificialBeeColony.MAX_TRIALS_LIMIT, textField);
            }
        });
    }

    private void addRangeValueChangeListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (textField == xRangeFromTextField || textField == xRangeToTextField) {
                handleXRange(textField);
            } else if (textField == yRangeFromTextField || textField == yRangeToTextField) {
                handleYRange(textField);
            }

            FunctionChart2D chart = mainController.getCenterChart();
            chart.setTestFunction(func.getValue());
            chart.drawAll();
        });
    }

    private void handleXRange(TextField currentTextField) {
        double lower, upper;
        boolean lowerInRange, upperInRange;

        try {
            lower = Double.parseDouble(xRangeFromTextField.getText());
            upper = Double.parseDouble(xRangeToTextField.getText());
        } catch (NumberFormatException e) {
            currentTextField.setStyle(textFieldErrorStyle);
            wrongParameters.setValue(true);
            return;
        }

        lowerInRange = textFieldValueInRange(lower, AbstractTestFunction.MIN_X, upper, xRangeFromTextField);
        upperInRange = textFieldValueInRange(upper, lower, AbstractTestFunction.MAX_X, xRangeToTextField);

        if (lowerInRange && upperInRange) {
            func.getValue().getLowerBoundaries()[0] = lower;
            func.getValue().getUpperBoundaries()[0] = upper;
            xRangeFromTextField.setStyle(textFieldDefaultStyle);
            xRangeToTextField.setStyle(textFieldDefaultStyle);
        }
    }

    private void handleYRange(TextField currentTextField) {
        double lower, upper;
        boolean lowerInRange, upperInRange;

        try {
            lower = Double.parseDouble(yRangeFromTextField.getText());
            upper = Double.parseDouble(yRangeToTextField.getText());
        } catch (NumberFormatException e) {
            currentTextField.setStyle(textFieldErrorStyle);
            wrongParameters.setValue(true);
            return;
        }

        lowerInRange = textFieldValueInRange(lower, AbstractTestFunction.MIN_Y, upper, yRangeFromTextField);
        upperInRange = textFieldValueInRange(upper, lower, AbstractTestFunction.MAX_Y, yRangeToTextField);

        if (lowerInRange && upperInRange) {
            func.getValue().getLowerBoundaries()[1] = lower;
            func.getValue().getUpperBoundaries()[1] = upper;
            yRangeFromTextField.setStyle(textFieldDefaultStyle);
            yRangeToTextField.setStyle(textFieldDefaultStyle);
        }
    }

    private boolean textFieldValueInRange(double d, double minValue, double maxValue, TextField textField) {
        if (d < minValue || d > maxValue) {
            textField.setStyle(textFieldErrorStyle);
            wrongParameters.setValue(true);
            return false;
        } else {
            textField.setStyle(textFieldDefaultStyle);
            validateTextFields();
            return true;
        }
    }

    private void validateTextFields() {
        boolean invalidTextFields = Stream.of(xRangeFromTextField, xRangeToTextField, yRangeFromTextField,
                        yRangeToTextField, swarmSizeTextField, maxIterTextField, trialsLimitTextField)
                .anyMatch(textField -> textField.getStyle().equals(textFieldErrorStyle));
        wrongParameters.setValue(invalidTextFields);
    }

    @FXML
    private void onActionFuncComboBox() {
        xRangeFromTextField.textProperty().setValue(String.valueOf(func.getValue().getLowerBoundaries()[0]));
        xRangeToTextField.textProperty().setValue(String.valueOf(func.getValue().getUpperBoundaries()[0]));
        yRangeFromTextField.textProperty().setValue(String.valueOf(func.getValue().getLowerBoundaries()[1]));
        yRangeToTextField.textProperty().setValue(String.valueOf(func.getValue().getUpperBoundaries()[1]));
    }

    @FXML
    private void onActionStartButton() {
        int swarmSize;
        int maxIter;
        int trialsLimit;

        try {
            swarmSize = Integer.parseInt(swarmSizeTextField.getText());
            maxIter = Integer.parseInt(maxIterTextField.getText());
            trialsLimit = Integer.parseInt(trialsLimitTextField.getText());
        } catch (NumberFormatException e) {
            return;
        }

        ArtificialBeeColony abc = new ArtificialBeeColony(swarmSize, maxIter, func.getValue(), trialsLimit);
        abc.run();
        double[][][] allFoodSources = abc.getAllFoodSources();

        initIterSlider(maxIter, allFoodSources);
    }

    private void initIterSlider(int maxIter, double[][][] allFoodSources) {
        iterSlider.setDisable(false);
        iterSlider.setShowTickMarks(true);
        iterSlider.setShowTickLabels(true);
        iterSlider.setMin(0);
        iterSlider.setMax(maxIter);
        iterSlider.setBlockIncrement(1);

        if (maxIter < BY_ONE_TICKS_LIMIT) {
            iterSlider.setMajorTickUnit(1);
            iterSlider.setMinorTickCount(0);
        } else {
            iterSlider.setMajorTickUnit((int) ((double) maxIter / SLIDER_MAJOR_TICKS_COUNT));
            iterSlider.setMinorTickCount((int) iterSlider.getMajorTickUnit() - 1);
        }

        if (sliderValueChangeListener != null) {
            iterSlider.valueProperty().removeListener(sliderValueChangeListener);
        }
        sliderValueChangeListener = (observable, oldValue, newValue) ->
                mainController.getCenterChart().drawBees(allFoodSources[newValue.intValue()]);

        iterSlider.valueProperty().addListener(sliderValueChangeListener);
        iterSlider.setValue(maxIter);
        mainController.getCenterChart().drawBees(allFoodSources[maxIter]);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setIterSlider(Slider iterSlider) {
        this.iterSlider = iterSlider;
    }
}
