package com.demo.model.score;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ScoreManager {
    private static ScoreManager instance;
    private Map<Integer, Integer> highScores; // Map lưu điểm cao của mỗi level
    private final String saveFilePath = "highscores.dat";

    private ScoreManager() {
        highScores = new HashMap<>();
        loadScores(); // Tải điểm số từ file khi khởi tạo
    }

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    /**
     * Cập nhật điểm cao cho một level
     * @param level Level hiện tại
     * @param score Điểm số mới
     * @return true nếu điểm mới cao hơn điểm cũ
     */
    public boolean updateHighScore(int level, int score) {
        int currentHighScore = getHighScore(level);
        if (score > currentHighScore) {
            highScores.put(level, score);
            saveScores(); // Lưu ngay khi có điểm mới
            return true;
        }
        return false;
    }

    /**
     * Lấy điểm cao cho một level
     * @param level Level cần lấy điểm
     * @return Điểm cao nhất của level đó
     */
    public int getHighScore(int level) {
        return highScores.getOrDefault(level, 0);
    }

    /**
     * Lưu điểm số vào file
     */
    public void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath))) {
            oos.writeObject(highScores);
            System.out.println("Điểm cao đã được lưu thành công");
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu điểm cao: " + e.getMessage());
        }
    }

    /**
     * Tải điểm số từ file
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(saveFilePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                highScores = (Map<Integer, Integer>) ois.readObject();
                System.out.println("Đã tải điểm cao từ file");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Lỗi khi tải điểm cao: " + e.getMessage());
                // Nếu có lỗi, sử dụng HashMap mới
                highScores = new HashMap<>();
            }
        } else {
            System.out.println("Chưa có file điểm cao, tạo mới");
            highScores = new HashMap<>();
        }
    }

    /**
     * Xóa tất cả điểm cao
     */
    public void resetAllHighScores() {
        highScores.clear();
        saveScores();
    }
}
