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

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class FunctionChart2D extends GridPane {

    private static final double BEE_SIZE = 10.0;
    private AbstractTestFunction testFunction;
    private final Canvas chartCanvas = new Canvas();
    private final Canvas beesCanvas = new Canvas();
    private final FunctionChartScale functionChartScale;
    private final FunctionChartAxes functionChartAxes;
    private final Canvas xAxisCanvas, yAxisCanvas, scaleCanvas, scaleAxisCanvas;
    private final PixelWriter pixelWriter;
    private double[] funcMinValuePos = new double[2];
    private double funcMinValue = Double.MAX_VALUE;
    private double funcMaxValue = -Double.MAX_VALUE;
    private double x1, x2, y1, y2; // Function's args range
    private double chartCanvasWidth, chartCanvasHeight;

    // -------------------------------BEES-------------------------------
    private double[][] currentIterBees;

    public FunctionChart2D(AbstractTestFunction testFunction) {
        setTestFunction(testFunction);
        initCanvases();
        pixelWriter = chartCanvas.getGraphicsContext2D().getPixelWriter();
        functionChartAxes = new FunctionChartAxes(chartCanvas, y1, y2);
        xAxisCanvas = functionChartAxes.getXAxisCanvas();
        yAxisCanvas = functionChartAxes.getYAxisCanvas();
        functionChartScale = new FunctionChartScale(chartCanvas, funcMinValue, funcMaxValue, testFunction.isChartInLogScale());
        scaleCanvas = functionChartScale.getScaleCanvas();
        scaleAxisCanvas = functionChartScale.getScaleAxisCanvas();

        initGridPane();

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

        GridPane.setMargin(scaleCanvas, new Insets(0, 0, 0, FunctionChartScale.SCALE_CANVAS_LEFT_MARGIN));
    }

    private void initCanvases() {
        chartCanvas.widthProperty().bind(chartCanvas.heightProperty()); // chartCanvas is square
        chartCanvas.heightProperty().bind(heightProperty().multiply(0.9));
        beesCanvas.widthProperty().bind(chartCanvas.widthProperty());
        beesCanvas.heightProperty().bind(chartCanvas.heightProperty());
        chartCanvasWidth = chartCanvas.getWidth();
        chartCanvasHeight = chartCanvas.getHeight();
    }

    private void drawFunc() {
        double funcVal;
        for (int xCanvas = 0; xCanvas < chartCanvasWidth; xCanvas++) {
            for (int yCanvas = 0; yCanvas < chartCanvasHeight; yCanvas++) {
                funcVal = getFuncVal(xCanvas, yCanvas);
                Color color = functionChartScale.getColorFromFuncValue(funcVal, funcMinValue, funcMaxValue);
                pixelWriter.setColor(xCanvas, yCanvas, color);
            }
        }
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

    public void clearBees() {
        currentIterBees = null;
        beesCanvas.getGraphicsContext2D().clearRect(0, 0, beesCanvas.getWidth(), beesCanvas.getHeight());
    }

    private void updateFuncValuesRange() {
        funcMinValue = Double.MAX_VALUE;
        funcMaxValue = -Double.MAX_VALUE;

        int ccw = (int) chartCanvasWidth;
        int cch = (int) chartCanvasHeight;
        double[] funcValues = new double[cch * ccw];

        IntStream.range(0, cch).parallel().forEach(yCanvas ->
                IntStream.range(0, ccw).parallel().forEach(xCanvas ->
                        funcValues[yCanvas * ccw + xCanvas] = getFuncVal(xCanvas, yCanvas) // Might be f(x, y) or log10(f(x, y))
                ));

        // Find min value index and max value
        OptionalInt maybeMinValueIdx = IntStream.range(0, funcValues.length).parallel().reduce((a, b) -> funcValues[a] < funcValues[b] ? a : b);
        OptionalDouble maybeMaxValue = Arrays.stream(funcValues).reduce(Double::max);

        if (maybeMinValueIdx.isPresent() && maybeMaxValue.isPresent()) {
            int minValueIdx = maybeMinValueIdx.getAsInt();

            int xMinCanvas = minValueIdx % ccw;
            int yMinCanvas = minValueIdx / ccw;

            funcMinValue = funcValues[minValueIdx];
            funcMinValuePos = getFuncXY(xMinCanvas, yMinCanvas);
            funcMaxValue = maybeMaxValue.getAsDouble();

            double funcVal;

            // Find more exact min value
            for (double xCanvas = xMinCanvas - 2.0; xCanvas <= xMinCanvas + 2.0; xCanvas += 0.02) {
                for (double yCanvas = yMinCanvas - 2.0; yCanvas <= yMinCanvas + 2.0; yCanvas += 0.02) {
                    try {
                        funcVal = getFuncVal(xCanvas, yCanvas); // Might be f(x, y) or log10(f(x, y))
                    } catch (IllegalArgumentException e) {
                        continue; // Coords are out of boundaries
                    }

                    if (funcVal < funcMinValue) {
                        funcMinValue = funcVal;
                        funcMinValuePos = getFuncXY(xCanvas, yCanvas);
                    }
                }
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
            functionChartAxes.updateYAxisCanvasWidth(y1, y2);
        }
        if (scaleAxisCanvas != null) {
            functionChartScale.updateScaleAxisCanvasWidth(funcMinValue, funcMaxValue, testFunction.isChartInLogScale());
        }
    }

    public void drawAll() {
        drawFunc();
        functionChartAxes.drawAxes(x1, x2, y1, y2);
        functionChartScale.drawScale(funcMinValue, funcMaxValue);
        functionChartScale.drawScaleAxis(funcMinValue, funcMaxValue, testFunction.isChartInLogScale());
        if (currentIterBees != null) {
            drawBees(currentIterBees);
        }
    }
}
