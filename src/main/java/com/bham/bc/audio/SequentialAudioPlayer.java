package com.bham.bc.audio;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * <h1>Sequential Audio Player</h1>
 *
 * <p>Used to play multiple tracks in sequence. Tracks are played one by one from start to finish in a sequence
 * they were constructed from. The player resets itself after all tracks finish playing to the first track but
 * does not start paying again.</p>
 */
class SequentialAudioPlayer implements AudioPlayer {
    /** List of media players that are played in sequence */
    private final ArrayList<MediaPlayer> PLAYERS;

    /** On end property indicating what method will be run after all tracks finish playing */
    private final ObjectProperty<Runnable> ON_END;

    /** The current media player that is being played as per sequence order */
    private MediaPlayer currentTrack;

    /** Indicates whether this audio player is playing */
    private boolean isPlaying;

    /**
     * Constructs a sequential audio player with at least one song playing
     * @param  mediaFiles audio files to be put into playlist
     * @throws IllegalArgumentException thrown when the list of audio files is empty
     */
    SequentialAudioPlayer(ArrayList<Media> mediaFiles) throws IllegalArgumentException {
        if(mediaFiles.isEmpty()) throw new IllegalArgumentException("There must be at least one media file to play");

        PLAYERS = mediaFiles.stream().map(MediaPlayer::new).collect(Collectors.toCollection(ArrayList::new));
        ON_END = new SimpleObjectProperty<>(this, "onEndOfAudio");
        isPlaying = false;
        initPlaylist();
    }

    /**
     * Initializes a one-time sequence of media players
     */
    private void initPlaylist() {
        currentTrack = PLAYERS.get(0);

        PLAYERS.get(PLAYERS.size() - 1).setOnEndOfMedia(() -> {
            isPlaying = false;
            currentTrack = PLAYERS.get(0);
            PLAYERS.get(PLAYERS.size() - 1).stop();
            if(getOnEndOfAudio() != null) Platform.runLater(getOnEndOfAudio());
        });

        for (int i = 0; i < PLAYERS.size() - 1; i++) {
            MediaPlayer curr = PLAYERS.get(i);
            MediaPlayer next = PLAYERS.get(i + 1);

            curr.setOnEndOfMedia(() -> {
                curr.stop();
                next.setVolume(curr.getVolume());
                next.play();
                currentTrack = next;
            });
        }
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void play() {
        currentTrack.play();
        isPlaying = true;
    }

    @Override
    public void pause() {
        currentTrack.pause();
        isPlaying = false;
    }

    @Override
    public void stop() {
        currentTrack.stop();
        isPlaying = false;
    }

    @Override
    public void setVolume(double volume) {
        currentTrack.setVolume(volume);
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
