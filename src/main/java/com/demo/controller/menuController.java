package com.demo.controller;

import com.demo.GameApplication;
import com.demo.model.GameManager;
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
    private void start(ActionEvent event) throws IOException {
//        GameManager.getInstance().startGame();
        Parent root = FXMLLoader.load(getClass().getResource("/com/demo/view/gamePane.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }




    @FXML
    public void openOptions(ActionEvent event) throws IOException {}

    @FXML
    public void showScores(ActionEvent event) throws IOException {}

    @FXML
    public void exitGame(ActionEvent event) throws IOException {}




}