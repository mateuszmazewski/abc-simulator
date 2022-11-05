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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.roundToTwoDecimalPlaces;

public class FunctionChart2D extends GridPane {

    private final AbstractTestFunction testFunction;
    private final Canvas chartCanvas, xAxisCanvas, yAxisCanvas;
    private final PixelWriter pixelWriter;
    private double funcMinValue = Double.MAX_VALUE;
    private double funcMaxValue = Double.MIN_VALUE;
    private final double x1, x2, y1, y2; // Function's args range
    private int chartCanvasWidth, chartCanvasHeight;

    // -------------------------AXES' PARAMETERS-------------------------
    private final double axesFontSize = 12.0;
    private final Font axesFont = new Font(axesFontSize);
    private final double axesMarkerLength = 8.0;
    private final double axesGapBetweenMarkerAndText = 5.0;

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

        this.xAxisCanvas.widthProperty().bind(this.chartCanvas.widthProperty());
        this.yAxisCanvas.heightProperty().bind(this.chartCanvas.heightProperty());
        this.xAxisCanvas.setHeight(this.axesMarkerLength + this.axesFontSize + this.axesGapBetweenMarkerAndText);
        this.yAxisCanvas.setWidth(this.axesMarkerLength + this.axesGapBetweenMarkerAndText + this.getLongestYTextWidth());

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

    private double getLongestYTextWidth() {
        // Longest number on y-axis is a negative number with two digits after decimal point
        double longestNumberOnYAxis = -((int) Math.max(Math.abs(this.y1), Math.abs(this.y2)) + 0.77);
        Text longestYText = new Text(String.valueOf(longestNumberOnYAxis));
        longestYText.setFont(this.axesFont);
        return longestYText.getLayoutBounds().getWidth();
    }

    private void drawAxes() {
        int stepsCount = 10;
        double xAxisStep = this.xAxisCanvas.getWidth() / stepsCount;
        double yAxisStep = this.yAxisCanvas.getHeight() / stepsCount;
        double xFuncStep = (this.x2 - this.x1) / stepsCount;
        double yFuncStep = (this.y2 - this.y1) / stepsCount;

        GraphicsContext xAxisGraphics = this.xAxisCanvas.getGraphicsContext2D();
        GraphicsContext yAxisGraphics = this.yAxisCanvas.getGraphicsContext2D();
        xAxisGraphics.setStroke(Color.BLACK);
        yAxisGraphics.setStroke(Color.BLACK);

        double x, y;
        String xText, yText;
        xAxisGraphics.setFont(this.axesFont);
        yAxisGraphics.setFont(this.axesFont);

        xAxisGraphics.clearRect(0, 0, this.xAxisCanvas.getWidth(), this.xAxisCanvas.getHeight());
        yAxisGraphics.clearRect(0, 0, this.yAxisCanvas.getWidth(), this.yAxisCanvas.getHeight());

        for (int i = 0; i <= stepsCount; i++) {
            xText = String.valueOf(roundToTwoDecimalPlaces(this.x1 + i * xFuncStep));
            yText = String.valueOf(roundToTwoDecimalPlaces(this.y1 + (stepsCount - i) * yFuncStep));

            xAxisGraphics.setTextBaseline(VPos.BOTTOM);
            yAxisGraphics.setTextAlign(TextAlignment.RIGHT);

            // Determine line's position to avoid antialiasing
            // Determine text alignment/baseline to fit text on canvas
            if (i == 0) {
                // first marker
                x = Math.round(i * xAxisStep) + 0.5;
                xAxisGraphics.setTextAlign(TextAlignment.LEFT);

                y = Math.round(i * yAxisStep) + 0.5;
                yAxisGraphics.setTextBaseline(VPos.TOP);
            } else if (i == stepsCount) {
                // last marker
                x = Math.round(i * xAxisStep) - 0.5;
                xAxisGraphics.setTextAlign(TextAlignment.RIGHT);

                y = Math.round(i * yAxisStep) - 0.5;
                yAxisGraphics.setTextBaseline(VPos.BOTTOM);
            } else {
                x = Math.round(i * xAxisStep) + 0.5;
                xAxisGraphics.setTextAlign(TextAlignment.CENTER);

                y = Math.round(i * yAxisStep) + 0.5;
                yAxisGraphics.setTextBaseline(VPos.CENTER);
            }

            xAxisGraphics.strokeLine(x, 0, x, this.axesMarkerLength);
            xAxisGraphics.fillText(xText, x, this.axesMarkerLength + this.axesFontSize + this.axesGapBetweenMarkerAndText);

            yAxisGraphics.strokeLine(this.yAxisCanvas.getWidth(), y, this.yAxisCanvas.getWidth() - this.axesMarkerLength, y);
            yAxisGraphics.fillText(yText, this.yAxisCanvas.getWidth() - (this.axesMarkerLength + this.axesGapBetweenMarkerAndText), y);
        }
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
