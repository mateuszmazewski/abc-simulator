package com.github.mateuszmazewski.abcsimulator.utils;

import com.github.mateuszmazewski.abcsimulator.controller.ControllerMediator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.util.Optional;

public class DialogUtils {

    private static final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();

    public static void errorDialog(String msg) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.initOwner(ControllerMediator.getInstance().mainControllerGetStage());

        error.titleProperty().bind(messagesFactory.getStringBinding("errorDialog.title"));
        error.headerTextProperty().bind(messagesFactory.getStringBinding("errorDialog.header"));
        TextArea textArea = new TextArea(msg);
        textArea.setEditable(false);
        error.getDialogPane().setContent(textArea);
        error.showAndWait();
    }

    public static Optional<ButtonType> showConfirmDialog(String titleKey, String headerTextKey, String contextTextKey) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(ControllerMediator.getInstance().mainControllerGetStage());

        try {
            confirm.titleProperty().bind(messagesFactory.getStringBinding(titleKey));
            confirm.headerTextProperty().bind(messagesFactory.getStringBinding(headerTextKey));
            if (contextTextKey != null && !contextTextKey.isEmpty()) {
                confirm.contentTextProperty().bind(messagesFactory.getStringBinding(contextTextKey));
            }
        } catch (Exception e) {
            errorDialog(e.getMessage());
            return Optional.empty();
        }

        return confirm.showAndWait();
    }

    public static void showAboutDialog() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.initOwner(ControllerMediator.getInstance().mainControllerGetStage());

        about.titleProperty().bind(messagesFactory.getStringBinding("aboutDialog.title"));
        about.headerTextProperty().bind(messagesFactory.getStringBinding("aboutDialog.header"));
        about.contentTextProperty().bind(messagesFactory.getStringBinding("aboutDialog.content"));
        about.showAndWait();
    }
}
