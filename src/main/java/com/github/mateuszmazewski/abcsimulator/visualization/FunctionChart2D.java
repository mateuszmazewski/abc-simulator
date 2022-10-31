package com.github.mateuszmazewski.abcsimulator.visualization;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class FunctionChart2D extends Pane {

    private final AbstractTestFunction testFunction;
    private final Canvas canvas;
    private final PixelWriter pixelWriter;
    private double funcMinValue = Double.MAX_VALUE;
    private double funcMaxValue = Double.MIN_VALUE;
    private final double x1, x2, y1, y2; // Function's args range
    private int canvasWidth, canvasHeight;

    public FunctionChart2D(AbstractTestFunction testFunction) {
        this.testFunction = testFunction;

        if (testFunction.getDim() != 2) {
            throw new IllegalArgumentException("Cannot visualize function with dimension other than 2");
        }

        this.x1 = testFunction.getLowerBoundaries()[0];
        this.x2 = testFunction.getUpperBoundaries()[0];
        this.y1 = testFunction.getLowerBoundaries()[1];
        this.y2 = testFunction.getUpperBoundaries()[1];

        this.canvas = new Canvas();
        this.pixelWriter = this.canvas.getGraphicsContext2D().getPixelWriter();
        this.canvas.widthProperty().bind(this.canvas.heightProperty()); // Canvas is square
        this.canvas.heightProperty().bind(this.heightProperty()); // Canvas fills entire parent pane
        this.canvasWidth = (int) this.canvas.getWidth();
        this.canvasHeight = (int) this.canvas.getHeight();

        updateFuncValuesRange();
        visualizeFunc();

        ChangeListener<Number> paneSizeListener = (observable, oldValue, newValue) -> {
            this.canvasWidth = (int) this.canvas.getWidth();
            this.canvasHeight = (int) this.canvas.getHeight();
            updateFuncValuesRange();
            visualizeFunc();
        };

        this.getChildren().add(this.canvas);
        this.widthProperty().addListener(paneSizeListener);
        this.heightProperty().addListener(paneSizeListener);
    }

    private void visualizeFunc() {
        for (int xCanvas = 0; xCanvas < this.canvasWidth; xCanvas++) {
            for (int yCanvas = 0; yCanvas < this.canvasHeight; yCanvas++) {
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
        for (int xCanvas = 0; xCanvas < this.canvasWidth; xCanvas++) {
            for (int yCanvas = this.canvasHeight - 1; yCanvas >= 0; yCanvas--) {
                double funcVal = getFuncVal(xCanvas, yCanvas);
                this.funcMinValue = Math.min(this.funcMinValue, funcVal);
                this.funcMaxValue = Math.max(this.funcMaxValue, funcVal);
            }
        }
    }

    private double getFuncVal(int xCanvas, int yCanvas) {
        // Scale canvas' coords to function's coords
        double funcX = (double) xCanvas / this.canvasWidth * (this.x2 - this.x1) + this.x1;
        double funcY = (double) yCanvas / this.canvasHeight * (this.y2 - this.y1) + this.y1;
        return this.testFunction.getValue(new double[]{funcX, funcY});
    }
}
