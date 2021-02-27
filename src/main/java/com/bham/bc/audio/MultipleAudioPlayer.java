package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;

public class MultipleAudioPlayer implements AudioPlayer {
    private ArrayList<MediaPlayer> mediaPlayers;
    private MediaPlayer currentTrack;
    private boolean isPlaying;

    /**
     * Constructs a sequential media player
     * @param mediaFiles audio files to be put into playlist
     */
    public MultipleAudioPlayer(ArrayList<Media> mediaFiles) {
        mediaPlayers = new ArrayList<>();
        for(Media media: mediaFiles) mediaPlayers.add(new MediaPlayer(media));

        initPlaylist();
        isPlaying = false;
    }

    /**
     * initializes a cyclic sequence of media players
     */
    private void initPlaylist() {
        for(int i = 0; i < mediaPlayers.size(); i++) {
            final MediaPlayer curr = mediaPlayers.get(i);
            final MediaPlayer next = mediaPlayers.get((i+1) % mediaPlayers.size());

            curr.setOnEndOfMedia(() -> {
                curr.stop();
                currentTrack = next;
                next.play();
            });
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
        if(currentTrack != null && isPlaying) {
            new Thread(() -> currentTrack.stop()).start();
            isPlaying = false;
        }
    }

    @Override
    public void setVolume(double volume) {
        for(MediaPlayer player: mediaPlayers) {
            new Thread(() -> player.setVolume(volume)).start();
        }
    }
}
