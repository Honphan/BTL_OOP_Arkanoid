module com.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Mở các package cho JavaFX FXML
    opens com.demo to javafx.fxml;
    opens com.demo.model to javafx.fxml;
    opens com.demo.controller to javafx.fxml;

    // Export các API public
    exports com.demo;

    // Chỉ cần export các model nếu chúng được sử dụng bởi các module khác
    // exports com.demo.model;
}