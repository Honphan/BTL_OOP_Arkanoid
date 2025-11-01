module com.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires java.desktop;
    requires javafx.swing;
    requires jdk.compiler;


    // Mở các package cho JavaFX FXML
    opens com.demo to javafx.fxml;
    opens com.demo.model to javafx.fxml;
    opens com.demo.controller to javafx.fxml;

    // Export các API public
    exports com.demo;
    opens com.demo.model.brick to javafx.fxml;
    opens com.demo.model.powerup to javafx.fxml;

    // Chỉ cần export các model nếu chúng được sử dụng bởi các module khác
    // exports com.demo.model;
}