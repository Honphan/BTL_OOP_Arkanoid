package com.demo.GameView;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Render {

    // Tạo instance duy nhất của Render
    private static Render instance;

    // Constructor private để ngăn chặn việc tạo instance từ bên ngoài
    private Render() {
    }

    // Phương thức tĩnh để lấy instance của Render
    public static Render getInstance() {
        if (instance == null) {
            instance = new Render();
        }
        return instance;
    }

    public void loadMenuGame(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/fxml/menu.fxml"));
        Scene scene = new Scene(loader.load(), 500, 600);
        stage.setScene(scene);
        stage.setTitle("Arkanoid Game");
        stage.show();
    }

    public void loadGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/demo/view/fxml/gamePane.fxml")));
        Scene scene = new Scene(root, 1000, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
