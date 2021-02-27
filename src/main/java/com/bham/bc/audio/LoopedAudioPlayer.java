package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class LoopedAudioPlayer implements AudioPlayer {
    MediaPlayer player;
    boolean isPlaying;

    /**
     * Constructs a looped media player
     * @param media audio file to be played over and over
     */
    public LoopedAudioPlayer(Media media) {
        player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
    }

    @Override
    public void play() {
        if(player != null && !isPlaying) {
            new Thread(() -> player.play()).start();
            isPlaying = true;
        }
    }

    @Override
    public void pause() {
        if(player != null && isPlaying) {
            new Thread(() -> player.pause()).start();
            isPlaying = false;
        }
    }

    @Override
    public void stop() {
        if(player != null && !isPlaying) {
            new Thread(() -> player.stop()).start();
            isPlaying = false;
        }
    }

    @Override
    public void setVolume(double volume) {
        new Thread(() -> player.setVolume(volume)).start();
    }
}
