package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.abc.ArtificialBeeColony;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.RastriginFunction;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.TestFunctionUtils;
import com.github.mateuszmazewski.abcsimulator.utils.ObservableResourceFactory;
import com.github.mateuszmazewski.abcsimulator.utils.TextFieldUtils;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class ParametersController {

    private static final int SLIDER_MAJOR_TICKS_COUNT = 20;
    private static final int SLIDER_BY_ONE_TICKS_LIMIT = 30;
    public static final int INITIAL_FOOD_SOURCES_COUNT = 50;
    public static final int INITIAL_MAX_ITER = 100;
    public static final int INITIAL_TRIALS_LIMIT = 50;

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
    private TextField foodSourcesCountTextField;

    @FXML
    private TextField maxIterTextField;

    @FXML
    private TextField trialsLimitTextField;

    @FXML
    private Button startButton;

    // ---------------------ONLY TO CHANGE THE LANGUAGE----------------------
    @FXML
    private Label funcLabel;

    @FXML
    private Label xRangeLabel;

    @FXML
    private Label yRangeLabel;

    @FXML
    private Label xToLabel;

    @FXML
    private Label yToLabel;

    @FXML
    private Label foodSourcesCountLabel;

    @FXML
    private Label maxIterLabel;

    @FXML
    private Label trialsLimitLabel;

    @FXML
    private Tooltip functionLabelTooltip;

    @FXML
    private Tooltip foodSourcesCountLabelTooltip;

    @FXML
    private Tooltip foodSourcesCountTextFieldTooltip;

    @FXML
    private Tooltip maxIterTextFieldTooltip;

    @FXML
    private Tooltip trialsLimitLabelTooltip;

    @FXML
    private Tooltip trialsLimitTextFieldTooltip;

    @FXML
    private Tooltip xRangeFromTextFieldTooltip;

    @FXML
    private Tooltip xRangeToTextFieldTooltip;

    @FXML
    private Tooltip yRangeFromTextFieldTooltip;

    @FXML
    private Tooltip yRangeToTextFieldTooltip;

    // -----------------------------------------------------------------
    private final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();
    private final IControllerMediator controllerMediator = ControllerMediator.getInstance();
    private final ObservableMap<String, AbstractTestFunction> testFunctionObservableMap = TestFunctionUtils.createTestFunctionObservableMap();
    private final ObjectProperty<AbstractTestFunction> func = new SimpleObjectProperty<>();
    private final BooleanProperty wrongParameters = new SimpleBooleanProperty(false);

    // ------------------------------------------------------------------
    private ChangeListener<Number> sliderValueChangeListener;
    private boolean rangeChangeListenersActive = true;
    private boolean funcComboBoxChangeListenerActive = true;

    @FXML
    private void initialize() {
        initLanguageBindings();

        funcComboBox.setItems(FXCollections.observableArrayList(testFunctionObservableMap.values()));

        messagesFactory.resourcesProperty().addListener(e -> {
            // Set items again with changed names
            funcComboBoxChangeListenerActive = false;
            AbstractTestFunction selected = funcComboBox.getSelectionModel().getSelectedItem();
            funcComboBox.setItems(null);
            funcComboBox.setItems(FXCollections.observableArrayList(testFunctionObservableMap.values()));
            funcComboBox.getSelectionModel().select(selected);
            funcComboBoxChangeListenerActive = true;
        });

        func.bind(funcComboBox.valueProperty());
        funcComboBox.getSelectionModel().select(testFunctionObservableMap.get(RastriginFunction.class.getSimpleName()));
        onActionFuncComboBox();

        startButton.disableProperty().bind(wrongParameters);

        addValueChangeListenerToTextFields();

        foodSourcesCountTextField.setText(String.valueOf(INITIAL_FOOD_SOURCES_COUNT));
        maxIterTextField.setText(String.valueOf(INITIAL_MAX_ITER));
        trialsLimitTextField.setText(String.valueOf(INITIAL_TRIALS_LIMIT));
    }

    private void initLanguageBindings() {
        funcLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.function"));
        xRangeLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.xRange"));
        yRangeLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.yRange"));
        xToLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.rangeTo"));
        yToLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.rangeTo"));
        foodSourcesCountLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.foodSourcesCount"));
        maxIterLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.maxIter"));
        trialsLimitLabel.textProperty().bind(messagesFactory.getStringBinding("parameters.trialsLimit"));
        startButton.textProperty().bind(messagesFactory.getStringBinding("parameters.startButton"));

        functionLabelTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.funcLabel"));
        foodSourcesCountLabelTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.foodSourcesCountLabel"));
        foodSourcesCountTextFieldTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.foodSourcesCountTextField"));
        maxIterTextFieldTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.maxIterTextField"));
        trialsLimitLabelTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.trialsLimitLabel"));
        trialsLimitTextFieldTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.trialsLimitTextField"));
        xRangeFromTextFieldTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.xRangeFromTextField"));
        xRangeToTextFieldTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.xRangeToTextField"));
        yRangeFromTextFieldTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.yRangeFromTextField"));
        yRangeToTextFieldTooltip.textProperty().bind(messagesFactory.getStringBinding("tooltip.yRangeToTextField"));
    }

    private void addValueChangeListenerToTextFields() {
        addRangeValueChangeListener(xRangeFromTextField);
        addRangeValueChangeListener(xRangeToTextField);
        addRangeValueChangeListener(yRangeFromTextField);
        addRangeValueChangeListener(yRangeToTextField);
        addIntValueChangeListener(foodSourcesCountTextField);
        addIntValueChangeListener(maxIterTextField);
        addIntValueChangeListener(trialsLimitTextField);
    }

    private void addIntValueChangeListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            int number;
            try {
                number = Integer.parseInt(newValue);
                TextFieldUtils.setValid(textField);
                validateTextFields();
            } catch (NumberFormatException e) {
                TextFieldUtils.setInvalid(textField);
                wrongParameters.setValue(true);
                return;
            }

            if (textField == foodSourcesCountTextField) {
                textFieldValueInRange(number, ArtificialBeeColony.MIN_FOOD_SOURCES_COUNT, ArtificialBeeColony.MAX_FOOD_SOURCES_COUNT, textField);
            } else if (textField == maxIterTextField) {
                textFieldValueInRange(number, ArtificialBeeColony.MAX_ITER_LOWER_LIMIT, ArtificialBeeColony.MAX_ITER_UPPER_LIMIT, textField);
            } else if (textField == trialsLimitTextField) {
                textFieldValueInRange(number, ArtificialBeeColony.MIN_TRIALS_LIMIT, ArtificialBeeColony.MAX_TRIALS_LIMIT, textField);
            }
        });
    }

    private void addRangeValueChangeListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (rangeChangeListenersActive) {
                if (textField == xRangeFromTextField || textField == xRangeToTextField) {
                    handleXRange(textField);
                } else if (textField == yRangeFromTextField || textField == yRangeToTextField) {
                    handleYRange(textField);
                }

                FunctionChart2D chart = controllerMediator.mainControllerGetFunctionChart();
                chart.setTestFunction(func.getValue());
                chart.drawAll();
                controllerMediator.resultsControllerShowFuncBest(func.getValue().getMinValuePos(), func.getValue().getMinValue());
            }
        });
    }

    private void handleXRange(TextField currentTextField) {
        double lower, upper;
        boolean lowerInRange, upperInRange;

        try {
            lower = Double.parseDouble(xRangeFromTextField.getText());
            upper = Double.parseDouble(xRangeToTextField.getText());
        } catch (NumberFormatException e) {
            TextFieldUtils.setInvalid(currentTextField);
            wrongParameters.setValue(true);
            return;
        }

        lowerInRange = textFieldValueInRange(lower, AbstractTestFunction.MIN_X, upper, xRangeFromTextField);
        upperInRange = textFieldValueInRange(upper, lower, AbstractTestFunction.MAX_X, xRangeToTextField);

        if (lowerInRange && upperInRange) {
            func.getValue().getLowerBoundaries()[0] = lower;
            func.getValue().getUpperBoundaries()[0] = upper;
            TextFieldUtils.setValid(xRangeFromTextField);
            TextFieldUtils.setValid(xRangeToTextField);
        }
    }

    private void handleYRange(TextField currentTextField) {
        double lower, upper;
        boolean lowerInRange, upperInRange;

        try {
            lower = Double.parseDouble(yRangeFromTextField.getText());
            upper = Double.parseDouble(yRangeToTextField.getText());
        } catch (NumberFormatException e) {
            TextFieldUtils.setInvalid(currentTextField);
            wrongParameters.setValue(true);
            return;
        }

        lowerInRange = textFieldValueInRange(lower, AbstractTestFunction.MIN_Y, upper, yRangeFromTextField);
        upperInRange = textFieldValueInRange(upper, lower, AbstractTestFunction.MAX_Y, yRangeToTextField);

        if (lowerInRange && upperInRange) {
            func.getValue().getLowerBoundaries()[1] = lower;
            func.getValue().getUpperBoundaries()[1] = upper;
            TextFieldUtils.setValid(yRangeFromTextField);
            TextFieldUtils.setValid(yRangeToTextField);
        }
    }

    private boolean textFieldValueInRange(double d, double minValue, double maxValue, TextField textField) {
        if (TextFieldUtils.textFieldValueInRange(d, minValue, maxValue, textField)) {
            validateTextFields();
            return true;
        } else {
            wrongParameters.setValue(true);
            return false;
        }
    }

    private void validateTextFields() {
        boolean textFieldsInvalid = TextFieldUtils.textFieldsInvalid(
                xRangeFromTextField, xRangeToTextField, yRangeFromTextField,
                yRangeToTextField, foodSourcesCountTextField, maxIterTextField, trialsLimitTextField);
        wrongParameters.setValue(textFieldsInvalid);
    }

    @FXML
    private void onActionFuncComboBox() {
        if (funcComboBoxChangeListenerActive) {
            if (controllerMediator.isMainControllerRegistered()) {
                controllerMediator.mainControllerGetFunctionChart().clearFoodSources();
                controllerMediator.mainControllerGetIterSlider().setDisable(true);
                controllerMediator.resultsControllerShowFuncBest(func.getValue().getMinValuePos(), func.getValue().getMinValue());
                controllerMediator.resultsControllerSetResultsVisible(false);
            }
            func.getValue().restoreDefaultRanges();
            setRangeTextFields();
        }
    }

    private void setRangeTextFields() {
        rangeChangeListenersActive = false;
        xRangeFromTextField.textProperty().setValue(String.valueOf(func.getValue().getLowerBoundaries()[0]));
        yRangeFromTextField.textProperty().setValue(String.valueOf(func.getValue().getLowerBoundaries()[1]));
        // Set the old value manually to ensure that it is different from the new one
        // in order to run text-field's listeners
        xRangeToTextField.textProperty().setValue(String.valueOf(-Double.MAX_VALUE));
        yRangeToTextField.textProperty().setValue(String.valueOf(-Double.MAX_VALUE));
        xRangeToTextField.textProperty().setValue(String.valueOf(func.getValue().getUpperBoundaries()[0]));
        rangeChangeListenersActive = true; // Draw chart only once
        yRangeToTextField.textProperty().setValue(String.valueOf(func.getValue().getUpperBoundaries()[1]));
    }

    @FXML
    private void onActionStartButton() {
        int foodSourcesCount, maxIter, trialsLimit;

        try {
            foodSourcesCount = Integer.parseInt(foodSourcesCountTextField.getText());
            maxIter = Integer.parseInt(maxIterTextField.getText());
            trialsLimit = Integer.parseInt(trialsLimitTextField.getText());
        } catch (NumberFormatException e) {
            return;
        }

        ArtificialBeeColony abc = new ArtificialBeeColony(foodSourcesCount, maxIter, func.getValue(), trialsLimit);
        abc.run();
        ABCResults results = new ABCResults(abc);
        controllerMediator.resultsControllerSetResults(results);

        initIterSlider(results);
        controllerMediator.resultsControllerSetResultsVisible(true);
        controllerMediator.resultsControllerShowResults(maxIter);
    }

    private void initIterSlider(ABCResults results) {
        int maxIter = results.getMaxIter();
        Slider iterSlider = controllerMediator.mainControllerGetIterSlider();

        iterSlider.setDisable(false);
        iterSlider.setShowTickMarks(true);
        iterSlider.setShowTickLabels(true);
        iterSlider.setMin(0);
        iterSlider.setMax(maxIter);
        iterSlider.setBlockIncrement(1);

        if (maxIter < SLIDER_BY_ONE_TICKS_LIMIT) {
            iterSlider.setMajorTickUnit(1);
            iterSlider.setMinorTickCount(0);
        } else {
            iterSlider.setMajorTickUnit((int) ((double) maxIter / SLIDER_MAJOR_TICKS_COUNT));
            iterSlider.setMinorTickCount((int) iterSlider.getMajorTickUnit() - 1);
        }

        if (sliderValueChangeListener != null) {
            iterSlider.valueProperty().removeListener(sliderValueChangeListener);
        }
        sliderValueChangeListener = (observable, oldValue, newValue) -> {
            int iterNumber = newValue.intValue();
            controllerMediator.mainControllerGetFunctionChart().drawFoodSources(results.getAllFoodSources()[iterNumber]);
            controllerMediator.resultsControllerShowResults(iterNumber);
        };

        iterSlider.valueProperty().addListener(sliderValueChangeListener);
        iterSlider.setValue(maxIter);
        controllerMediator.mainControllerGetFunctionChart().drawFoodSources(results.getAllFoodSources()[maxIter]);
    }

    public void initResults(ABCResults results) throws IOException {
        AbstractTestFunction func = testFunctionObservableMap.get(results.getTestFunctionName());
        if (func == null) {
            throw new IOException("Unknown function: " + results.getTestFunctionName());
        }
        funcComboBox.getSelectionModel().select(func);

        func.getLowerBoundaries()[0] = results.getLowerBoundaries()[0];
        func.getLowerBoundaries()[1] = results.getLowerBoundaries()[1];
        func.getUpperBoundaries()[0] = results.getUpperBoundaries()[0];
        func.getUpperBoundaries()[1] = results.getUpperBoundaries()[1];
        func.setMinimum(results.getMinValuePos(), results.getMinValue());
        setRangeTextFields();
        handleXRange(xRangeFromTextField);
        handleYRange(yRangeFromTextField);

        controllerMediator.resultsControllerShowResults(results.getMaxIter());

        maxIterTextField.setText(String.valueOf(results.getMaxIter()));
        foodSourcesCountTextField.setText(String.valueOf(results.getFoodSourcesCount()));
        trialsLimitTextField.setText(String.valueOf(results.getTrialsLimit()));

        initIterSlider(results);
    }

    public AbstractTestFunction getFunc() {
        return func.getValue();
    }
}
