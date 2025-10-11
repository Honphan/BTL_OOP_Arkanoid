package com.demo.Utils;


import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer effectPlayer;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBackgroundMusic(String musicPath) {
        try {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
                backgroundMusicPlayer.dispose();
            }

            // Thử load từ resources trước
            Media media;
            try {
                media = new Media(getClass().getResource(musicPath).toExternalForm());
            } catch (Exception e) {
                // Nếu không tìm thấy trong resources, thử đường dẫn tuyệt đối
                File audioFile = new File(musicPath);
                if (audioFile.exists()) {
                    media = new Media(audioFile.toURI().toString());
                } else {
                    System.err.println("Không tìm thấy file âm thanh: " + musicPath);
                    return;
                }
            }

            backgroundMusicPlayer = new MediaPlayer(media);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusicPlayer.setVolume(0.8);

            backgroundMusicPlayer.setOnReady(() -> {
                backgroundMusicPlayer.play();
                System.out.println("Đang phát nhạc nền: " + musicPath);
            });

            backgroundMusicPlayer.setOnError(() -> {
                System.err.println("Lỗi MediaPlayer: " + backgroundMusicPlayer.getError());
            });

        } catch (MediaException e) {
            System.err.println("Lỗi Media: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi khi phát nhạc nền: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playSound(String soundPath) {
        try {
            Media media;
            try {
                media = new Media(getClass().getResource(soundPath).toExternalForm());
            } catch (Exception e) {
                File audioFile = new File(soundPath);
                if (audioFile.exists()) {
                    media = new Media(audioFile.toURI().toString());
                } else {
                    System.err.println("Không tìm thấy file âm thanh: " + soundPath);
                    return;
                }
            }

            if (effectPlayer != null) {
                effectPlayer.stop();
                effectPlayer.dispose();
            }

            effectPlayer = new MediaPlayer(media);
            effectPlayer.setVolume(1);
            effectPlayer.setOnReady(() -> effectPlayer.play());

        } catch (Exception e) {
            System.err.println("Lỗi khi phát âm thanh: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }

    public void setMusicVolume(double volume) {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(volume);
        }
    }

    public void dispose() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.dispose();
        }
        if (effectPlayer != null) {
            effectPlayer.dispose();
        }
    }
}
