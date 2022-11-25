package com.github.mateuszmazewski.abcsimulator.visualization;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static com.github.mateuszmazewski.abcsimulator.utils.MathUtils.decimalFormat2;

public class FunctionChartAxes {

    public static final double AXES_FONT_SIZE = 12.0;
    public static final Font AXES_FONT = new Font(AXES_FONT_SIZE);
    public static final double AXES_MARKER_LENGTH = 8.0;
    public static final double AXES_GAP_BETWEEN_MARKER_AND_TEXT = 5.0;

    private final Canvas xAxisCanvas = new Canvas();
    private final Canvas yAxisCanvas = new Canvas();

    public FunctionChartAxes(Canvas chartCanvas, double yMin, double yMax) {
        xAxisCanvas.widthProperty().bind(chartCanvas.widthProperty());
        yAxisCanvas.heightProperty().bind(chartCanvas.heightProperty());
        xAxisCanvas.setHeight(AXES_MARKER_LENGTH + AXES_FONT_SIZE + AXES_GAP_BETWEEN_MARKER_AND_TEXT);
        updateYAxisCanvasWidth(yMin, yMax);

        xAxisCanvas.getGraphicsContext2D().setStroke(Color.BLACK);
        xAxisCanvas.getGraphicsContext2D().setFont(AXES_FONT);
        yAxisCanvas.getGraphicsContext2D().setStroke(Color.BLACK);
        yAxisCanvas.getGraphicsContext2D().setFont(AXES_FONT);
    }

    private double getYAxisWidth(double y1, double y2) {
        // Longest number on y-axis is a negative number with two digits after decimal point
        double longestNumberOnYAxis = -((int) Math.max(Math.abs(y1), Math.abs(y2)) + 0.77);
        Text longestYText = new Text(String.valueOf(longestNumberOnYAxis));
        longestYText.setFont(AXES_FONT);
        return longestYText.getLayoutBounds().getWidth() + AXES_MARKER_LENGTH + AXES_GAP_BETWEEN_MARKER_AND_TEXT;
    }

    public void drawAxes(double x1, double x2, double y1, double y2) {
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

            xAxisGraphics.strokeLine(x, 0, x, AXES_MARKER_LENGTH);
            xAxisGraphics.fillText(xText, x, AXES_MARKER_LENGTH + AXES_FONT_SIZE + AXES_GAP_BETWEEN_MARKER_AND_TEXT);

            yAxisGraphics.strokeLine(yAxisCanvas.getWidth(), y, yAxisCanvas.getWidth() - AXES_MARKER_LENGTH, y);
            yAxisGraphics.fillText(yText, yAxisCanvas.getWidth() - (AXES_MARKER_LENGTH + AXES_GAP_BETWEEN_MARKER_AND_TEXT), y);
        }
    }

    public Canvas getXAxisCanvas() {
        return xAxisCanvas;
    }

    public Canvas getYAxisCanvas() {
        return yAxisCanvas;
    }

    public void updateYAxisCanvasWidth(double yMin, double yMax) {
        yAxisCanvas.setWidth(getYAxisWidth(yMin, yMax));
    }
}
