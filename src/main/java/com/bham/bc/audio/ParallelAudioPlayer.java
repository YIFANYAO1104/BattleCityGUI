package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Parallel player class to play multiple audio files at the same time
 * The class is normally used to play multiple layers (stems) of some track which is needed to be analyzed
 * NB Each layer must be of the same length and of similar metadata
 */
public class ParallelAudioPlayer implements AudioPlayer {
    private ArrayList<MediaPlayer> players;
    private ArrayList<AudioAnalyser> analysers;
    private boolean isPlaying;
    private CyclicBarrier gate;

    /**
     * Constructs a parallel media player
     * @param mediaFiles audio files to be played as 1 audio
     */
    public ParallelAudioPlayer(ArrayList<Media> mediaFiles) {
        players = new ArrayList<>();
        analysers = new ArrayList<>();

        for(Media media: mediaFiles) players.add(new MediaPlayer(media));
        for(MediaPlayer mp: players) analysers.add(new AudioAnalyser(mp,.06, 96));

        gate = new CyclicBarrier(mediaFiles.size());
        isPlaying = false;
    }

    /**
     * gets array of arrays of frequencies for each layer for each band
     * @return array of arrays of frequencies
     */
    public float[][] getFrequencies() {
        return analysers.stream().map(AudioAnalyser::getFrequencies).toArray(float[][]::new);
    }

    @Override
    public void play() {
        if(!players.isEmpty() && !isPlaying) {
            for(MediaPlayer player: players) {
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
        if(!players.isEmpty() && isPlaying) {
            for(MediaPlayer player: players) {
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
        if(!players.isEmpty()) {
            for(MediaPlayer player: players) {
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
        if(!players.isEmpty()) {
            for(MediaPlayer player: players) {
                new Thread(() -> player.setVolume(volume)).start();
            }
        }
    }

    @Override
    public boolean isPlaying() { return isPlaying; }
}
