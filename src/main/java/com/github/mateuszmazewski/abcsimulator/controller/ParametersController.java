package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.abc.ArtificialBeeColony;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.*;
import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ParametersController {

    private static final int SLIDER_MAJOR_TICKS_COUNT = 20;
    private static final int SLIDER_BY_ONE_TICKS_LIMIT = 30;
    public static final int INITIAL_SWARM_SIZE = 50;
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
    private TextField swarmSizeTextField;

    @FXML
    private TextField maxIterTextField;

    @FXML
    private TextField trialsLimitTextField;

    @FXML
    private Button startButton;

    private final ObservableMap<String, AbstractTestFunction> testFunctionObservableMap = FXCollections.observableHashMap();
    private final ObjectProperty<AbstractTestFunction> func = new SimpleObjectProperty<>();
    private final String textFieldDefaultStyle = new TextField().getStyle();
    private final String textFieldErrorStyle = "-fx-background-color: lightcoral;";
    private final BooleanProperty wrongParameters = new SimpleBooleanProperty(false);

    // --------------------Injected by MainController--------------------
    private MainController mainController;
    private Slider iterSlider;

    //-------------------------------------------------------------------
    private ChangeListener<Number> sliderValueChangeListener;
    private boolean rangeChangeListenersActive = true;

    @FXML
    private void initialize() {
        ResourceBundle messagesBundle = FxmlUtils.getResourceBundle();

        AbstractTestFunction rastrigin = new RastriginFunction();
        rastrigin.setName(messagesBundle.getString("rastriginFunction.name"));
        testFunctionObservableMap.put(rastrigin.getClass().getSimpleName(), rastrigin);

        AbstractTestFunction ackley = new AckleyFunction();
        ackley.setName(messagesBundle.getString("ackleyFunction.name"));
        testFunctionObservableMap.put(ackley.getClass().getSimpleName(), ackley);

        AbstractTestFunction sphere = new SphereFunction();
        sphere.setName(messagesBundle.getString("sphereFunction.name"));
        testFunctionObservableMap.put(sphere.getClass().getSimpleName(), sphere);

        AbstractTestFunction rosenbrock = new RosenbrockFunction();
        rosenbrock.setName(messagesBundle.getString("rosenbrockFunction.name"));
        testFunctionObservableMap.put(rosenbrock.getClass().getSimpleName(), rosenbrock);

        AbstractTestFunction beale = new BealeFunction();
        beale.setName(messagesBundle.getString("bealeFunction.name"));
        testFunctionObservableMap.put(beale.getClass().getSimpleName(), beale);

        AbstractTestFunction goldsteinPrice = new GoldsteinPriceFunction();
        goldsteinPrice.setName(messagesBundle.getString("goldsteinPriceFunction.name"));
        testFunctionObservableMap.put(goldsteinPrice.getClass().getSimpleName(), goldsteinPrice);

        AbstractTestFunction booth = new BoothFunction();
        booth.setName(messagesBundle.getString("boothFunction.name"));
        testFunctionObservableMap.put(booth.getClass().getSimpleName(), booth);

        AbstractTestFunction matyas = new MatyasFunction();
        matyas.setName(messagesBundle.getString("matyasFunction.name"));
        testFunctionObservableMap.put(matyas.getClass().getSimpleName(), matyas);

        AbstractTestFunction threeHumpCamel = new ThreeHumpCamelFunction();
        threeHumpCamel.setName(messagesBundle.getString("threeHumpCamelFunction.name"));
        testFunctionObservableMap.put(threeHumpCamel.getClass().getSimpleName(), threeHumpCamel);

        AbstractTestFunction eggholderFunction = new EggholderFunction();
        eggholderFunction.setName(messagesBundle.getString("eggholderFunction.name"));
        testFunctionObservableMap.put(eggholderFunction.getClass().getSimpleName(), eggholderFunction);

        funcComboBox.setItems(FXCollections.observableArrayList(testFunctionObservableMap.values()));
        func.bind(funcComboBox.valueProperty());
        funcComboBox.getSelectionModel().select(rastrigin);
        onActionFuncComboBox();

        startButton.disableProperty().bind(wrongParameters);

        addValueChangeListenerToTextFields();

        swarmSizeTextField.setText(String.valueOf(INITIAL_SWARM_SIZE));
        maxIterTextField.setText(String.valueOf(INITIAL_MAX_ITER));
        trialsLimitTextField.setText(String.valueOf(INITIAL_TRIALS_LIMIT));
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
            if (rangeChangeListenersActive) {
                if (textField == xRangeFromTextField || textField == xRangeToTextField) {
                    handleXRange(textField);
                } else if (textField == yRangeFromTextField || textField == yRangeToTextField) {
                    handleYRange(textField);
                }

                FunctionChart2D chart = mainController.getCenterChart();
                chart.setTestFunction(func.getValue());
                chart.drawAll();
                mainController.getResultsController().showFuncBest(func.getValue().getMinValuePos(), func.getValue().getMinValue());
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
        if (mainController != null) {
            mainController.getCenterChart().clearBees();
            iterSlider.setDisable(true);
            mainController.getResultsController().showFuncBest(func.getValue().getMinValuePos(), func.getValue().getMinValue());
            mainController.getResultsController().setResultsVisible(false);
        }
        func.getValue().restoreDefaultRanges();
        setRangeTextFields();
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
        ABCResults results = new ABCResults(abc);
        mainController.getResultsController().setResults(results);

        initIterSlider(results);
        mainController.getResultsController().setResultsVisible(true);
        mainController.getResultsController().showResults(maxIter);
    }

    private void initIterSlider(ABCResults results) {
        int maxIter = results.getMaxIter();
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
            mainController.getCenterChart().drawBees(results.getAllFoodSources()[iterNumber]);
            mainController.getResultsController().showResults(iterNumber);
        };

        iterSlider.valueProperty().addListener(sliderValueChangeListener);
        iterSlider.setValue(maxIter);
        mainController.getCenterChart().drawBees(results.getAllFoodSources()[maxIter]);
    }

    public void initResults(ABCResults results) {
        AbstractTestFunction func = testFunctionObservableMap.get(results.getTestFunctionName());
        funcComboBox.getSelectionModel().select(func);

        func.getLowerBoundaries()[0] = results.getLowerBoundaries()[0];
        func.getLowerBoundaries()[1] = results.getLowerBoundaries()[1];
        func.getUpperBoundaries()[0] = results.getUpperBoundaries()[0];
        func.getUpperBoundaries()[1] = results.getUpperBoundaries()[1];
        func.setMinValue(results.getMinValue());
        func.setMinValuePos(results.getMinValuePos());
        setRangeTextFields();

        mainController.getResultsController().showResults(results.getMaxIter());

        maxIterTextField.setText(String.valueOf(results.getMaxIter()));
        swarmSizeTextField.setText(String.valueOf(results.getSwarmSize()));
        trialsLimitTextField.setText(String.valueOf(results.getTrialsLimit()));

        initIterSlider(results);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setIterSlider(Slider iterSlider) {
        this.iterSlider = iterSlider;
    }

    public AbstractTestFunction getFunc() {
        return func.getValue();
    }
}
