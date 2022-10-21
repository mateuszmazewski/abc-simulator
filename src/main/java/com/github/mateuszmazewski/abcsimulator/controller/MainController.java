package com.github.mateuszmazewski.abcsimulator.controller;

import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private BorderPane borderPane;

    public void setCenter(String fxmlPath) {
        borderPane.setCenter(FxmlUtils.loadFxml(fxmlPath));
    }
}
