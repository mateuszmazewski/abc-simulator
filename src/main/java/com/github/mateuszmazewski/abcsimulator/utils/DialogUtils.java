package com.github.mateuszmazewski.abcsimulator.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.util.ResourceBundle;

public class DialogUtils {

    private static final ResourceBundle messagesBundle = FxmlUtils.getResourceBundle();
    public static void errorDialog(String msg) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(messagesBundle.getString("errorDialog.title"));
        error.setHeaderText(messagesBundle.getString("errorDialog.header"));
        TextArea textArea = new TextArea(msg);
        textArea.setEditable(false);
        error.getDialogPane().setContent(textArea);
        error.showAndWait();
    }
}
