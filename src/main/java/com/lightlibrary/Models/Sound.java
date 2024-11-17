package com.lightlibrary.Models;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sound {
    private static Map<String, AudioClip> soundEffects = new HashMap<>();
    private static MediaPlayer backgroundMusic;

    public static void initialize() {
        loadSoundEffect("jump", "/com/lightlibrary/Sounds/Jump.wav");
        loadSoundEffect("button", "/com/lightlibrary/Sounds/Button.wav");
        loadSoundEffect("gameOver", "/com/lightlibrary/Sounds/GameOver.wav");
        loadSoundEffect("pickUp", "/com/lightlibrary/Sounds/PickUp.wav");

        Media bgMusic = new Media(Objects.requireNonNull(Sound.class.getResource("/com/lightlibrary/Sounds/Game.mp3")).toExternalForm());
        backgroundMusic = new MediaPlayer(bgMusic);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);

        setBackgroundMusicVolume(0.3);
        setSoundEffectVolume(0.3);
    }

    private static void loadSoundEffect(String name, String path) {
        AudioClip sound = new AudioClip(Objects.requireNonNull(Sound.class.getResource(path)).toExternalForm());
        soundEffects.put(name, sound);
    }

    public static void playSoundEffect(String name) {
        AudioClip player = soundEffects.get(name);
        if (player != null) {
            player.stop();
            player.play();
        }
    }

    public static void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public static void pauseBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }

    public static void setBackgroundMusicVolume(double volume) {
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(volume);
        }
    }

    public static void setSoundEffectVolume(double volume) {
        for (AudioClip audioClip : soundEffects.values()) {
            audioClip.setVolume(volume);
        }
    }
}
