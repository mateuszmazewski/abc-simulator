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

    private static final double FOOD_SOURCE_SIZE = 10.0;
    private static final double MINIMUM_MARK_SIZE = 12.0;

    private final Canvas chartCanvas = new Canvas();
    private final Canvas foodSourcesCanvas = new Canvas();
    private final FunctionChartScale functionChartScale;
    private final FunctionChartAxes functionChartAxes;
    private final Canvas xAxisCanvas, yAxisCanvas, scaleCanvas, scaleAxisCanvas;
    private double chartCanvasWidth, chartCanvasHeight;

    // ---------------------------FOOD SOURCES---------------------------
    private double[][] currentIterFoodSources;

    // -----------------------------FUNCTION-----------------------------
    private AbstractTestFunction testFunction;
    private double x1, x2, y1, y2; // Function's args range
    private double[] funcMinValuePos = new double[2];
    private double funcMinValue = Double.MAX_VALUE;
    private double funcMaxValue = -Double.MAX_VALUE;
    double[] funcValues;

    public FunctionChart2D(AbstractTestFunction testFunction) {
        setTestFunction(testFunction);
        initCanvases();
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
            updateFuncValues();
            drawAll();
        };

        widthProperty().addListener(paneSizeListener);
        heightProperty().addListener(paneSizeListener);
    }

    private void initGridPane() {
        getChildren().addAll(chartCanvas, foodSourcesCanvas, xAxisCanvas, yAxisCanvas, scaleCanvas, scaleAxisCanvas);
        GridPane.setHalignment(xAxisCanvas, HPos.RIGHT);
        GridPane.setValignment(yAxisCanvas, VPos.TOP);
        GridPane.setConstraints(chartCanvas, 1, 0, 1, 1);
        GridPane.setConstraints(foodSourcesCanvas, 1, 0, 1, 1);
        GridPane.setConstraints(xAxisCanvas, 0, 1, 2, 1);
        GridPane.setConstraints(yAxisCanvas, 0, 0, 1, 2);
        GridPane.setConstraints(scaleCanvas, 2, 0, 1, 1);
        GridPane.setConstraints(scaleAxisCanvas, 3, 0, 1, 1);

        GridPane.setMargin(scaleCanvas, new Insets(0, 0, 0, FunctionChartScale.SCALE_CANVAS_LEFT_MARGIN));
    }

    private void initCanvases() {
        chartCanvas.widthProperty().bind(chartCanvas.heightProperty()); // chartCanvas is square
        chartCanvas.heightProperty().bind(heightProperty().multiply(0.95));
        foodSourcesCanvas.widthProperty().bind(chartCanvas.widthProperty());
        foodSourcesCanvas.heightProperty().bind(chartCanvas.heightProperty());
        chartCanvasWidth = chartCanvas.getWidth();
        chartCanvasHeight = chartCanvas.getHeight();
    }

    private void drawFunc() {
        double funcVal;
        int cch = (int) chartCanvasHeight;
        int ccw = (int) chartCanvasWidth;
        PixelWriter pixelWriter = chartCanvas.getGraphicsContext2D().getPixelWriter();

        for (int yCanvas = 0; yCanvas < cch; yCanvas++) {
            for (int xCanvas = 0; xCanvas < ccw; xCanvas++) {
                funcVal = funcValues[yCanvas * ccw + xCanvas];
                Color color = functionChartScale.getColorFromFuncValue(funcVal, funcMinValue, funcMaxValue);
                pixelWriter.setColor(xCanvas, yCanvas, color);
            }
        }
    }

    public void drawFoodSources(double[][] foodSources) {
        currentIterFoodSources = foodSources; // In case user resizes the window, we need to draw food sources again
        double[] canvasXY;
        double offset = FOOD_SOURCE_SIZE / 2.0; // Food source's position is the circle's center (not upper-left vertex)
        GraphicsContext foodSourcesGraphics = foodSourcesCanvas.getGraphicsContext2D();
        foodSourcesGraphics.setFill(Color.WHITE);
        foodSourcesCanvas.getGraphicsContext2D().clearRect(0, 0, foodSourcesCanvas.getWidth(), foodSourcesCanvas.getHeight());

        for (double[] foodSource : foodSources) {
            canvasXY = getCanvasXY(foodSource);
            foodSourcesGraphics.fillOval(canvasXY[0] - offset, canvasXY[1] - offset, FOOD_SOURCE_SIZE, FOOD_SOURCE_SIZE);
        }
    }

    private void drawMinimum() {
        GraphicsContext funcGraphics = chartCanvas.getGraphicsContext2D();
        funcGraphics.setStroke(Color.RED);
        funcGraphics.setLineWidth(2.0);

        double[] canvasXY = getCanvasXY(funcMinValuePos);
        double offset = MINIMUM_MARK_SIZE / 2.0;

        funcGraphics.strokeLine(canvasXY[0] - offset, canvasXY[1] - offset, canvasXY[0] + offset, canvasXY[1] + offset);
        funcGraphics.strokeLine(canvasXY[0] + offset, canvasXY[1] - offset, canvasXY[0] - offset, canvasXY[1] + offset);
    }

    public void clearFoodSources() {
        currentIterFoodSources = null;
        foodSourcesCanvas.getGraphicsContext2D().clearRect(0, 0, foodSourcesCanvas.getWidth(), foodSourcesCanvas.getHeight());
    }

    private void updateFuncValues() {
        int cch = (int) chartCanvasHeight;
        int ccw = (int) chartCanvasWidth;
        funcValues = new double[cch * ccw];
        funcMinValue = Double.MAX_VALUE;
        funcMaxValue = -Double.MAX_VALUE;

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
        updateFuncValues();

        if (yAxisCanvas != null) {
            functionChartAxes.updateYAxisCanvasWidth(y1, y2);
        }
        if (scaleAxisCanvas != null) {
            functionChartScale.updateScaleAxisCanvasWidth(funcMinValue, funcMaxValue, testFunction.isChartInLogScale());
        }
    }

    public void drawAll() {
        drawFunc();
        drawMinimum();
        functionChartAxes.drawAxes(x1, x2, y1, y2);
        functionChartScale.drawScale(funcMinValue, funcMaxValue);
        functionChartScale.drawScaleAxis(funcMinValue, funcMaxValue, testFunction.isChartInLogScale());
        if (currentIterFoodSources != null) {
            drawFoodSources(currentIterFoodSources);
        }
    }
}
