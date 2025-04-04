package motlohi.demo1;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;

public class ToolBar {
    private final HBox toolbar;
    private final DrawingCanvas drawingCanvas;
    private final MediaPanel mediaPanel;

    public ToolBar(DrawingCanvas drawingCanvas, MediaPanel mediaPanel) {
        this.drawingCanvas = drawingCanvas;
        this.mediaPanel = mediaPanel;
        this.toolbar = new HBox(10);

        initializeToolbar();
    }

    private void initializeToolbar() {
        toolbar.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-padding: 15px; -fx-alignment: center; -fx-background-radius: 10px;");

        ColorPicker colorPicker = createColorPicker();
        Slider thicknessSlider = createThicknessSlider();

        // Drawing tools
        Button eraserButton = createToolButton("Eraser", "#e74c3c", e -> drawingCanvas.toggleEraser());
        Button textButton = createToolButton("Add Text", "#3498db", e -> new TextDialog(drawingCanvas).show());
        Button clearButton = createToolButton("Clear", "#f39c12", e -> drawingCanvas.clearCanvas());

        // Media tools
        Button imageButton = createToolButton("Add Image", "#9b59b6", e -> mediaPanel.addImage());
        Button videoButton = createToolButton("Add Video", "#1abc9c", e -> mediaPanel.addVideo());
        Button musicButton = createToolButton("Add Music", "#e67e22", e -> mediaPanel.addMusic());

        // History tools
        Button undoButton = createToolButton("Undo", "#34495e", e -> drawingCanvas.undo());
        Button redoButton = createToolButton("Redo", "#7f8c8d", e -> drawingCanvas.redo());

        // File operations
        Button saveButton = createToolButton("Save", "#27ae60", e -> saveCanvasToFile());

        toolbar.getChildren().addAll(
                colorPicker,
                new Label("Thickness:"), thicknessSlider,
                eraserButton, textButton, imageButton,
                videoButton, musicButton, clearButton,
                undoButton, redoButton, saveButton
        );
    }

    private ColorPicker createColorPicker() {
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setOnAction(e -> drawingCanvas.setCurrentColor(colorPicker.getValue()));
        animateColorPicker(colorPicker);
        return colorPicker;
    }

    private Slider createThicknessSlider() {
        Slider slider = new Slider(1, 20, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(5);
        slider.setMinorTickCount(4);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            drawingCanvas.setStrokeWidth(newVal.doubleValue());
            drawingCanvas.getGraphicsContext().setLineWidth(newVal.doubleValue());
        });
        return slider;
    }

    private Button createToolButton(String text, String color, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-padding: 10px 15px; -fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; -fx-cursor: hand;");
        button.setOnAction(handler);
        addHoverEffect(button, color);
        return button;
    }

    private void addHoverEffect(Button button, String baseColor) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        button.setOnMouseEntered(e -> {
            st.setToX(1.05);
            st.setToY(1.05);
            st.playFromStart();
            button.setStyle("-fx-background-color: derive(" + baseColor + ", 20%); -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 15px; " +
                    "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");
        });
        button.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
            button.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 15px; " +
                    "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");
        });
    }

    private void animateColorPicker(ColorPicker picker) {
        picker.setOnMouseEntered(e -> {
            RotateTransition rt = new RotateTransition(Duration.millis(200), picker);
            rt.setByAngle(5);
            rt.setCycleCount(4);
            rt.setAutoReverse(true);
            rt.play();
        });
    }

    private void saveCanvasToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Whiteboard");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Image", "*.jpg"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            // In a real application, we would implement actual saving functionality
            System.out.println("Whiteboard saved to: " + file.getAbsolutePath());
        }
    }

    public HBox getToolbar() {
        return toolbar;
    }
}