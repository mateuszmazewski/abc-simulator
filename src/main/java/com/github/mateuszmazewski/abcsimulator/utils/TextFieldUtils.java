package com.github.mateuszmazewski.abcsimulator.utils;

import javafx.scene.control.TextField;

import java.util.stream.Stream;

public class TextFieldUtils {

    private static final String textFieldDefaultStyle = new TextField().getStyle();
    private static final String textFieldErrorStyle = "-fx-background-color: lightcoral;";

    public static boolean textFieldsInvalid(TextField... textFields) {
        return Stream.of(textFields).anyMatch(textField -> textField.getStyle().equals(textFieldErrorStyle));
    }

    public static void setInvalid(TextField textField) {
        textField.setStyle(textFieldErrorStyle);
    }

    public static void setValid(TextField textField) {
        textField.setStyle(textFieldDefaultStyle);
    }

    public static boolean textFieldValueInRange(double d, double minValue, double maxValue, TextField textField, boolean strictInequalities) {
        boolean valid = strictInequalities ? d > minValue && d < maxValue : d >= minValue && d <= maxValue;
        if (valid) {
            setValid(textField);
        } else {
            setInvalid(textField);
        }
        return valid;
    }

}
