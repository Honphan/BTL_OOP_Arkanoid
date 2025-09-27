module com.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    exports com.demo to javafx.graphics;
    exports com.demo.controller to javafx.fxml;
    opens com.demo.controller to javafx.fxml; // <-- add this line
}