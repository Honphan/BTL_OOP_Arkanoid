package com.demo.GameView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Render {
    public void loadMenuGame(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/fxml/menu.fxml"));
        Scene scene = new Scene(loader.load(),500,600);
        stage.setScene(scene);
        stage.setTitle("Arkanoid Game");
        stage.show();
    }
}
