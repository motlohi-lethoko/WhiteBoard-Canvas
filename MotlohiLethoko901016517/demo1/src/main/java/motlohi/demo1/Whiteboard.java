package motlohi.demo1;

import javafx.scene.layout.BorderPane;

public class Whiteboard {
    private final BorderPane root;
    private final DrawingCanvas drawingCanvas;
    private final ToolBar toolBar;
    private final MediaPanel mediaPanel;

    public Whiteboard() {
        this.root = new BorderPane();
        this.drawingCanvas = new DrawingCanvas();
        this.mediaPanel = new MediaPanel(drawingCanvas);
        this.toolBar = new ToolBar(drawingCanvas, mediaPanel); 

        initializeLayout();
    }

    private void initializeLayout() {
        root.setCenter(drawingCanvas.getCanvasContainer());
        root.setBottom(toolBar.getToolbar());
        root.setRight(mediaPanel.getMediaBox());
    }

    public BorderPane getRoot() {
        return root;
    }
}