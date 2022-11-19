package com.github.mateuszmazewski.abcsimulator.visualization;

import com.github.mateuszmazewski.abcsimulator.abc.testfunctions.AbstractTestFunction;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.decimalFormat2;

public class FunctionChart2D extends GridPane {

    private static final double SCALE_CANVAS_WIDTH = 15.0;
    private static final double SCALE_CANVAS_LEFT_MARGIN = 10.0;
    private static final double BEE_SIZE = 10.0;
    private AbstractTestFunction testFunction;
    private final Canvas chartCanvas = new Canvas();
    private final Canvas beesCanvas = new Canvas();
    private final Canvas scaleCanvas = new Canvas();
    private final Canvas xAxisCanvas = new Canvas();
    private final Canvas yAxisCanvas = new Canvas();
    private final Canvas scaleAxisCanvas = new Canvas();
    private final PixelWriter pixelWriter;
    private double[] funcMinValuePos = new double[2];
    private double funcMinValue = Double.MAX_VALUE;
    private double funcMaxValue = -Double.MAX_VALUE;
    private double x1, x2, y1, y2; // Function's args range
    private double chartCanvasWidth, chartCanvasHeight;

    // -------------------------AXES' PARAMETERS-------------------------
    private final double axesFontSize = 12.0;
    private final Font axesFont = new Font(axesFontSize);
    private final double axesMarkerLength = 8.0;
    private final double axesGapBetweenMarkerAndText = 5.0;

    // -------------------------------BEES-------------------------------
    private double[][] currentIterBees;

    public FunctionChart2D(AbstractTestFunction testFunction) {
        setTestFunction(testFunction);
        initCanvases();
        pixelWriter = chartCanvas.getGraphicsContext2D().getPixelWriter();
        initGridPane();
        drawAll();

        ChangeListener<Number> paneSizeListener = (observable, oldValue, newValue) -> {
            chartCanvasWidth = chartCanvas.getWidth();
            chartCanvasHeight = chartCanvas.getHeight();
            updateFuncValuesRange();
            drawAll();
        };

        widthProperty().addListener(paneSizeListener);
        heightProperty().addListener(paneSizeListener);
    }

    private void initGridPane() {
        getChildren().addAll(chartCanvas, beesCanvas, xAxisCanvas, yAxisCanvas, scaleCanvas, scaleAxisCanvas);
        GridPane.setHalignment(xAxisCanvas, HPos.RIGHT);
        GridPane.setValignment(yAxisCanvas, VPos.TOP);
        GridPane.setConstraints(chartCanvas, 1, 0, 1, 1);
        GridPane.setConstraints(beesCanvas, 1, 0, 1, 1);
        GridPane.setConstraints(xAxisCanvas, 0, 1, 2, 1);
        GridPane.setConstraints(yAxisCanvas, 0, 0, 1, 2);
        GridPane.setConstraints(scaleCanvas, 2, 0, 1, 1);
        GridPane.setConstraints(scaleAxisCanvas, 3, 0, 1, 1);

        GridPane.setMargin(scaleCanvas, new Insets(0, 0, 0, SCALE_CANVAS_LEFT_MARGIN));
    }

    private void initCanvases() {
        chartCanvas.widthProperty().bind(chartCanvas.heightProperty()); // chartCanvas is square
        chartCanvas.heightProperty().bind(heightProperty().multiply(0.9));
        beesCanvas.widthProperty().bind(chartCanvas.widthProperty());
        beesCanvas.heightProperty().bind(chartCanvas.heightProperty());
        chartCanvasWidth = chartCanvas.getWidth();
        chartCanvasHeight = chartCanvas.getHeight();

        xAxisCanvas.widthProperty().bind(chartCanvas.widthProperty());
        yAxisCanvas.heightProperty().bind(chartCanvas.heightProperty());
        xAxisCanvas.setHeight(axesMarkerLength + axesFontSize + axesGapBetweenMarkerAndText);
        yAxisCanvas.setWidth(getYAxisWidth());

        scaleCanvas.setWidth(SCALE_CANVAS_WIDTH);
        scaleCanvas.heightProperty().bind(chartCanvas.heightProperty());

        scaleAxisCanvas.setWidth(getScaleAxisWidth());
        scaleAxisCanvas.heightProperty().bind(scaleCanvas.heightProperty());

        initCanvas(xAxisCanvas);
        initCanvas(yAxisCanvas);
        initCanvas(scaleCanvas);
        initCanvas(scaleAxisCanvas);
    }

    private void initCanvas(Canvas canvas) {
        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().setFont(axesFont);
    }

    private double getYAxisWidth() {
        // Longest number on y-axis is a negative number with two digits after decimal point
        double longestNumberOnYAxis = -((int) Math.max(Math.abs(y1), Math.abs(y2)) + 0.77);
        Text longestYText = new Text(String.valueOf(longestNumberOnYAxis));
        longestYText.setFont(axesFont);
        return longestYText.getLayoutBounds().getWidth() + axesMarkerLength + axesGapBetweenMarkerAndText;
    }

    private double getScaleAxisWidth() {
        Text longestScaleAxisText;
        if (testFunction.isChartInLogScale()) {
            longestScaleAxisText = new Text("10^(-99)");
        } else {
            double longestNumberOnScaleAxis = -((int) Math.max(Math.abs(funcMinValue), Math.abs(funcMaxValue)) + 0.77);
            longestScaleAxisText = new Text(String.valueOf(longestNumberOnScaleAxis));
        }
        longestScaleAxisText.setFont(axesFont);
        return longestScaleAxisText.getLayoutBounds().getWidth() + axesMarkerLength + axesGapBetweenMarkerAndText;
    }

    private void drawAxes() {
        int stepsCount = 10;
        double xAxisStep = xAxisCanvas.getWidth() / stepsCount;
        double yAxisStep = yAxisCanvas.getHeight() / stepsCount;
        double xFuncStep = (x2 - x1) / stepsCount;
        double yFuncStep = (y2 - y1) / stepsCount;
        GraphicsContext xAxisGraphics = xAxisCanvas.getGraphicsContext2D();
        GraphicsContext yAxisGraphics = yAxisCanvas.getGraphicsContext2D();
        double x, y;
        String xText, yText;

        xAxisGraphics.clearRect(0, 0, xAxisCanvas.getWidth(), xAxisCanvas.getHeight());
        yAxisGraphics.clearRect(0, 0, yAxisCanvas.getWidth(), yAxisCanvas.getHeight());

        for (int i = 0; i <= stepsCount; i++) {
            xText = decimalFormat2.format(x1 + i * xFuncStep);
            yText = decimalFormat2.format(y1 + (stepsCount - i) * yFuncStep);

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
        double funcVal;
        for (int xCanvas = 0; xCanvas < chartCanvasWidth; xCanvas++) {
            for (int yCanvas = 0; yCanvas < chartCanvasHeight; yCanvas++) {
                funcVal = getFuncVal(xCanvas, yCanvas);
                Color color = getColorFromFuncValue(funcVal);
                pixelWriter.setColor(xCanvas, yCanvas, color);
            }
        }
    }

    private Color getColorFromFuncValue(double funcVal) {
        // Scale func. values to [0, 240] ->  HSV color space
        // 0 deg. = red, 120 deg. = green, 240 deg. = blue
        double funcValScaled = (funcVal - funcMinValue) / (funcMaxValue - funcMinValue) * 240;
        return Color.hsb(240.0 - funcValScaled, 1.0, 1.0);
    }

    public void drawBees(double[][] foodSources) {
        currentIterBees = foodSources; // In case user resizes the window, we need to draw bees again
        double[] canvasXY;
        double offset = BEE_SIZE / 2.0; // Bee's position is the circle's center (not upper-left vertex)
        GraphicsContext beesGraphics = beesCanvas.getGraphicsContext2D();
        beesGraphics.setFill(Color.WHITE);
        beesCanvas.getGraphicsContext2D().clearRect(0, 0, beesCanvas.getWidth(), beesCanvas.getHeight());

        for (double[] foodSource : foodSources) {
            canvasXY = getCanvasXY(foodSource);
            beesGraphics.fillOval(canvasXY[0] - offset, canvasXY[1] - offset, BEE_SIZE, BEE_SIZE);
        }
    }

    private void drawScale() {
        int scaleCanvasWidth = (int) scaleCanvas.getWidth();
        int scaleCanvasHeight = (int) scaleCanvas.getHeight();
        PixelWriter scalePixelWriter = scaleCanvas.getGraphicsContext2D().getPixelWriter();
        double funcValRange = funcMaxValue - funcMinValue;
        double funcVal;

        for (int yCanvas = 0; yCanvas < scaleCanvasHeight; yCanvas++) {
            //funcVal = funcValRange / scaleCanvasHeight * (scaleCanvasHeight - yCanvas);
            funcVal = funcMinValue + (scaleCanvasHeight - yCanvas) * funcValRange / (scaleCanvasHeight - 1);

            Color color = getColorFromFuncValue(funcVal);
            for (int xCanvas = 0; xCanvas < scaleCanvasWidth; xCanvas++) {
                scalePixelWriter.setColor(xCanvas, yCanvas, color);
            }
        }
    }

    private void drawScaleAxis() {
        int stepsCount;
        double step, funcValStep;
        if (testFunction.isChartInLogScale()) {
            stepsCount = (int) scaleAxisCanvas.getHeight();
        } else {
            stepsCount = 10;
        }
        step = scaleAxisCanvas.getHeight() / stepsCount;
        funcValStep = (funcMaxValue - funcMinValue) / stepsCount;
        GraphicsContext scaleAxisGraphics = scaleAxisCanvas.getGraphicsContext2D();
        double y, funcVal;
        String text;
        long exponent;
        List<Long> addedExponents = new ArrayList<>();

        scaleAxisGraphics.clearRect(0, 0, scaleAxisCanvas.getWidth(), scaleAxisCanvas.getHeight());

        for (int i = 0; i <= stepsCount; i++) {
            funcVal = funcMinValue + (stepsCount - i) * funcValStep;

            if (testFunction.isChartInLogScale()) {
                // Find only integer exponents
                if (Math.abs(funcVal % 1) < 0.05) {
                    exponent = Math.round(funcVal);
                    if (!addedExponents.contains(exponent)) {
                        addedExponents.add(exponent);
                        text = "10^" + (exponent >= 0 ? exponent : "(" + exponent + ")");
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            } else {
                text = decimalFormat2.format(funcVal);
            }

            scaleAxisGraphics.setTextAlign(TextAlignment.LEFT);

            // Determine line's position to avoid antialiasing
            // Determine text alignment/baseline to fit text on canvas
            if (i == 0) {
                // first marker
                y = Math.round(i * step) + 0.5;
                scaleAxisGraphics.setTextBaseline(VPos.TOP);
            } else if (i == stepsCount) {
                // last marker
                y = Math.round(i * step) - 0.5;
                scaleAxisGraphics.setTextBaseline(VPos.BOTTOM);
            } else {
                y = Math.round(i * step) + 0.5;
                scaleAxisGraphics.setTextBaseline(VPos.CENTER);
            }

            scaleAxisGraphics.strokeLine(0, y, axesMarkerLength, y);
            scaleAxisGraphics.fillText(text, axesMarkerLength + axesGapBetweenMarkerAndText, y);
        }
    }

    public void clearBees() {
        currentIterBees = null;
        beesCanvas.getGraphicsContext2D().clearRect(0, 0, beesCanvas.getWidth(), beesCanvas.getHeight());
    }

    private void updateFuncValuesRange() {
        funcMinValue = Double.MAX_VALUE;
        funcMaxValue = -Double.MAX_VALUE;

        for (double xCanvas = 0.0; xCanvas <= chartCanvasWidth; xCanvas += 0.2) {
            for (double yCanvas = chartCanvasHeight; yCanvas >= 0.0; yCanvas -= 0.2) {
                double funcVal = getFuncVal(xCanvas, yCanvas); // Might be f(x, y) or log10(f(x, y))
                if (funcVal < funcMinValue) {
                    funcMinValue = funcVal;
                    funcMinValuePos = getFuncXY(xCanvas, yCanvas);
                }
                funcMaxValue = Math.max(funcMaxValue, funcVal);
            }
        }

        testFunction.setMinValuePos(funcMinValuePos);
        testFunction.setMinValue(testFunction.getValue(funcMinValuePos)); // Make sure it's f(x, y), not log10(f(x, y))
    }

    private double getFuncVal(double xCanvas, double yCanvas) {
        double[] pos = getFuncXY(xCanvas, yCanvas);
        if (testFunction.isChartInLogScale()) {
            return testFunction.getLog10Value(pos);
        } else {
            return testFunction.getValue(pos);
        }
    }

    private double[] getCanvasXY(double[] xyFunc) {
        // Scale function coords to canvas coords
        double xCanvas = (xyFunc[0] - x1) / (x2 - x1) * chartCanvasWidth;
        double yCanvas = chartCanvasHeight - (xyFunc[1] - y1) / (y2 - y1) * chartCanvasHeight;
        return new double[]{xCanvas, yCanvas};
    }

    private double[] getFuncXY(double xCanvas, double yCanvas) {
        // Scale canvas coords to function coords
        double xFunc = xCanvas / chartCanvasWidth * (x2 - x1) + x1;
        double yFunc = (chartCanvasHeight - yCanvas) / chartCanvasHeight * (y2 - y1) + y1;
        return new double[]{xFunc, yFunc};
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
            yAxisCanvas.setWidth(getYAxisWidth());
        }
        if (scaleAxisCanvas != null) {
            scaleAxisCanvas.setWidth(getScaleAxisWidth());
        }
    }

    public void drawAll() {
        drawFunc();
        drawAxes();
        drawScale();
        drawScaleAxis();
        if (currentIterBees != null) {
            drawBees(currentIterBees);
        }
    }
}
