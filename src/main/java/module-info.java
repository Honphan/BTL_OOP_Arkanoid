module com.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
<<<<<<< HEAD
    requires javafx.media;
    requires java.desktop;
    requires javafx.swing;

=======
    requires java.desktop;
>>>>>>> 838080f3b0d2d93cfba54a5e9231de2230cecc33

    // Mở các package cho JavaFX FXML
    opens com.demo to javafx.fxml;
    opens com.demo.model to javafx.fxml;
    opens com.demo.controller to javafx.fxml;

    // Export các API public
    exports com.demo;
<<<<<<< HEAD
    opens com.demo.model.powerup to javafx.fxml;
    opens com.demo.model.bricks to javafx.fxml;
=======
    opens com.demo.model.brick to javafx.fxml;
    opens com.demo.model.powerup to javafx.fxml;
    opens com.demo.model.ball to javafx.fxml;
    opens com.demo.model.paddle to javafx.fxml;
>>>>>>> 838080f3b0d2d93cfba54a5e9231de2230cecc33

    // Chỉ cần export các model nếu chúng được sử dụng bởi các module khác
    // exports com.demo.model;
}