package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.utils.ObservableResourceFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ABCResultsIO {

    private final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();
    private final Stage stage;

    public ABCResultsIO(Stage stage) {
        this.stage = stage;
    }

    public void saveResults(ABCResults results) throws IOException {
        if (results.getAllFoodSources() == null || results.getBestFoodSources() == null || results.getBestFx() == null) {
            throw new IOException("Saving error - empty results.");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(messagesFactory.getStringBinding("fileChooser.save.title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(messagesFactory.getResources().getString("fileChooser.extensionDescription"), "*.txt"));
        fileChooser.setInitialFileName(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss")));
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
        int swarmSize = results.getSwarmSize();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write("function = " + results.getTestFunctionName());
        writer.write("\nx_range = " + results.getLowerBoundaries()[0] + " " + results.getUpperBoundaries()[0]);
        writer.write("\ny_range = " + results.getLowerBoundaries()[1] + " " + results.getUpperBoundaries()[1]);
        writer.write("\niterations = " + maxIter);
        writer.write("\nswarm_size = " + swarmSize);
        writer.write("\ntrials_limit = " + results.getTrialsLimit());

        writer.write("\n\n# Best possible solution");
        writer.write("\nmin_possible_value = " + results.getMinValue());
        writer.write("\nmin_possible_value_position = " + results.getMinValuePos()[0] + " " + results.getMinValuePos()[1]);

        writer.write("\n\n# Best found solution");
        writer.write("\nmin_found_value = " + results.getFoundMinValue());
        writer.write("\nmin_found_value_position = " + results.getFoundMinValuePos()[0] + " " + results.getFoundMinValuePos()[1]);

        writer.write("\n\nIterations");

        writer.write("\n\n# Best solution in each iteration");
        writer.write("\niteration\tbest_position_x\tbest_position_y\tbest_value\n");

        for (int iter = 0; iter <= maxIter; iter++) {
            writer.write(iter + "\t" + bestFoodSources[iter][0] + "\t" + bestFoodSources[iter][1] + "\t" + bestFx[iter] + "\n");
        }

        writer.write("\n# All solutions in each iteration");
        writer.write("\niteration\tbee_number\tposition_x\tposition_y\tvalue\n");

        double[] beePos;
        for (int iter = 0; iter <= maxIter; iter++) {
            for (int bee = 0; bee < swarmSize; bee++) {
                beePos = allFoodSources[iter][bee];
                writer.write(iter + "\t" + bee + "\t" + beePos[0] + "\t" + beePos[1] + "\t" + allFx[iter][bee] + "\n");
            }
        }

        writer.close();
    }

    public ABCResults readResults() throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(messagesFactory.getStringBinding("fileChooser.read.title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(messagesFactory.getResources().getString("fileChooser.extensionDescription"), "*.txt"));
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
                case "function":
                    results.setTestFunctionName(splitLine[2]);
                    break;
                case "x_range":
                    results.getLowerBoundaries()[0] = Double.parseDouble(splitLine[2]);
                    results.getUpperBoundaries()[0] = Double.parseDouble(splitLine[3]);
                    break;
                case "y_range":
                    results.getLowerBoundaries()[1] = Double.parseDouble(splitLine[2]);
                    results.getUpperBoundaries()[1] = Double.parseDouble(splitLine[3]);
                    break;
                case "iterations":
                    results.setMaxIter(Integer.parseInt(splitLine[2]));
                    break;
                case "swarm_size":
                    results.setSwarmSize(Integer.parseInt(splitLine[2]));
                    break;
                case "trials_limit":
                    results.setTrialsLimit(Integer.parseInt(splitLine[2]));
                    break;
                case "min_possible_value":
                    results.setMinValue(Double.parseDouble(splitLine[2]));
                    break;
                case "min_possible_value_position":
                    results.getMinValuePos()[0] = Double.parseDouble(splitLine[2]);
                    results.getMinValuePos()[1] = Double.parseDouble(splitLine[3]);
                    break;
                case "min_found_value":
                    results.setFoundMinValue(Double.parseDouble(splitLine[2]));
                    break;
                case "min_found_value_position":
                    results.getFoundMinValuePos()[0] = Double.parseDouble(splitLine[2]);
                    results.getFoundMinValuePos()[1] = Double.parseDouble(splitLine[3]);
                    break;
                default:
                    throw new IOException("Wrong line format: line " + lineNumber);
            }
        }

        results.setBestFoodSources(new double[results.getMaxIter() + 1][2]);
        results.setAllFoodSources(new double[results.getMaxIter() + 1][results.getSwarmSize()][2]);
        results.setBestFx(new double[results.getMaxIter() + 1]);
        results.setAllFx(new double[results.getMaxIter() + 1][results.getSwarmSize()]);
        int iter, bee;

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
                bee = Integer.parseInt(splitLine[1]);

                results.getAllFoodSources()[iter][bee][0] = Double.parseDouble(splitLine[2]);
                results.getAllFoodSources()[iter][bee][1] = Double.parseDouble(splitLine[3]);
                results.getAllFx()[iter][bee] = Double.parseDouble(splitLine[4]);
            } else {
                throw new IOException("Wrong line format: line " + lineNumber);
            }
        }

        return results;
    }
}
