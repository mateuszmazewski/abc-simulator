package com.github.mateuszmazewski.abcsimulator.abc;

import com.github.mateuszmazewski.abcsimulator.utils.FxmlUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ABCResultsIO {

    private final Stage stage;
    private final ResourceBundle messages;

    public ABCResultsIO(Stage stage) {
        this.stage = stage;
        messages = FxmlUtils.getResourceBundle();
    }

    public void saveResults(ABCResults results) throws IOException {
        if (results.getAllFoodSources() == null || results.getBestFoodSources() == null || results.getBestFx() == null) {
            throw new IOException("Saving error - empty results.");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messages.getString("fileChooser.title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(messages.getString("fileChooser.extensionDescription"), "*.txt"));
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
}
