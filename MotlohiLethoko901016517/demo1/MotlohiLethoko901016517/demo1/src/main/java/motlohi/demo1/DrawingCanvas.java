package motlohi.demo1;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.Stack;

public class DrawingCanvas {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final StackPane canvasContainer;
    private final Stack<WritableImage> undoStack;
    private final Stack<WritableImage> redoStack;

    private Color currentColor;
    private double strokeWidth;
    private boolean eraserMode;
    private boolean isDrawingStraightLine;
    private double lastX, lastY;
    private double prevX, prevY; // For smooth drawing

    public DrawingCanvas() {
        this.canvas = new Canvas(950, 550);
        this.gc = canvas.getGraphicsContext2D();
        this.canvasContainer = new StackPane(canvas);
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        initializeCanvas();
        styleCanvasContainer();
    }

    private void initializeCanvas() {
        currentColor = Color.BLACK;
        strokeWidth = 3.0;
        eraserMode = false;
        isDrawingStraightLine = false;

        // Configure smoother stroke rendering
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        clearCanvas();
        setupDrawingHandlers();
    }

    private void styleCanvasContainer() {
        canvasContainer.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.5, 0, 1); -fx-cursor: hand;");
        canvasContainer.setPadding(new Insets(15));
    }

    public void clearCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        undoStack.clear();
        redoStack.clear();
        saveCanvasState();
    }

    private void setupDrawingHandlers() {
        canvas.setOnMousePressed(e -> {
            if (e.isShiftDown()) {
                isDrawingStraightLine = true;
                lastX = e.getX();
                lastY = e.getY();
                startDrawing(e);
            } else {
                isDrawingStraightLine = false;
                prevX = e.getX();
                prevY = e.getY();
                startDrawing(e);
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (e.isShiftDown()) {
                if (!isDrawingStraightLine) {
                    isDrawingStraightLine = true;
                    lastX = e.getX();
                    lastY = e.getY();
                }
                drawStraightLine(e);
            } else {
                isDrawingStraightLine = false;
                drawSmooth(e);
            }
        });

        canvas.setOnMouseReleased(e -> {
            saveCanvasState();
            isDrawingStraightLine = false;
        });
    }

    private void startDrawing(MouseEvent e) {
        gc.setStroke(eraserMode ? Color.WHITE : currentColor);
        gc.setLineWidth(strokeWidth);
        gc.beginPath();
        gc.moveTo(e.getX(), e.getY());
        gc.stroke();
    }

    private void drawSmooth(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        // Calculate control point (midpoint between previous and current position)
        double ctrlX = (x + prevX) / 2;
        double ctrlY = (y + prevY) / 2;

        // Draw quadratic Bézier curve
        gc.beginPath();
        gc.moveTo(prevX, prevY);
        gc.quadraticCurveTo(ctrlX, ctrlY, x, y);
        gc.stroke();

        // Store current position for next segment
        prevX = x;
        prevY = y;
    }

    private void drawStraightLine(MouseEvent e) {
        double newX = e.getX();
        double newY = e.getY();

        if (Math.abs(newX - lastX) > Math.abs(newY - lastY)) {
            newY = lastY;
        } else {
            newX = lastX;
        }

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (!undoStack.isEmpty()) {
            gc.drawImage(undoStack.peek(), 0, 0);
        }

        gc.beginPath();
        gc.moveTo(lastX, lastY);
        gc.lineTo(newX, newY);
        gc.stroke();
    }

    public void saveCanvasState() {
        WritableImage snapshot = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
        canvas.snapshot(null, snapshot);
        undoStack.push(snapshot);
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.size() > 1) {
            redoStack.push(undoStack.pop());
            WritableImage previousState = undoStack.peek();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(previousState, 0, 0);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            WritableImage nextState = redoStack.pop();
            undoStack.push(nextState);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(nextState, 0, 0);
        }
    }

    // Getters and setters
    public StackPane getCanvasContainer() {
        return canvasContainer;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setStrokeWidth(double width) {
        this.strokeWidth = width;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setEraserMode(boolean eraserMode) {
        this.eraserMode = eraserMode;
    }

    public boolean isEraserMode() {
        return eraserMode;
    }

    public void toggleEraser() {
        this.eraserMode = !this.eraserMode;
    }
}