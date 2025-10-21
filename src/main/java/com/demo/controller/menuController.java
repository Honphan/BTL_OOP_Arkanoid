package com.demo.controller;

import com.demo.view.Render;
import com.demo.util.SoundManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class menuController {
    private int selectedLevel = 1;
    private SoundManager soundManager;
    String[] imagePaths = new String[]{"/com/demo/view/image/levels/level1.png", "/com/demo/view/image/levels/level2.png", "/com/demo/view/image/levels/level3.png"};


    @FXML
    private ImageView levelPreviewImage;

    @FXML
    private Label levelNumberLabel;

    @FXML
    private StackPane leftArrowButton;

    @FXML
    private StackPane rightArrowButton;

    // Array of level preview images
    private Image[] levelImages;

    @FXML
    private void initialize() {
        soundManager = SoundManager.getInstance();
        soundManager.playBackgroundMusic("/com/demo/view/sound/barkanoid_background.mp3");


        // Setup arrow button events
        setupArrowButtons();
        // Load level preview images
        loadLevelImages();

        // Set initial level preview
        updateLevelPreview();
    }

    private void setupArrowButtons() {
        // Left arrow click event
        leftArrowButton.setOnMouseClicked(event -> {
            if (selectedLevel > 1) {
                selectedLevel--;
                updateLevelPreview();
                playClickSound();
            }
        });

        // Right arrow click event
        rightArrowButton.setOnMouseClicked(event -> {
            if (selectedLevel < imagePaths.length && selectedLevel > 0) {
                selectedLevel++;
                updateLevelPreview();
                playClickSound();
            }
        });
    }

    private void playClickSound() {
        try {
            if (soundManager != null) {
                soundManager.playSound("/com/demo/view/sound/clock_mouse.mp3");
            }
        } catch (Exception e) {
            System.err.println("Could not play click sound: " + e.getMessage());
        }
    }

    private void loadLevelImages() {
        levelImages = new Image[imagePaths.length];
        for (int i = 0; i < imagePaths.length; i++) {
            try {
                levelImages[i] = new Image(getClass().getResourceAsStream(imagePaths[i]));
                System.out.println("Loaded image: " + imagePaths[i]);
            } catch (Exception e) {
                System.err.println("Không load được ảnh level " + (i + 1) + ": " + e.getMessage());
            }
        }
    }



    private void updateLevelPreview() {
        // Update level number label
        levelNumberLabel.setText("LEVEL " + selectedLevel);

        // Update preview image
        if (levelImages != null && levelImages[selectedLevel - 1] != null) {
            levelPreviewImage.setImage(levelImages[selectedLevel - 1]);
            levelPreviewImage.setVisible(true);
        } else {
            levelPreviewImage.setVisible(false);
            System.err.println("Image for level " + selectedLevel + " is null");
        }

        // Update arrow button states
        leftArrowButton.setDisable(selectedLevel == 1);
        rightArrowButton.setDisable(selectedLevel == 3);
    }


    @FXML
    private void previousLevel() {
        if (selectedLevel > 1) {
            selectedLevel--;
            updateLevelPreview();
            soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");
        }
    }

    @FXML
    private void nextLevel() {
        if (selectedLevel < imagePaths.length && selectedLevel > 0) {
            selectedLevel++;
            updateLevelPreview();
            soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");
        }
    }

    @FXML
    private void start(ActionEvent event) throws IOException {
        soundManager.stopBackgroundMusic();
        Render render = Render.getInstance();
        render.loadGame(event, selectedLevel);
    }


    @FXML
    public void exitGame(ActionEvent event) throws IOException {
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận thoát");
        alert.setHeaderText("Bạn có chắc chắn muốn thoát?");
        alert.setContentText("Nhấn OK để thoát hoặc Cancel để quay lại.");

        ButtonType buttonYes = new ButtonType("Thoát");
        ButtonType buttonNo = new ButtonType("Quay lại", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
            soundManager.dispose();
            mainStage.close();
            System.exit(0);
        }
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }
}