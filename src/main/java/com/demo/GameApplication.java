package com.demo;


import com.demo.view.Render;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Render render = Render.getInstance();
        render.loadMenuGame(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}