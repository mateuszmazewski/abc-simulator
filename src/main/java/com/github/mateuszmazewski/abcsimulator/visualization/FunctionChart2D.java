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

    private AbstractTestFunction testFunction;
    private final Canvas chartCanvas, beesCanvas, xAxisCanvas, yAxisCanvas;
    private final PixelWriter pixelWriter;
    private double funcMinValue = Double.MAX_VALUE;
    private double funcMaxValue = Double.MIN_VALUE;
    private double x1;
    private double x2;
    private double y1;
    private double y2; // Function's args range
    private int chartCanvasWidth, chartCanvasHeight;

    // -------------------------AXES' PARAMETERS-------------------------
    private final double axesFontSize = 12.0;
    private final Font axesFont = new Font(axesFontSize);
    private final double axesMarkerLength = 8.0;
    private final double axesGapBetweenMarkerAndText = 5.0;

    // -------------------------------BEES-------------------------------
    private double[][] currentIterBees;

    public FunctionChart2D(AbstractTestFunction testFunction) {
        setTestFunction(testFunction);

        chartCanvas = new Canvas();
        beesCanvas = new Canvas();
        xAxisCanvas = new Canvas();
        yAxisCanvas = new Canvas();
        pixelWriter = chartCanvas.getGraphicsContext2D().getPixelWriter();
        chartCanvas.widthProperty().bind(chartCanvas.heightProperty()); // chartCanvas is square
        chartCanvas.heightProperty().bind(heightProperty().multiply(0.9));
        beesCanvas.widthProperty().bind(chartCanvas.widthProperty());
        beesCanvas.heightProperty().bind(chartCanvas.heightProperty());
        chartCanvasWidth = (int) chartCanvas.getWidth();
        chartCanvasHeight = (int) chartCanvas.getHeight();

        xAxisCanvas.widthProperty().bind(chartCanvas.widthProperty());
        yAxisCanvas.heightProperty().bind(chartCanvas.heightProperty());
        xAxisCanvas.setHeight(axesMarkerLength + axesFontSize + axesGapBetweenMarkerAndText);
        yAxisCanvas.setWidth(axesMarkerLength + axesGapBetweenMarkerAndText + getLongestYTextWidth());

        getChildren().addAll(chartCanvas, beesCanvas, xAxisCanvas, yAxisCanvas);
        GridPane.setHalignment(xAxisCanvas, HPos.RIGHT);
        GridPane.setValignment(yAxisCanvas, VPos.TOP);
        GridPane.setConstraints(chartCanvas, 1, 0, 1, 1);
        GridPane.setConstraints(beesCanvas, 1, 0, 1, 1);
        GridPane.setConstraints(xAxisCanvas, 0, 1, 2, 1);
        GridPane.setConstraints(yAxisCanvas, 0, 0, 1, 2);

        drawAll();

        ChangeListener<Number> paneSizeListener = (observable, oldValue, newValue) -> {
            chartCanvasWidth = (int) chartCanvas.getWidth();
            chartCanvasHeight = (int) chartCanvas.getHeight();
            updateFuncValuesRange();
            drawAll();
        };

        widthProperty().addListener(paneSizeListener);
        heightProperty().addListener(paneSizeListener);
    }

    private double getLongestYTextWidth() {
        // Longest number on y-axis is a negative number with two digits after decimal point
        double longestNumberOnYAxis = -((int) Math.max(Math.abs(y1), Math.abs(y2)) + 0.77);
        Text longestYText = new Text(String.valueOf(longestNumberOnYAxis));
        longestYText.setFont(axesFont);
        return longestYText.getLayoutBounds().getWidth();
    }

    private void drawAxes() {
        int stepsCount = 10;
        double xAxisStep = xAxisCanvas.getWidth() / stepsCount;
        double yAxisStep = yAxisCanvas.getHeight() / stepsCount;
        double xFuncStep = (x2 - x1) / stepsCount;
        double yFuncStep = (y2 - y1) / stepsCount;

        GraphicsContext xAxisGraphics = xAxisCanvas.getGraphicsContext2D();
        GraphicsContext yAxisGraphics = yAxisCanvas.getGraphicsContext2D();
        xAxisGraphics.setStroke(Color.BLACK);
        yAxisGraphics.setStroke(Color.BLACK);

        double x, y;
        String xText, yText;
        xAxisGraphics.setFont(axesFont);
        yAxisGraphics.setFont(axesFont);

        xAxisGraphics.clearRect(0, 0, xAxisCanvas.getWidth(), xAxisCanvas.getHeight());
        yAxisGraphics.clearRect(0, 0, yAxisCanvas.getWidth(), yAxisCanvas.getHeight());

        for (int i = 0; i <= stepsCount; i++) {
            xText = String.valueOf(roundToTwoDecimalPlaces(x1 + i * xFuncStep));
            yText = String.valueOf(roundToTwoDecimalPlaces(y1 + (stepsCount - i) * yFuncStep));

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

            xAxisGraphics.strokeLine(x, 0, x, axesMarkerLength);
            xAxisGraphics.fillText(xText, x, axesMarkerLength + axesFontSize + axesGapBetweenMarkerAndText);

            yAxisGraphics.strokeLine(yAxisCanvas.getWidth(), y, yAxisCanvas.getWidth() - axesMarkerLength, y);
            yAxisGraphics.fillText(yText, yAxisCanvas.getWidth() - (axesMarkerLength + axesGapBetweenMarkerAndText), y);
        }
    }

    private void drawFunc() {
        for (int xCanvas = 0; xCanvas < chartCanvasWidth; xCanvas++) {
            for (int yCanvas = 0; yCanvas < chartCanvasHeight; yCanvas++) {
                double funcVal = getFuncVal(xCanvas, yCanvas);
                // Scale func. values to [0, 240] ->  HSV color space
                // 0 deg. = red, 120 deg. = green, 240 deg. = blue
                double funcValScaled = ((funcVal - funcMinValue) / (funcMaxValue - funcMinValue) * 240);
                Color color = Color.hsb(240 - funcValScaled, 1.0, 1.0);
                pixelWriter.setColor(xCanvas, yCanvas, color);
            }
        }
    }

    public void drawBees(double[][] foodSources) {
        currentIterBees = foodSources; // In case user resizes the window, we need to draw bees again
        double[] canvasXY;
        GraphicsContext beesGraphics = beesCanvas.getGraphicsContext2D();
        beesGraphics.setFill(Color.WHITE);
        beesGraphics.clearRect(0, 0, beesCanvas.getWidth(), beesCanvas.getHeight());

        for (double[] foodSource : foodSources) {
            canvasXY = getCanvasXY(foodSource);
            beesGraphics.fillOval(canvasXY[0], canvasXY[1], 10, 10);
        }
    }

    private void updateFuncValuesRange() {
        funcMinValue = Double.MAX_VALUE;
        funcMaxValue = Double.MIN_VALUE;

        for (int xCanvas = 0; xCanvas < chartCanvasWidth; xCanvas++) {
            for (int yCanvas = chartCanvasHeight - 1; yCanvas >= 0; yCanvas--) {
                double funcVal = getFuncVal(xCanvas, yCanvas);
                funcMinValue = Math.min(funcMinValue, funcVal);
                funcMaxValue = Math.max(funcMaxValue, funcVal);
            }
        }
    }

    private double getFuncVal(int xCanvas, int yCanvas) {
        // Scale canvas' coords to function's coords
        double xFunc = (double) xCanvas / chartCanvasWidth * (x2 - x1) + x1;
        double yFunc = (double) yCanvas / chartCanvasHeight * (y2 - y1) + y1;
        return testFunction.getValue(new double[]{xFunc, yFunc});
    }

    private double[] getCanvasXY(double[] xyFunc) {
        // Scale function's coords to canvas' coords
        double xCanvas = (xyFunc[0] - x1) / (x2 - x1) * chartCanvasWidth;
        double yCanvas = (xyFunc[1] - y1) / (y2 - y1) * chartCanvasHeight;
        return new double[]{xCanvas, yCanvas};
    }

    public void setTestFunction(AbstractTestFunction testFunction) {
        if (testFunction.getDim() != 2) {
            throw new IllegalArgumentException("Cannot visualize function with dimension other than 2");
        }

        this.testFunction = testFunction;
        x1 = testFunction.getLowerBoundaries()[0];
        x2 = testFunction.getUpperBoundaries()[0];
        y1 = testFunction.getLowerBoundaries()[1];
        y2 = testFunction.getUpperBoundaries()[1];
        updateFuncValuesRange();

        if (yAxisCanvas != null) {
            yAxisCanvas.setWidth(axesMarkerLength + axesGapBetweenMarkerAndText + getLongestYTextWidth());
        }
    }

    public void drawAll() {
        drawFunc();
        drawAxes();
        if (currentIterBees != null) {
            drawBees(currentIterBees);
        }
    }
}
