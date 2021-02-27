package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ParallelAudioPlayer implements AudioPlayer {
    private ArrayList<MediaPlayer> mediaPlayers;
    private boolean isPlaying;
    private CyclicBarrier gate;

    /**
     * Constructs a parallel media player
     * @param mediaFiles audio files to be played as 1 audio
     */
    public ParallelAudioPlayer(ArrayList<Media> mediaFiles) {
        mediaPlayers = new ArrayList<>();
        for(Media media: mediaFiles) mediaPlayers.add(new MediaPlayer(media));

        gate = new CyclicBarrier(mediaFiles.size());
        isPlaying = false;
    }

    @Override
    public void play() {
        if(!mediaPlayers.isEmpty() && !isPlaying) {
            for(MediaPlayer player: mediaPlayers) {
                new Thread(() -> {
                    try {
                        gate.await();
                        player.play();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            isPlaying = true;
        }
    }

    @Override
    public void pause() {
        if(!mediaPlayers.isEmpty() && isPlaying) {
            for(MediaPlayer player: mediaPlayers) {
                new Thread(() -> {
                    try {
                        gate.await();
                        player.pause();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            isPlaying = false;
        }
    }

    @Override
    public void stop() {
        if(!mediaPlayers.isEmpty() && isPlaying) {
            for(MediaPlayer player: mediaPlayers) {
                new Thread(() -> {
                    try {
                        gate.await();
                        player.stop();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            isPlaying = false;
        }
    }

    @Override
    public void setVolume(double volume) {
        if(!mediaPlayers.isEmpty()) {
            for(MediaPlayer player: mediaPlayers) {
                new Thread(() -> player.setVolume(volume)).start();
            }
        }
    }
}
