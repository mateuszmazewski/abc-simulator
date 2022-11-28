package com.github.mateuszmazewski.abcsimulator;

import com.github.mateuszmazewski.abcsimulator.controller.MainController;
import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    private static final String MAIN_FXML = "/fxml/Main.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = FxmlUtils.getLoader(MAIN_FXML, Locale.forLanguageTag("pl"));
        Pane mainBorderPane = mainLoader.load();

        assert mainBorderPane != null;
        ((MainController) mainLoader.getController()).setStage(primaryStage);
        Scene scene = new Scene(mainBorderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle(FxmlUtils.getResourceBundle(Locale.forLanguageTag("pl")).getString("app.title"));
        primaryStage.show();
    }
}