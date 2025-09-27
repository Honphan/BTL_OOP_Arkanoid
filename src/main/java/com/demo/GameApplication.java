package com.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/menu.fxml"));
        Scene scene = new Scene(loader.load(),500,600);
        stage.setScene(scene);
        stage.setTitle("Arkanoid Game");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
