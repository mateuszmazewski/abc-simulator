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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ParametersController {

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
    private MainController mainController;
    private final BooleanProperty wrongParameters = new SimpleBooleanProperty(false);

    @FXML
    private void initialize() {
        ResourceBundle messagesBundle = FxmlUtils.getResourceBundle();
        AbstractTestFunction beale = new BealeFunction();
        beale.setName(messagesBundle.getString("bealeFunction.name"));
        funcList.add(beale);

        funcComboBox.setItems(funcList);
        func.bind(funcComboBox.valueProperty());

        startButton.disableProperty().bind(wrongParameters);

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

            FunctionChart2D functionChart2D = new FunctionChart2D(func.getValue());
            mainController.setCenterChart(functionChart2D);
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
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}