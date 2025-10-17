package com.demo.GameView;

import com.demo.controller.GamePaneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Render {
    private static Render instance;
    private int selectedLevel = 1;
    private static int currentLevel = 1;

    private Render() {}

    public static Render getInstance() {
        if (instance == null) {
            instance = new Render();
        }
        return instance;
    }

    public void loadMenuGame(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/fxml/menu.fxml"));
        Scene scene = new Scene(loader.load());


        stage.setScene(scene);
        stage.setTitle("Arkanoid Game");
        stage.setMaximized(true); // Full screen
        stage.show();
    }

    public void loadGame(ActionEvent event, int levelNumber) throws IOException {
        Render.currentLevel = levelNumber;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/fxml/gamePane.fxml"));
        Parent root = loader.load();

        // Lấy controller và set level
        GamePaneController controller = loader.getController();

        // Lấy stage hiện tại
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Lấy kích thước hiện tại của cửa sổ
        double windowWidth = stage.getWidth();
        double windowHeight = stage.getHeight();

        // Tạo Scene mới có cùng kích thước
        Scene scene = new Scene(root, windowWidth, windowHeight);

        // Gán scene mới vào stage
        stage.setScene(scene);
        stage.show();
    }
    public int getSelectedLevel() {
        return selectedLevel;
    }
    public static int getCurrentLevel() {
        return currentLevel;
    }
}