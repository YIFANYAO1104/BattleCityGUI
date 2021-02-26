package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;

public class MultipleAudioPlayer {
    private ArrayList<MediaPlayer> mediaPlayers;
    private MediaPlayer currentTrack;
    private boolean isPlaying;

    public MultipleAudioPlayer(ArrayList<Media> mediaFiles) {
        mediaPlayers = new ArrayList<>();
        for(Media media: mediaFiles) mediaPlayers.add(new MediaPlayer(media));

        initPlaylist();
        isPlaying = false;
    }

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

    public void play() {
        if(currentTrack != null && !isPlaying) {
            currentTrack.play();
        }
    }

    public void pause() {
        if(currentTrack != null && isPlaying) {
            currentTrack.pause();
        }
    }

    public void stop() {
        if(currentTrack != null && !isPlaying) {
            currentTrack.stop();
        }

    }

}
