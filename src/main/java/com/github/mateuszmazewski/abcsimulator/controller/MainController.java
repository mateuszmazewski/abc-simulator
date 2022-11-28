package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;
import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.RastriginFunction;
import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import com.github.mateuszmazewski.abcsimulator.utils.ObservableResourceFactory;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Optional;

public class MainController {

    private final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();

    private Stage stage;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Slider iterSlider;

    @FXML
    private ParametersController parametersController;

    @FXML
    private ResultsController resultsController;

    // ---------------------ONLY TO CHANGE THE LANGUAGE----------------------
    @FXML
    private Menu applicationMenu;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private Menu languageMenu;

    @FXML
    private CheckMenuItem alwaysOnTopCheckMenuItem;

    public FunctionChart2D getCenterChart() {
        return (FunctionChart2D) borderPane.getCenter();
    }

    @FXML
    private void initialize() {
        messagesFactory.setResources(FxmlUtils.getResourceBundle(Locale.forLanguageTag("pl")));
        initLanguageBindings();

        FunctionChart2D functionChart2D = new FunctionChart2D(new RastriginFunction());
        BorderPane.setMargin(functionChart2D, new Insets(10, 10, 10, 10));
        borderPane.setCenter(functionChart2D);
        parametersController.setMainController(this);

        iterSlider.setDisable(true);
        iterSlider.setMin(0);
        iterSlider.setMax(ParametersController.INITIAL_MAX_ITER);
        parametersController.setIterSlider(iterSlider);

        AbstractTestFunction func = parametersController.getFunc();
        resultsController.setMainController(this);
        resultsController.showFuncBest(func.getMinValuePos(), func.getMinValue());
        resultsController.setResultsVisible(false);
    }

    private void initLanguageBindings() {
        applicationMenu.textProperty().bind(messagesFactory.getStringBinding("topMenuBar.app"));
        alwaysOnTopCheckMenuItem.textProperty().bind(messagesFactory.getStringBinding("topMenuBar.alwaysOnTop"));
        helpMenu.textProperty().bind(messagesFactory.getStringBinding("topMenuBar.help"));
        closeMenuItem.textProperty().bind(messagesFactory.getStringBinding("topMenuBar.app.close"));
        aboutMenuItem.textProperty().bind(messagesFactory.getStringBinding("topMenuBar.help.about"));
        languageMenu.textProperty().bind(messagesFactory.getStringBinding("topMenuBar.language"));
    }

    @FXML
    private void setAlwaysOnTop(ActionEvent actionEvent) {
        boolean checkboxSelected = ((CheckMenuItem) actionEvent.getSource()).isSelected();
        stage.setAlwaysOnTop(checkboxSelected);
    }

    @FXML
    private void onActionPolishRadioMenuItem() {
        messagesFactory.setResources(FxmlUtils.getResourceBundle(Locale.forLanguageTag("pl")));
    }

    @FXML
    private void onActionEnglishRadioMenuItem() {
        messagesFactory.setResources(FxmlUtils.getResourceBundle(Locale.ROOT));
    }

    @FXML
    private void closeApp() {
        Optional<ButtonType> buttonType = DialogUtils.showConfirmDialog(
                "exitDialog.title",
                "exitDialog.header",
                null);
        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void showAboutDialog() {
        DialogUtils.showAboutDialog();
    }

    public ResultsController getResultsController() {
        return resultsController;
    }

    public ParametersController getParametersController() {
        return parametersController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
