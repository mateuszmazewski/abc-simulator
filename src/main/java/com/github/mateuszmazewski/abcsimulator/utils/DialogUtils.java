package com.github.mateuszmazewski.abcsimulator.utils;

import com.github.mateuszmazewski.abcsimulator.controller.ControllerMediator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Optional;

public class DialogUtils {

    private static final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();

    public static void errorDialog(String msg) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        Stage stage = ControllerMediator.getInstance().mainControllerGetStage();
        error.initOwner(stage);

        error.titleProperty().bind(messagesFactory.getStringBinding("errorDialog.title"));
        error.headerTextProperty().bind(messagesFactory.getStringBinding("errorDialog.header"));
        TextArea textArea = new TextArea(msg);
        textArea.setEditable(false);
        error.getDialogPane().setContent(textArea);
        centerDialogOnStage(stage, error);
        error.showAndWait();
    }

    public static Optional<ButtonType> showConfirmDialog(String titleKey, String headerTextKey, String contextTextKey) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        Stage stage = ControllerMediator.getInstance().mainControllerGetStage();
        confirm.initOwner(stage);

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

        centerDialogOnStage(stage, confirm);
        return confirm.showAndWait();
    }

    public static void showAboutDialog() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = ControllerMediator.getInstance().mainControllerGetStage();
        about.initOwner(stage);

        about.titleProperty().bind(messagesFactory.getStringBinding("aboutDialog.title"));
        about.headerTextProperty().bind(messagesFactory.getStringBinding("aboutDialog.header"));
        about.contentTextProperty().bind(messagesFactory.getStringBinding("aboutDialog.content"));
        centerDialogOnStage(stage, about);
        about.showAndWait();
    }

    public static void centerDialogOnStage(Stage parent, Alert dialog) {
        dialog.setX(parent.getX() + (parent.getWidth() / 2) - dialog.getWidth() / 2);
        dialog.setY(parent.getY() + (parent.getHeight() / 2) - dialog.getHeight() / 2);
    }
}
