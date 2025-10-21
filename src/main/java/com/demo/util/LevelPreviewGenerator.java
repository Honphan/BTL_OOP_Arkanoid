package com.demo.util;

import com.demo.model.LevelManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.io.File;

public class LevelPreviewGenerator {

    public static void generateLevelPreview(int levelNumber, String outputPath) {
        try {
            // Create canvas với kích thước chính xác
            double width = 340;  // Match ImageView fitWidth
            double height = 240; // Match ImageView fitHeight
            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Draw background image/gradient thay vì solid color
            gc.setFill(Color.web("#001a33"));
            gc.fillRect(0, 0, width, height);

            // Add stars background effect
            gc.setFill(Color.web("#ffffff"));
            for (int i = 0; i < 20; i++) {
                double x = Math.random() * width;
                double y = Math.random() * height;
                gc.fillOval(x, y, 1, 1);
            }

            // Add gradient overlay
            gc.setFill(Color.web("#003366"));
            gc.fillRect(0, height - 60, width, 60);

            // Create level với toàn bộ canvas
            double totalWidth = width * 0.9;
            double totalHeight = 120;
            double startX = (width - totalWidth) / 2;
            double startY = 40;

            LevelManager level = new LevelManager(levelNumber, totalWidth, totalHeight);
            level.render(gc);

            // Add level text
            gc.setFill(Color.web("#00ffff"));
            gc.setFont(javafx.scene.text.Font.font("Arial Black", 20));
            gc.fillText("LEVEL " + levelNumber, 10, 25);

            // Save image
            WritableImage writableImage = new WritableImage((int)width, (int)height);
            canvas.snapshot(null, writableImage);

            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", outputFile);

            System.out.println("Generated preview for level " + levelNumber + " at: " + outputPath);

        } catch (Exception e) {
            System.err.println("Error generating level preview: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
