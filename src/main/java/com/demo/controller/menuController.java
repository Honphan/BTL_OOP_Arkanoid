package com.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class menuController {

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/gamePane.fxml"));
        Parent gameRoot = loader.load();
        Scene gameScene = new Scene(gameRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(gameScene);
        stage.setMaximized(true);
        stage.show();
    }


    @FXML
    public void openOptions() {
        System.out.println("Options clicked!");
        // TODO: load options scene
    }

    @FXML
    public void showScores() {
        System.out.println("High Scores clicked!");
        // TODO: load scores scene
    }

    @FXML
    public void exitGame() {
        System.out.println("Exit clicked!");
        System.exit(0);
    }
}