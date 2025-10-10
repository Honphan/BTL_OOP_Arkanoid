package com.demo.controller;

import com.demo.view.Render;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class MenuController {
    private String difficultyLevel = "Easy";

    @FXML
    private void start(ActionEvent event) throws IOException {
        Render render = Render.getInstance();
        render.loadGame(event);
    }

    @FXML
    public void openOptions(ActionEvent event) throws IOException {
        // Tạo alert để hiển thị các mức độ khó
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Chọn mức độ khó");
        alert.setHeaderText("Vui lòng chọn mức độ khó cho game:");
        alert.setContentText("Chọn một mức độ phù hợp với bạn.");

        // Thêm các nút tùy chỉnh cho các mức độ khó
        ButtonType easyButton = new ButtonType("Dễ");
        ButtonType mediumButton = new ButtonType("Vừa");
        ButtonType hardButton = new ButtonType("Khó");
        ButtonType cancelButton = new ButtonType("Hủy", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(easyButton, mediumButton, hardButton, cancelButton);

        // Hiển thị alert và xử lý lựa chọn
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == easyButton) {
                difficultyLevel = "Easy";
            } else if (result.get() == mediumButton) {
                difficultyLevel = "Medium";
            } else if (result.get() == hardButton) {
                difficultyLevel = "Hard";
            }
        }

        // Sau khi chọn, in ra mức độ khó (chỉ để debug)
        System.out.println("Mức độ khó được chọn: " + difficultyLevel);

        // Gửi thông tin này xuống tầng gamePane
        passDifficultyToGamePane();
    }

    private void passDifficultyToGamePane() {
        System.out.println("Gửi mức độ khó xuống tầng gamePane: " + difficultyLevel);
    }

    @FXML
    public void exitGame(ActionEvent event) throws IOException {
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Tạo Alert tiêu chuẩn, tránh tùy biến quá nhiều
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận thoát");
        alert.setHeaderText("Bạn có chắc chắn muốn thoát?");
        alert.setContentText("Nhấn OK để thoát hoặc Cancel để quay lại.");

        // Tùy chỉnh nhẹ các nút
        ButtonType buttonYes = new ButtonType("Thoát");
        ButtonType buttonNo = new ButtonType("Quay lại", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        // Hiển thị alert và xử lý kết quả
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
            mainStage.close();
            System.exit(0);
        }
    }
}