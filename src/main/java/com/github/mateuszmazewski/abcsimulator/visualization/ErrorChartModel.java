package com.github.mateuszmazewski.abcsimulator.visualization;

import com.github.mateuszmazewski.abcsimulator.abc.ABCResults;
import com.github.mateuszmazewski.abcsimulator.controller.ControllerMediator;
import com.github.mateuszmazewski.abcsimulator.utils.ObservableResourceFactory;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class ErrorChartModel {

    private final ObservableResourceFactory messagesFactory = ObservableResourceFactory.getInstance();

    public Alert createErrorChartDialog(ABCResults results) {
        Alert chartDialog = new Alert(Alert.AlertType.NONE);
        chartDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        ((Button) chartDialog.getDialogPane().lookupButton(ButtonType.CLOSE)).textProperty().bind(
                messagesFactory.getStringBinding("results.errorChartDialog.closeButton"));
        chartDialog.initOwner(ControllerMediator.getInstance().mainControllerGetStage());
        chartDialog.setWidth(800);
        chartDialog.setHeight(600);
        chartDialog.setResizable(true);
        chartDialog.titleProperty().bind(messagesFactory.getStringBinding("results.errorChartDialog.title"));

        Chart chart = createErrorChart(results);
        VBox vBox = new VBox(chart);
        vBox.setPrefSize(chartDialog.getWidth(), chartDialog.getHeight());
        VBox.setVgrow(chart, Priority.ALWAYS);

        if (Math.abs(results.getBestFx()[results.getBestFx().length - 1] - results.getMinValue()) == 0.0) {
            Label infoLabel = new Label();
            infoLabel.textProperty().bind(messagesFactory.getStringBinding("results.errorChart.precisionLimitInfo"));
            infoLabel.setWrapText(true);
            infoLabel.prefWidthProperty().bind(chartDialog.widthProperty());
            vBox.getChildren().add(infoLabel);
        }

        chartDialog.getDialogPane().setContent(vBox);
        return chartDialog;
    }

    private LineChart<Number, Number> createErrorChart(ABCResults results) {
        double[] bestFx = results.getBestFx();
        double minValue = results.getMinValue();

        NumberAxis xAxis = new NumberAxis();
        xAxis.labelProperty().bind(messagesFactory.getStringBinding("results.errorChart.xAxis"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.labelProperty().bind(messagesFactory.getStringBinding("results.errorChart.yAxis"));

        LineChart<Number, Number> errorChart = new LineChart<>(xAxis, yAxis);
        XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();
        double error;

        for (int iter = 0; iter < bestFx.length; iter++) {
            error = Math.abs(bestFx[iter] - minValue);
            if (error == 0.0) {
                // Obtained maximum double precision
                break;
            }
            dataSeries.getData().add(new XYChart.Data<>(iter, Math.log10(error)));
        }

        errorChart.getData().add(dataSeries);
        errorChart.setCreateSymbols(false);
        errorChart.setLegendVisible(false);
        errorChart.titleProperty().bind(messagesFactory.getStringBinding("results.errorChart.title"));
        Label titleLabel = ((Label) errorChart.lookup(".chart-title"));
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        return errorChart;
    }
}
