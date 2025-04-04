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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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

            // Create a snapshot of the video for dragging
            ImageView dragImageView = new ImageView();
            mediaView.setOnMousePressed(event -> {
                Image snapshot = mediaView.snapshot(null, null);
                dragImageView.setImage(snapshot);
                dragImageView.setFitWidth(mediaView.getFitWidth());
                dragImageView.setPreserveRatio(true);
            });

            Button playButton = createStyledButton("Play", "#2ecc71");
            Button stopButton = createStyledButton("Stop", "#e74c3c");
            HBox videoControls = new HBox(5, playButton, stopButton);

            playButton.setOnAction(e -> mediaPlayer.play());
            stopButton.setOnAction(e -> mediaPlayer.pause());

            VBox videoContainer = new VBox(5, mediaView, videoControls);
            styleMediaContainer(videoContainer);

            // Setup drag and drop for the video container
            setupVideoDragHandlers(videoContainer, mediaView, file.getName());

            mediaBox.getChildren().add(videoContainer);
            mediaPlayer.play();
        }
    }

    private void setupVideoDragHandlers(VBox videoContainer, MediaView mediaView, String fileName) {
        videoContainer.setOnDragDetected(event -> {
            // Create snapshot for dragging
            Image snapshot = mediaView.snapshot(null, null);

            Dragboard db = videoContainer.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putImage(snapshot);
            content.putString("VIDEO:" + mediaView.getMediaPlayer().getMedia().getSource() + ":" + fileName);
            db.setContent(content);

            // Create a drag view
            ImageView dragView = new ImageView(snapshot);
            dragView.setFitWidth(mediaView.getFitWidth());
            dragView.setPreserveRatio(true);
            db.setDragView(dragView.snapshot(null, null), event.getX(), event.getY());

            event.consume();
        });
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

            // Setup drag for music
            setupMusicDragHandlers(musicContainer, file.getName(), media.getSource());

            playButton.setOnAction(e -> mediaPlayer.play());
            pauseButton.setOnAction(e -> mediaPlayer.pause());
            stopButton.setOnAction(e -> {
                mediaPlayer.stop();
                mediaPlayer.seek(Duration.ZERO);
            });

            mediaBox.getChildren().add(musicContainer);
        }
    }

    private void setupMusicDragHandlers(VBox musicContainer, String fileName, String mediaSource) {
        musicContainer.setOnDragDetected(event -> {
            Dragboard db = musicContainer.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();

            // Create a visual representation for dragging
            Text dragNode = new Text(fileName);
            dragNode.setFont(Font.font(14));
            dragNode.setFill(Color.BLUE);

            // Create an image of the text for dragging
            Image snapshot = dragNode.snapshot(null, null);

            content.putImage(snapshot);
            content.putString("MUSIC:" + mediaSource + ":" + fileName);
            db.setContent(content);

            db.setDragView(snapshot, event.getX(), event.getY());
            event.consume();
        });
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
            content.putString("IMAGE:" + img.getUrl());
            db.setContent(content);
            event.consume();
        });

        drawingCanvas.getCanvas().setOnDragOver(event -> {
            if (event.getGestureSource() != drawingCanvas.getCanvas() &&
                    (event.getDragboard().hasImage() || event.getDragboard().hasString())) {
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
            else if (db.hasString()) {
                String data = db.getString();
                String[] parts = data.split(":");
                String type = parts[0];
                String source = parts[1];
                String name = parts.length > 2 ? parts[2] : "Media";

                double x = event.getX();
                double y = event.getY();

                if (type.equals("IMAGE")) {
                    // Load and draw the image
                    Image image = new Image(source);
                    drawingCanvas.getGraphicsContext().drawImage(image, x, y);
                }
                else if (type.equals("VIDEO") || type.equals("MUSIC")) {
                    // Draw a representation of the media
                    drawingCanvas.getGraphicsContext().setFill(Color.LIGHTGRAY);
                    drawingCanvas.getGraphicsContext().fillRect(x, y, 150, 40);
                    drawingCanvas.getGraphicsContext().setFill(Color.BLACK);
                    drawingCanvas.getGraphicsContext().fillText(name, x + 5, y + 20);

                    // You could also store the media reference for later interaction
                }
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