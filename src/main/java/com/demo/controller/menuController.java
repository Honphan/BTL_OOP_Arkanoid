package com.demo.controller;

import com.demo.GameView.Render;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class menuController {

    @FXML
    private void start(ActionEvent event) throws IOException {
        Render render = Render.getInstance();
        render.loadGame(event);
    }

    @FXML
    public void openOptions(ActionEvent event) throws IOException {}

    @FXML
    public void showScores(ActionEvent event) throws IOException {}

    @FXML
    public void exitGame(ActionEvent event) throws IOException {}




}