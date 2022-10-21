package com.github.mateuszmazewski.abcsimulator.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.util.Optional;
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

    public static Optional<ButtonType> showConfirmDialog(String titleKey, String headerTextKey, String contextTextKey) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

        try {
            confirm.setTitle(messagesBundle.getString(titleKey));
            confirm.setHeaderText(messagesBundle.getString(headerTextKey));
            if (contextTextKey != null && !contextTextKey.isEmpty()) {
                confirm.setContentText(messagesBundle.getString(contextTextKey));
            }
        } catch (Exception e) {
            errorDialog(e.getMessage());
            return Optional.empty();
        }

        return confirm.showAndWait();
    }

    public static void showAboutDialog() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);

        about.setTitle(messagesBundle.getString("aboutDialog.title"));
        about.setHeaderText(messagesBundle.getString("aboutDialog.header"));
        about.setContentText(messagesBundle.getString("aboutDialog.content"));
        about.showAndWait();
    }
}
