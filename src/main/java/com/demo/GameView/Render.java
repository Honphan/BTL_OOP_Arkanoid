package com.demo.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
<<<<<<< HEAD:src/main/java/com/demo/GameView/Render.java
import javafx.stage.Screen;
=======
>>>>>>> 838080f3b0d2d93cfba54a5e9231de2230cecc33:src/main/java/com/demo/view/Render.java
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Render {
    private static Render instance;
    private int selectedLevel = 1;

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

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        stage.setScene(scene);
        stage.setTitle("Arkanoid Game");
        stage.setMaximized(true); // Full screen
        stage.show();
    }

    public void loadGame(ActionEvent event, int levelNumber) throws IOException {
        this.selectedLevel = levelNumber;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/fxml/gamePane.fxml"));
        Parent root = loader.load();

        // Get the controller and set the selected level
        com.demo.controller.GamePaneController controller = loader.getController();
        controller.setStartingLevel(levelNumber);


        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();


        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true); // Full screen
        stage.show();
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }
}