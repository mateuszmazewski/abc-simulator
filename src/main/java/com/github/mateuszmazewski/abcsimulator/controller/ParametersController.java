package com.github.mateuszmazewski.abcsimulator.controller;

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

        addValueChangeListener(xRangeFromTextField);
        addValueChangeListener(xRangeToTextField);
        addValueChangeListener(yRangeFromTextField);
        addValueChangeListener(yRangeToTextField);
    }

    private void addValueChangeListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            double d;
            try {
                d = Double.parseDouble(newValue);
                textField.setStyle(textFieldDefaultStyle);
                wrongParameters.setValue(false);
            } catch (NumberFormatException e) {
                textField.setStyle("-fx-background-color: lightcoral;");
                wrongParameters.setValue(true);
                return;
            }

            if (textField == xRangeFromTextField) {
                func.getValue().getLowerBoundaries()[0] = d;
            } else if (textField == xRangeToTextField) {
                func.getValue().getUpperBoundaries()[0] = d;
            } else if (textField == yRangeFromTextField) {
                func.getValue().getLowerBoundaries()[1] = d;
            } else if (textField == yRangeToTextField) {
                func.getValue().getUpperBoundaries()[1] = d;
            }

            FunctionChart2D functionChart2D = new FunctionChart2D(func.getValue());
            mainController.setCenterChart(functionChart2D);
        });
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
