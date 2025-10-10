package com.demo.Utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Background {
    public static Image remove(Image originalImage) {
        if (originalImage == null) return null;

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelReader pixelReader = originalImage.getPixelReader();

        // Lấy màu background từ các góc ảnh
        Color backgroundColor = getBackgroundColor(pixelReader, width, height);
        double tolerance = 0.15; // Tăng tolerance để bao phủm nhiều màu tương tự

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color currentColor = pixelReader.getColor(x, y);

                if (isBackgroundColor(currentColor, backgroundColor, tolerance)) {
                    pixelWriter.setColor(x, y, Color.TRANSPARENT);
                } else {
                    pixelWriter.setColor(x, y, currentColor);
                }
            }
        }

        return writableImage;
    }

    private static Color getBackgroundColor(PixelReader pixelReader, int width, int height) {
        // Lấy màu từ 4 góc và chọn màu xuất hiện nhiều nhất
        Color[] cornerColors = {
                pixelReader.getColor(0, 0),                    // Góc trên trái
                pixelReader.getColor(width-1, 0),              // Góc trên phải
                pixelReader.getColor(0, height-1),             // Góc dưới trái
                pixelReader.getColor(width-1, height-1)        // Góc dưới phải
        };

        // Trả về màu của góc trên trái (thường là background)
        return cornerColors[0];
    }

    private static boolean isBackgroundColor(Color color, Color backgroundColor, double tolerance) {
        double redDiff = Math.abs(color.getRed() - backgroundColor.getRed());
        double greenDiff = Math.abs(color.getGreen() - backgroundColor.getGreen());
        double blueDiff = Math.abs(color.getBlue() - backgroundColor.getBlue());

        return redDiff <= tolerance && greenDiff <= tolerance && blueDiff <= tolerance;
    }

    public static  Image loadBackgroundImage() {
        Image backgroundImage = null;
        try {
            backgroundImage = new Image(Background.class.getResource("/com/demo/view/image/game/bg3.png").toExternalForm());
            System.out.println("Tải bg3.png thành công!");
        } catch (Exception e) {
            System.err.println("Không thể load background image: " + e.getMessage());
            backgroundImage = null;
        }
        return backgroundImage;
    }

    // Phương thức render background
    public static void renderBackground(GraphicsContext gc, double canvasWidth, double canvasHeight, Image backgroundImage) {
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, canvasWidth, canvasHeight);
        } else {
            // Fallback: vẽ màu nền mặc định
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, canvasWidth, canvasHeight);
        }
    }


}
