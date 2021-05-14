package com.bham.bd.audio;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

/**
 * <h1>Parallel Audio Player</h1>
 *
 * <p>Parallel player class to play multiple audio files at the same time. The class is normally used to play multiple layers
 * (stems) of some track which is needed to be analyzed for different effects on different song layers, for example, lyrics,
 * base or instruments.</p>
 *
 * <p><b>NB</b> Each layer must be of the same length and of similar metadata</p>
 */
class ParallelAudioPlayer implements AudioPlayer {
    /** List of media players that are played in parallel */
    private final List<MediaPlayer> PLAYERS;

    /** List of analysers each media player possesses */
    private final List<AudioAnalyser> ANALYSERS;

    /** Gate which prevents any player to be started/paused/stopped before each of them are synchronized */
    private final CyclicBarrier GATE;

    /** On end property indicating what method will be run after all tracks finish playing */
    private final ObjectProperty<Runnable> ON_END;

    /** Number of players to wait before some action could happen (e.g., before <i>ON_END</i> could be fired) */
    private int countdown;

    /** Indicates whether this audio player is playing */
    private boolean isPlaying;

    /**
     * Constructs a parallel audio player with at least one song playing
     * @param  mediaFiles audio files to be played as 1 audio
     * @throws IllegalArgumentException thrown when the list of audio files is empty
     */
    ParallelAudioPlayer(ArrayList<Media> mediaFiles) throws IllegalArgumentException {
        if(mediaFiles.isEmpty()) throw new IllegalArgumentException("There must be at least one media file to play");

        PLAYERS = mediaFiles.stream().map(MediaPlayer::new).collect(Collectors.toList());
        ANALYSERS = PLAYERS.stream().map(mp -> new AudioAnalyser(mp,.06, 96)).collect(Collectors.toList());

        PLAYERS.forEach(player -> player.setOnEndOfMedia(() -> {
            player.stop();
            if(--countdown == 0) {
                isPlaying = false;
                countdown = mediaFiles.size();

                if(getOnEndOfAudio() != null) Platform.runLater(getOnEndOfAudio());
            }
        }));

        isPlaying = false;
        countdown = mediaFiles.size();

        GATE = new CyclicBarrier(Math.min(1, countdown));
        ON_END = new SimpleObjectProperty<>(this, "onEndOfAudio");
    }

    /**
     * Gets array of arrays of frequencies for each layer for each band
     * @return array of arrays of frequencies
     */
    public float[][] getFrequencies() {
        return ANALYSERS.stream().map(AudioAnalyser::getFrequencies).toArray(float[][]::new);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void play() {
        for(MediaPlayer player: PLAYERS) {
            new Thread(() -> {
                try {
                    GATE.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                player.play();
            }).start();
        }
        isPlaying = true;
    }

    @Override
    public void pause() {
        for(MediaPlayer player: PLAYERS) {
            new Thread(() -> {
                try {
                    GATE.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                player.pause();
            }).start();
        }
        isPlaying = false;
    }

    @Override
    public void stop() {
        for(MediaPlayer player: PLAYERS) {
            new Thread(() -> {
                try {
                    GATE.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                player.stop();
            }).start();
        }
        isPlaying = false;
    }

    @Override
    public void setVolume(double volume) {
        PLAYERS.forEach(player -> player.setVolume(volume));
    }

    @Override
    public void setOnEndOfAudio(Runnable value) {
        ON_END.set(value);
    }

    @Override
    public Runnable getOnEndOfAudio() {
        return ON_END == null ? null : ON_END.get();
    }
}
