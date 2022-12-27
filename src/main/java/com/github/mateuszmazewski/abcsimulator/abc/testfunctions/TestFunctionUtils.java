package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

import com.github.mateuszmazewski.abcsimulator.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestFunctionUtils {

    private static final List<Class<? extends AbstractTestFunction>> allTestFunctionClasses = Arrays.asList(
            AckleyFunction.class,
            BealeFunction.class,
            BoothFunction.class,
            EggholderFunction.class,
            GoldsteinPriceFunction.class,
            MatyasFunction.class,
            RastriginFunction.class,
            RosenbrockFunction.class,
            SphereFunction.class,
            ThreeHumpCamelFunction.class
    );

    public static final List<String> allTestFunctionNames = allTestFunctionClasses.stream()
            .map(Class::getSimpleName)
            .collect(Collectors.toList());

    public static ObservableMap<String, AbstractTestFunction> createTestFunctionObservableMap() {
        ObservableMap<String, AbstractTestFunction> testFunctionObservableMap = FXCollections.observableHashMap();
        allTestFunctionClasses.forEach(funcClass -> {
            try {
                testFunctionObservableMap.put(funcClass.getSimpleName(), funcClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                DialogUtils.errorDialog(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        });
        return testFunctionObservableMap;
    }
}
