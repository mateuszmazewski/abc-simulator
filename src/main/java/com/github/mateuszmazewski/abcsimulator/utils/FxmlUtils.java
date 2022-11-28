package com.github.mateuszmazewski.abcsimulator.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.util.Locale;
import java.util.ResourceBundle;

public class FxmlUtils {

    public static Pane loadFxml(String fxmlPath, Locale locale) {
        try {
            return getLoader(fxmlPath, locale).load();
        } catch (Exception e) {
            DialogUtils.errorDialog(e.getMessage());
        }
        return null;
    }

    public static FXMLLoader getLoader(String fxmlPath, Locale locale) {
        FXMLLoader loader = new FXMLLoader(FxmlUtils.class.getResource(fxmlPath));
        loader.setResources(getResourceBundle(locale));
        return loader;
    }

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle("bundles.messages", locale);
    }
}
