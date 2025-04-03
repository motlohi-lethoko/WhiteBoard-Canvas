package motlohi.demo1;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.*;

public class TextDialog {
    private final Dialog<String> dialog;
    private final DrawingCanvas drawingCanvas;

    public TextDialog(DrawingCanvas drawingCanvas) {
        this.drawingCanvas = drawingCanvas;
        this.dialog = new Dialog<>();

        initializeDialog();
    }

    private void initializeDialog() {
        dialog.setTitle("Add Text");
        dialog.setHeaderText("Enter your text (supports multiple lines):");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Type your text here...");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setPrefColumnCount(30);

        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().setStyle("-fx-background-color: #f8f9fa;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return textArea.getText();
            }
            return null;
        });

        setupAnimation();
    }

    private void setupAnimation() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), dialog.getDialogPane());
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        dialog.setOnShown(e -> scaleIn.play());
    }

    public void show() {
        dialog.showAndWait().ifPresent(text -> {
            GraphicsContext gc = drawingCanvas.getGraphicsContext();
            gc.setFill(drawingCanvas.getCurrentColor());
            gc.setFont(Font.font("Arial", drawingCanvas.getStrokeWidth() * 5));

            double startX = 50;
            double startY = 50;
            double lineHeight = drawingCanvas.getStrokeWidth() * 8;
            double maxWidth = drawingCanvas.getCanvas().getWidth() - startX - 20;

            String[] paragraphs = text.split("\n");

            for (String paragraph : paragraphs) {
                String[] words = paragraph.split(" ");
                StringBuilder currentLine = new StringBuilder();
                double currentY = startY;

                for (String word : words) {
                    String testLine = currentLine + (currentLine.length() > 0 ? " " : "") + word;
                    double testWidth = computeTextWidth(testLine, gc.getFont());

                    if (testWidth <= maxWidth) {
                        currentLine.append(currentLine.length() > 0 ? " " : "").append(word);
                    } else {
                        if (currentLine.length() > 0) {
                            gc.fillText(currentLine.toString(), startX, currentY);
                            currentY += lineHeight;
                        }
                        currentLine = new StringBuilder(word);
                    }
                }

                if (currentLine.length() > 0) {
                    gc.fillText(currentLine.toString(), startX, currentY);
                }

                startY = currentY + lineHeight * 1.5;
            }

            drawingCanvas.saveCanvasState();
        });
    }

    private double computeTextWidth(String text, Font font) {
        Text tempText = new Text(text);
        tempText.setFont(font);
        return tempText.getLayoutBounds().getWidth();
    }
}