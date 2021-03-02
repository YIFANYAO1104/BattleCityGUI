package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;

public class SequentialAudioPlayer implements AudioPlayer {
    private ArrayList<MediaPlayer> players;
    private MediaPlayer currentTrack;
    private boolean isPlaying;

    /**
     * Constructs a sequential media player
     * @param mediaFiles audio files to be put into playlist
     */
    public SequentialAudioPlayer(ArrayList<Media> mediaFiles) {
        players = new ArrayList<>();
        for(Media media: mediaFiles) players.add(new MediaPlayer(media));

        initPlaylist();
        isPlaying = false;
    }

    /**
     * initializes a cyclic sequence of media players
     */
    private void initPlaylist() {
        if(!players.isEmpty()) currentTrack = players.get(0);

        if(players.size() == 1) {
            currentTrack.setCycleCount(MediaPlayer.INDEFINITE);
        } else {
            for (int i = 0; i < players.size(); i++) {
                MediaPlayer curr = players.get(i);
                MediaPlayer next = players.get((i + 1) % players.size());

                curr.setOnEndOfMedia(() -> {
                    curr.stop();
                    next.play();
                    currentTrack = next;
                });
            }
        }
    }

    @Override
    public void play() {
        if(currentTrack != null && !isPlaying) {
            new Thread(() -> currentTrack.play()).start();
            isPlaying = true;
        }
    }

    @Override
    public void pause() {
        if(currentTrack != null && isPlaying) {
            new Thread(() -> currentTrack.pause()).start();
            isPlaying = false;
        }
    }

    @Override
    public void stop() {
        if(currentTrack != null) {
            new Thread(() -> currentTrack.stop()).start();
            isPlaying = false;
        }
    }

    @Override
    public void setVolume(double volume) {
        for(MediaPlayer player: players) {
            new Thread(() -> player.setVolume(volume)).start();
        }
    }

    @Override
    public boolean isPlaying() { return isPlaying; }
}
