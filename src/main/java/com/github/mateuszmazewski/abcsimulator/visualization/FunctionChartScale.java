package com.github.mateuszmazewski.abcsimulator.visualization;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.decimalFormat2;
import static com.github.mateuszmazewski.abcsimulator.visualization.FunctionChartAxes.*;

public class FunctionChartScale {

    public static final double SCALE_CANVAS_WIDTH = 15.0;
    public static final double SCALE_CANVAS_LEFT_MARGIN = 10.0;

    private final Canvas scaleCanvas = new Canvas();
    private final Canvas scaleAxisCanvas = new Canvas();

    public FunctionChartScale(Canvas chartCanvas, double funcMinValue, double funcMaxValue, boolean logScale) {
        scaleCanvas.setWidth(SCALE_CANVAS_WIDTH);
        scaleCanvas.heightProperty().bind(chartCanvas.heightProperty());

        scaleAxisCanvas.setWidth(getScaleAxisWidth(funcMinValue, funcMaxValue, logScale));
        scaleAxisCanvas.heightProperty().bind(scaleCanvas.heightProperty());

        scaleAxisCanvas.getGraphicsContext2D().setStroke(Color.BLACK);
        scaleAxisCanvas.getGraphicsContext2D().setFont(AXES_FONT);
    }

    public void drawScale(double funcMinValue, double funcMaxValue) {
        int scaleCanvasWidth = (int) scaleCanvas.getWidth();
        int scaleCanvasHeight = (int) scaleCanvas.getHeight();
        PixelWriter pixelWriter = scaleCanvas.getGraphicsContext2D().getPixelWriter();
        double funcValRange = funcMaxValue - funcMinValue;
        double funcVal;

        for (int yCanvas = 0; yCanvas < scaleCanvasHeight; yCanvas++) {
            funcVal = funcMinValue + (scaleCanvasHeight - yCanvas) * funcValRange / (scaleCanvasHeight - 1);

            Color color = getColorFromFuncValue(funcVal, funcMinValue, funcMaxValue);
            for (int xCanvas = 0; xCanvas < scaleCanvasWidth; xCanvas++) {
                pixelWriter.setColor(xCanvas, yCanvas, color);
            }
        }
    }

    public Color getColorFromFuncValue(double funcVal, double funcMinValue, double funcMaxValue) {
        // Scale func. values to [0, 240] ->  HSV color space
        // 0 deg. = red, 120 deg. = green, 240 deg. = blue
        double funcValScaled = (funcVal - funcMinValue) / (funcMaxValue - funcMinValue) * 240;
        return Color.hsb(240.0 - funcValScaled, 1.0, 1.0);
    }

    public void drawScaleAxis(double funcMinValue, double funcMaxValue, boolean logScale) {
        int stepsCount = logScale ? (int) scaleAxisCanvas.getHeight() : 10;
        double step = scaleAxisCanvas.getHeight() / stepsCount;
        double funcValStep = (funcMaxValue - funcMinValue) / stepsCount;

        GraphicsContext scaleAxisGraphics = scaleAxisCanvas.getGraphicsContext2D();
        double y, funcVal;
        String text;
        long exponent;
        List<Long> addedExponents = new ArrayList<>();

        scaleAxisGraphics.clearRect(0, 0, scaleAxisCanvas.getWidth(), scaleAxisCanvas.getHeight());

        for (int i = 0; i <= stepsCount; i++) {
            funcVal = funcMinValue + (stepsCount - i) * funcValStep;

            if (logScale) {
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

            scaleAxisGraphics.strokeLine(0, y, AXES_MARKER_LENGTH, y);
            scaleAxisGraphics.fillText(text, AXES_MARKER_LENGTH + AXES_GAP_BETWEEN_MARKER_AND_TEXT, y);
        }
    }

    private double getScaleAxisWidth(double funcMinValue, double funcMaxValue, boolean logScale) {
        Text longestScaleAxisText;
        if (logScale) {
            longestScaleAxisText = new Text("10^(-99)");
        } else {
            double longestNumberOnScaleAxis = -((int) Math.max(Math.abs(funcMinValue), Math.abs(funcMaxValue)) + 0.77);
            longestScaleAxisText = new Text(String.valueOf(longestNumberOnScaleAxis));
        }
        longestScaleAxisText.setFont(AXES_FONT);
        return longestScaleAxisText.getLayoutBounds().getWidth() + AXES_MARKER_LENGTH + AXES_GAP_BETWEEN_MARKER_AND_TEXT;
    }

    public void updateScaleAxisCanvasWidth(double funcMinValue, double funcMaxValue, boolean logScale) {
        scaleAxisCanvas.setWidth(getScaleAxisWidth(funcMinValue, funcMaxValue, logScale));
    }

    public Canvas getScaleCanvas() {
        return scaleCanvas;
    }

    public Canvas getScaleAxisCanvas() {
        return scaleAxisCanvas;
    }

}
