module motlohi.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens motlohi.demo1 to javafx.fxml;
    exports motlohi.demo1;
}