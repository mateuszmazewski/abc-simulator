package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.BealeFunction;
import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import com.github.mateuszmazewski.abcsimulator.visualization.FunctionChart2D;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.Optional;

public class MainController {

    @FXML
    private BorderPane borderPane;

    public void setCenter(String fxmlPath) {
        borderPane.setCenter(FxmlUtils.loadFxml(fxmlPath));
    }

    @FXML
    private void initialize() {
        Pane functionChart2D = new FunctionChart2D(new BealeFunction());
        BorderPane.setMargin(functionChart2D, new Insets(10, 10, 10, 10));
        borderPane.setCenter(functionChart2D);
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
}
