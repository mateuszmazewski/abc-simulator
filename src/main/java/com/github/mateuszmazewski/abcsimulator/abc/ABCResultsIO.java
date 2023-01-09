package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.TestFunctionUtils;
import com.github.mateuszmazewski.abcsimulator.utils.ObservableResourceFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ABCResultsIO {

    public static final String FUNCTION_PARAM = "function";
    public static final String X_RANGE_PARAM = "x_range";
    public static final String Y_RANGE_PARAM = "y_range";
    public static final String ITERATIONS_PARAM = "iterations";
    public static final String FOOD_SOURCES_COUNT_PARAM = "food_sources_count";
    public static final String TRIALS_LIMIT_PARAM = "trials_limit";
    public static final String MIN_POSSIBLE_VALUE_PARAM = "min_possible_value";
    public static final String MIN_POSSIBLE_VALUE_POSITION_PARAM = "min_possible_value_position";
    public static final String MIN_FOUND_VALUE_PARAM = "min_found_value";
    public static final String MIN_FOUND_VALUE_POSITION_PARAM = "min_found_value_position";
    public static final String BEST_ITERATION_PARAM = "best_iteration";
    private final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();
    private final Stage stage;

    public ABCResultsIO(Stage stage) {
        this.stage = stage;
    }

    public void saveResults(ABCResults results) throws IOException {
        if (results.getAllFoodSources() == null || results.getBestFoodSources() == null || results.getBestFx() == null) {
            throw new IOException("Saving error - empty results.");
        }

        String initialFileName = results.getTestFunctionName().replace("Function", "")
                + "_fs" + results.getFoodSourcesCount()
                + "_iter" + results.getMaxIter()
                + "_limit" + results.getTrialsLimit()
                + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss"));

        FileChooser fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(messagesFactory.getStringBinding("fileChooser.save.title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(messagesFactory.getResources().getString("fileChooser.extensionDescription.textFile"), "*.txt"),
                new FileChooser.ExtensionFilter(messagesFactory.getResources().getString("fileChooser.extensionDescription.allFiles"), "*"));
        fileChooser.setInitialFileName(initialFileName);
        File chosenFile = fileChooser.showSaveDialog(stage);

        if (chosenFile != null) {
            writeToFile(chosenFile, results);
        }
    }

    private void writeToFile(File file, ABCResults results) throws IOException {
        double[][][] allFoodSources = results.getAllFoodSources();
        double[][] bestFoodSources = results.getBestFoodSources();
        double[] bestFx = results.getBestFx();
        double[][] allFx = results.getAllFx();
        int maxIter = results.getMaxIter();
        int foodSourcesCount = results.getFoodSourcesCount();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(FUNCTION_PARAM + " = " + results.getTestFunctionName());
        writer.write("\n" + X_RANGE_PARAM + " = " + results.getLowerBoundaries()[0] + " " + results.getUpperBoundaries()[0]);
        writer.write("\n" + Y_RANGE_PARAM + " = " + results.getLowerBoundaries()[1] + " " + results.getUpperBoundaries()[1]);
        writer.write("\n" + ITERATIONS_PARAM + " = " + maxIter);
        writer.write("\n" + FOOD_SOURCES_COUNT_PARAM + " = " + foodSourcesCount);
        writer.write("\n" + TRIALS_LIMIT_PARAM + " = " + results.getTrialsLimit());

        writer.write("\n\n# Best possible solution");
        writer.write("\n" + MIN_POSSIBLE_VALUE_PARAM + " = " + results.getMinValue());
        writer.write("\n" + MIN_POSSIBLE_VALUE_POSITION_PARAM + " = " + results.getMinValuePos()[0] + " " + results.getMinValuePos()[1]);

        writer.write("\n\n# Best found solution");
        writer.write("\n" + BEST_ITERATION_PARAM + " = " + results.getBestIter());
        writer.write("\n" + MIN_FOUND_VALUE_PARAM + " = " + results.getFoundMinValue());
        writer.write("\n" + MIN_FOUND_VALUE_POSITION_PARAM + " = " + results.getFoundMinValuePos()[0] + " " + results.getFoundMinValuePos()[1]);

        writer.write("\n\nIterations");

        writer.write("\n\n# Best solution in each iteration");
        writer.write("\niteration\tbest_position_x\tbest_position_y\tbest_value\n");

        for (int iter = 0; iter <= maxIter; iter++) {
            writer.write(iter + "\t" + bestFoodSources[iter][0] + "\t" + bestFoodSources[iter][1] + "\t" + bestFx[iter] + "\n");
        }

        writer.write("\n# All solutions in each iteration");
        writer.write("\niteration\tfood_source_number\tposition_x\tposition_y\tvalue\n");

        double[] foodSourcePos;
        for (int iter = 0; iter <= maxIter; iter++) {
            for (int foodSource = 0; foodSource < foodSourcesCount; foodSource++) {
                foodSourcePos = allFoodSources[iter][foodSource];
                writer.write(iter + "\t" + foodSource + "\t" + foodSourcePos[0] + "\t" + foodSourcePos[1] + "\t" + allFx[iter][foodSource] + "\n");
            }
        }

        writer.close();
    }

    public ABCResults readResults() throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(messagesFactory.getStringBinding("fileChooser.read.title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(messagesFactory.getResources().getString("fileChooser.extensionDescription.textFile"), "*.txt"),
                new FileChooser.ExtensionFilter(messagesFactory.getResources().getString("fileChooser.extensionDescription.allFiles"), "*"));
        File chosenFile = fileChooser.showOpenDialog(stage);
        ABCResults results = null;

        if (chosenFile != null) {
            results = readFromFile(chosenFile);
        }
        return results;
    }

    private ABCResults readFromFile(File file) throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ABCResults results = new ABCResults();
        Map<String, Boolean> foundParametersInFileMap = initFoundParametersInFileMap();

        String line;
        String[] splitLine;
        int splitLineLength;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }
            if (line.trim().equals("Iterations")) {
                break;
            }

            splitLine = line.split("\\s+");
            splitLineLength = splitLine.length;
            if (splitLineLength < 3 || splitLineLength > 4) {
                throw new IOException("Wrong line format: line " + lineNumber);
            }

            switch (splitLine[0]) {
                case FUNCTION_PARAM:
                    if (!TestFunctionUtils.allTestFunctionNames.contains(splitLine[2])) {
                        throw new IOException("Unknown function: " + splitLine[2]);
                    }
                    results.setTestFunctionName(splitLine[2]);
                    foundParametersInFileMap.put(FUNCTION_PARAM, true);
                    break;
                case X_RANGE_PARAM:
                    if (splitLineLength != 4) {
                        throw new IOException("Wrong line format: line " + lineNumber);
                    }
                    results.getLowerBoundaries()[0] = Double.parseDouble(splitLine[2]);
                    results.getUpperBoundaries()[0] = Double.parseDouble(splitLine[3]);
                    if (results.getLowerBoundaries()[0] > results.getUpperBoundaries()[0]) {
                        throw new IOException("Wrong range <" + results.getLowerBoundaries()[0] + ", " + results.getUpperBoundaries()[0] + ">, line: " + lineNumber);
                    }
                    foundParametersInFileMap.put(X_RANGE_PARAM, true);
                    break;
                case Y_RANGE_PARAM:
                    if (splitLineLength != 4) {
                        throw new IOException("Wrong line format: line " + lineNumber);
                    }
                    results.getLowerBoundaries()[1] = Double.parseDouble(splitLine[2]);
                    results.getUpperBoundaries()[1] = Double.parseDouble(splitLine[3]);
                    if (results.getLowerBoundaries()[1] > results.getUpperBoundaries()[1]) {
                        throw new IOException("Wrong range <" + results.getLowerBoundaries()[1] + ", " + results.getUpperBoundaries()[1] + ">, line: " + lineNumber);
                    }
                    foundParametersInFileMap.put(Y_RANGE_PARAM, true);
                    break;
                case ITERATIONS_PARAM:
                    results.setMaxIter(Integer.parseInt(splitLine[2]));
                    foundParametersInFileMap.put(ITERATIONS_PARAM, true);
                    break;
                case FOOD_SOURCES_COUNT_PARAM:
                    results.setFoodSourcesCount(Integer.parseInt(splitLine[2]));
                    foundParametersInFileMap.put(FOOD_SOURCES_COUNT_PARAM, true);
                    break;
                case TRIALS_LIMIT_PARAM:
                    results.setTrialsLimit(Integer.parseInt(splitLine[2]));
                    foundParametersInFileMap.put(TRIALS_LIMIT_PARAM, true);
                    break;
                case MIN_POSSIBLE_VALUE_PARAM:
                    results.setMinValue(Double.parseDouble(splitLine[2]));
                    foundParametersInFileMap.put(MIN_POSSIBLE_VALUE_PARAM, true);
                    break;
                case MIN_POSSIBLE_VALUE_POSITION_PARAM:
                    results.getMinValuePos()[0] = Double.parseDouble(splitLine[2]);
                    results.getMinValuePos()[1] = Double.parseDouble(splitLine[3]);
                    foundParametersInFileMap.put(MIN_POSSIBLE_VALUE_POSITION_PARAM, true);
                    break;
                case BEST_ITERATION_PARAM:
                    results.setBestIter(Integer.parseInt(splitLine[2]));
                    foundParametersInFileMap.put(BEST_ITERATION_PARAM, true);
                    break;
                case MIN_FOUND_VALUE_PARAM:
                    results.setFoundMinValue(Double.parseDouble(splitLine[2]));
                    foundParametersInFileMap.put(MIN_FOUND_VALUE_PARAM, true);
                    break;
                case MIN_FOUND_VALUE_POSITION_PARAM:
                    results.getFoundMinValuePos()[0] = Double.parseDouble(splitLine[2]);
                    results.getFoundMinValuePos()[1] = Double.parseDouble(splitLine[3]);
                    foundParametersInFileMap.put(MIN_FOUND_VALUE_POSITION_PARAM, true);
                    break;
                default:
                    throw new IOException("Wrong line format: line " + lineNumber);
            }
        }

        List<String> missingParams = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : foundParametersInFileMap.entrySet()) {
            if (!entry.getValue()) {
                missingParams.add(entry.getKey());
            }
        }
        if (!missingParams.isEmpty()) {
            throw new IOException("File is missing required parameters: " + String.join(", ", missingParams));
        }

        if (results.getMaxIter() < ArtificialBeeColony.MAX_ITER_LOWER_LIMIT || results.getMaxIter() > ArtificialBeeColony.MAX_ITER_UPPER_LIMIT) {
            throw new IOException("Parameter " + ITERATIONS_PARAM + " must be in range" +
                    " <" + ArtificialBeeColony.MAX_ITER_LOWER_LIMIT + ", " + ArtificialBeeColony.MAX_ITER_UPPER_LIMIT + ">");
        }
        if (results.getFoodSourcesCount() < ArtificialBeeColony.MIN_FOOD_SOURCES_COUNT || results.getFoodSourcesCount() > ArtificialBeeColony.MAX_FOOD_SOURCES_COUNT) {
            throw new IOException("Parameter " + FOOD_SOURCES_COUNT_PARAM + " must be in range" +
                    " <" + ArtificialBeeColony.MIN_FOOD_SOURCES_COUNT + ", " + ArtificialBeeColony.MAX_FOOD_SOURCES_COUNT + ">");
        }
        if (results.getTrialsLimit() < ArtificialBeeColony.MIN_TRIALS_LIMIT || results.getTrialsLimit() > ArtificialBeeColony.MAX_TRIALS_LIMIT) {
            throw new IOException("Parameter " + TRIALS_LIMIT_PARAM + " must be in range" +
                    " <" + ArtificialBeeColony.MIN_TRIALS_LIMIT + ", " + ArtificialBeeColony.MAX_TRIALS_LIMIT + ">");
        }


        results.setBestFoodSources(new double[results.getMaxIter() + 1][2]);
        results.setAllFoodSources(new double[results.getMaxIter() + 1][results.getFoodSourcesCount()][2]);
        results.setBestFx(new double[results.getMaxIter() + 1]);
        results.setAllFx(new double[results.getMaxIter() + 1][results.getFoodSourcesCount()]);
        int iter, foodSource;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            splitLine = line.split("\\s+");
            splitLineLength = splitLine.length;
            if (splitLine[0].equals("iteration")) {
                continue;
            }

            if (splitLineLength == 4) {
                iter = Integer.parseInt(splitLine[0]);

                results.getBestFoodSources()[iter][0] = Double.parseDouble(splitLine[1]);
                results.getBestFoodSources()[iter][1] = Double.parseDouble(splitLine[2]);
                results.getBestFx()[iter] = Double.parseDouble(splitLine[3]);
            } else if (splitLineLength == 5) {
                iter = Integer.parseInt(splitLine[0]);
                foodSource = Integer.parseInt(splitLine[1]);

                results.getAllFoodSources()[iter][foodSource][0] = Double.parseDouble(splitLine[2]);
                results.getAllFoodSources()[iter][foodSource][1] = Double.parseDouble(splitLine[3]);
                results.getAllFx()[iter][foodSource] = Double.parseDouble(splitLine[4]);
            } else {
                throw new IOException("Wrong line format: line " + lineNumber);
            }
        }

        return results;
    }

    private Map<String, Boolean> initFoundParametersInFileMap() {
        Map<String, Boolean> map = new HashMap<>();
        Stream.of(
                FUNCTION_PARAM,
                X_RANGE_PARAM,
                Y_RANGE_PARAM,
                ITERATIONS_PARAM,
                FOOD_SOURCES_COUNT_PARAM,
                TRIALS_LIMIT_PARAM,
                MIN_POSSIBLE_VALUE_PARAM,
                MIN_POSSIBLE_VALUE_POSITION_PARAM,
                BEST_ITERATION_PARAM,
                MIN_FOUND_VALUE_PARAM,
                MIN_FOUND_VALUE_POSITION_PARAM
        ).forEach(param -> map.put(param, false));
        return map;
    }
}
