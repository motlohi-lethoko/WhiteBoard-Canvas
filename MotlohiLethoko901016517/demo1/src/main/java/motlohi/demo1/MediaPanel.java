package motlohi.demo1;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.Cursor;

import java.io.File;

public class MediaPanel {
    private final VBox mediaBox;
    private final DrawingCanvas drawingCanvas;

    public MediaPanel(DrawingCanvas drawingCanvas) {
        this.drawingCanvas = drawingCanvas;
        this.mediaBox = new VBox(10);

        initializeMediaBox();
    }

    private void initializeMediaBox() {
        mediaBox.setPadding(new Insets(10));
        mediaBox.setStyle("-fx-border-color: #ccc; -fx-background-color: rgba(255,255,255,0.9); -fx-padding: 10px; -fx-border-radius: 10px;");
        mediaBox.setPrefWidth(250);
        animateMediaBox();
    }

    public void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image img = new Image(file.toURI().toString());
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(200);
            imgView.setPreserveRatio(true);

            setupImageHoverEffect(imgView);
            setupImageDragHandlers(imgView, img);

            mediaBox.getChildren().add(imgView);
        }
    }

    public void addVideo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Video");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv", "*.mov")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(250);
            mediaView.setPreserveRatio(true);

            Button playButton = createStyledButton("Play", "#2ecc71");
            Button stopButton = createStyledButton("Stop", "#e74c3c");
            HBox videoControls = new HBox(5, playButton, stopButton);

            playButton.setOnAction(e -> mediaPlayer.play());
            stopButton.setOnAction(e -> mediaPlayer.pause());

            VBox videoContainer = new VBox(5, mediaView, videoControls);
            styleMediaContainer(videoContainer);

            mediaBox.getChildren().add(videoContainer);
            mediaPlayer.play();
        }
    }

    public void addMusic() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Music File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac", "*.ogg")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            Button playButton = createStyledButton("Play", "#2ecc71");
            Button pauseButton = createStyledButton("Pause", "#f39c12");
            Button stopButton = createStyledButton("Stop", "#e74c3c");
            Slider volumeSlider = new Slider(0, 1, 0.5);
            volumeSlider.valueProperty().bindBidirectional(mediaPlayer.volumeProperty());

            HBox musicControls = new HBox(5, playButton, pauseButton, stopButton, new Label("Vol:"), volumeSlider);

            Label musicLabel = new Label(file.getName());
            musicLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

            VBox musicContainer = new VBox(5, musicLabel, musicControls);
            styleMediaContainer(musicContainer);

            playButton.setOnAction(e -> mediaPlayer.play());
            pauseButton.setOnAction(e -> mediaPlayer.pause());
            stopButton.setOnAction(e -> {
                mediaPlayer.stop();
                mediaPlayer.seek(Duration.ZERO);
            });

            mediaBox.getChildren().add(musicContainer);
        }
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-padding: 10px 15px; -fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; -fx-cursor: hand;");
        return button;
    }

    private void styleMediaContainer(VBox container) {
        container.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5px; -fx-border-radius: 5px;");
        container.setCursor(Cursor.HAND);
        container.setOnMouseEntered(e -> {
            container.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px; -fx-border-radius: 5px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 0, 0);");
        });
        container.setOnMouseExited(e -> {
            container.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5px; -fx-border-radius: 5px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.5, 0, 0);");
        });
    }

    private void setupImageHoverEffect(ImageView imgView) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), imgView);
        imgView.setCursor(Cursor.HAND);
        imgView.setOnMouseEntered(e -> {
            st.setToX(1.05);
            st.setToY(1.05);
            st.playFromStart();
            imgView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0.5, 0, 0);");
        });
        imgView.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
            imgView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");
        });
    }

    private void setupImageDragHandlers(ImageView imgView, Image img) {
        imgView.setOnDragDetected(event -> {
            Dragboard db = imgView.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putImage(img);
            db.setContent(content);
            event.consume();
        });

        drawingCanvas.getCanvas().setOnDragOver(event -> {
            if (event.getGestureSource() != drawingCanvas.getCanvas() && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        drawingCanvas.getCanvas().setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                double x = event.getX() - db.getImage().getWidth() / 2;
                double y = event.getY() - db.getImage().getHeight() / 2;
                drawingCanvas.getGraphicsContext().drawImage(db.getImage(), x, y);
                success = true;
                drawingCanvas.saveCanvasState();
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void animateMediaBox() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), mediaBox);
        tt.setFromX(50);
        tt.setToX(0);
        tt.play();

        mediaBox.setOnMouseEntered(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), mediaBox);
            ft.setFromValue(0.9);
            ft.setToValue(1.0);
            ft.play();
            mediaBox.setStyle("-fx-border-color: #aaa; -fx-background-color: rgba(255,255,255,0.95); " +
                    "-fx-padding: 10px; -fx-border-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 0);");
        });

        mediaBox.setOnMouseExited(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), mediaBox);
            ft.setFromValue(1.0);
            ft.setToValue(0.9);
            ft.play();
            mediaBox.setStyle("-fx-border-color: #ccc; -fx-background-color: rgba(255,255,255,0.9); " +
                    "-fx-padding: 10px; -fx-border-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.5, 0, 0);");
        });
    }

    public VBox getMediaBox() {
        return mediaBox;
    }
}