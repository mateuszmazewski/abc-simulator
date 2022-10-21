package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

import java.util.Optional;

public class MainController {

    @FXML
    private BorderPane borderPane;

    public void setCenter(String fxmlPath) {
        borderPane.setCenter(FxmlUtils.loadFxml(fxmlPath));
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
