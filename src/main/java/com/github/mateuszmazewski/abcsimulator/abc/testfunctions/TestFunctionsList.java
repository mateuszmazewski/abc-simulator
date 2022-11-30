package com.github.mateuszmazewski.abcsimulator.abc.testfunctions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestFunctionsList {

    public static final List<Class<? extends AbstractTestFunction>> allTestFunctionClasses = Arrays.asList(
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
}
