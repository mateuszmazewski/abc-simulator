package com.github.mateuszmazewski.abcsimulator.visualization;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class FunctionChart2D extends GridPane {

    private final AbstractTestFunction testFunction;
    private final Canvas chartCanvas, xAxisCanvas, yAxisCanvas;
    private final PixelWriter pixelWriter;
    private double funcMinValue = Double.MAX_VALUE;
    private double funcMaxValue = Double.MIN_VALUE;
    private final double x1, x2, y1, y2; // Function's args range
    private int chartCanvasWidth, chartCanvasHeight;

    public FunctionChart2D(AbstractTestFunction testFunction) {
        this.testFunction = testFunction;

        if (testFunction.getDim() != 2) {
            throw new IllegalArgumentException("Cannot visualize function with dimension other than 2");
        }

        this.x1 = testFunction.getLowerBoundaries()[0];
        this.x2 = testFunction.getUpperBoundaries()[0];
        this.y1 = testFunction.getLowerBoundaries()[1];
        this.y2 = testFunction.getUpperBoundaries()[1];

        this.chartCanvas = new Canvas();
        this.xAxisCanvas = new Canvas();
        this.yAxisCanvas = new Canvas();
        this.pixelWriter = this.chartCanvas.getGraphicsContext2D().getPixelWriter();
        this.chartCanvas.widthProperty().bind(this.chartCanvas.heightProperty()); // chartCanvas is square
        this.chartCanvas.heightProperty().bind(this.heightProperty().multiply(0.9));
        this.chartCanvasWidth = (int) this.chartCanvas.getWidth();
        this.chartCanvasHeight = (int) this.chartCanvas.getHeight();

        this.getChildren().addAll(this.chartCanvas, this.xAxisCanvas, this.yAxisCanvas);
        GridPane.setHalignment(this.xAxisCanvas, HPos.RIGHT);
        GridPane.setValignment(this.yAxisCanvas, VPos.TOP);
        GridPane.setConstraints(this.chartCanvas, 1, 0, 1, 1);
        GridPane.setConstraints(this.xAxisCanvas, 0, 1, 2, 1);
        GridPane.setConstraints(this.yAxisCanvas, 0, 0, 1, 2);

        updateFuncValuesRange();
        visualizeFunc();
        drawAxes();

        ChangeListener<Number> paneSizeListener = (observable, oldValue, newValue) -> {
            this.chartCanvasWidth = (int) this.chartCanvas.getWidth();
            this.chartCanvasHeight = (int) this.chartCanvas.getHeight();
            updateFuncValuesRange();
            visualizeFunc();
            drawAxes();
        };

        this.widthProperty().addListener(paneSizeListener);
        this.heightProperty().addListener(paneSizeListener);
    }

    private void drawAxes() {
        //TODO - read from TestFunction
        double xAxisStep = 1.0;
        double yAxisStep = 1.0;
        int xStepsCount = (int) ((x2 - x1) / xAxisStep) + 1;
        int yStepsCount = (int) ((y2 - y1) / yAxisStep) + 1;

        GraphicsContext xAxisGraphics = xAxisCanvas.getGraphicsContext2D();
        GraphicsContext yAxisGraphics = yAxisCanvas.getGraphicsContext2D();
        xAxisGraphics.setFill(Color.BLACK);
        yAxisGraphics.setFill(Color.BLACK);

        xAxisGraphics.clearRect(0, 0, xAxisCanvas.getWidth(), xAxisCanvas.getHeight());
        yAxisGraphics.clearRect(0, 0, yAxisCanvas.getWidth(), yAxisCanvas.getHeight());

        xAxisCanvas.widthProperty().bind(chartCanvas.widthProperty());
        yAxisCanvas.heightProperty().bind(chartCanvas.heightProperty());
        xAxisCanvas.setHeight(60);
        yAxisCanvas.setWidth(60);

        xAxisCanvas.getGraphicsContext2D().fillRect(0, 0, xAxisCanvas.getWidth(), xAxisCanvas.getHeight());
        yAxisCanvas.getGraphicsContext2D().fillRect(0, 0, yAxisCanvas.getWidth(), yAxisCanvas.getHeight());
    }

    private void visualizeFunc() {
        for (int xCanvas = 0; xCanvas < this.chartCanvasWidth; xCanvas++) {
            for (int yCanvas = 0; yCanvas < this.chartCanvasHeight; yCanvas++) {
                double funcVal = getFuncVal(xCanvas, yCanvas);

                // Scale func. values to [0, 240] ->  HSV color space
                // 0 deg. = red, 120 deg. = green, 240 deg. = blue
                double funcValScaled = ((funcVal - this.funcMinValue) / (this.funcMaxValue - this.funcMinValue) * 240);
                Color color = Color.hsb(240 - funcValScaled, 1.0, 1.0);
                this.pixelWriter.setColor(xCanvas, yCanvas, color);
            }
        }
    }

    private void updateFuncValuesRange() {
        for (int xCanvas = 0; xCanvas < this.chartCanvasWidth; xCanvas++) {
            for (int yCanvas = this.chartCanvasHeight - 1; yCanvas >= 0; yCanvas--) {
                double funcVal = getFuncVal(xCanvas, yCanvas);
                this.funcMinValue = Math.min(this.funcMinValue, funcVal);
                this.funcMaxValue = Math.max(this.funcMaxValue, funcVal);
            }
        }
    }

    private double getFuncVal(int xCanvas, int yCanvas) {
        // Scale canvas' coords to function's coords
        double funcX = (double) xCanvas / this.chartCanvasWidth * (this.x2 - this.x1) + this.x1;
        double funcY = (double) yCanvas / this.chartCanvasHeight * (this.y2 - this.y1) + this.y1;
        return this.testFunction.getValue(new double[]{funcX, funcY});
    }
}
