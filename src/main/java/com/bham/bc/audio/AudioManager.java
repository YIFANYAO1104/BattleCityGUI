package com.bham.bc.audio;

import javafx.scene.media.Media;

import java.util.ArrayList;
import java.util.Arrays;

public class AudioManager {
    private static final double INITIAL_VOLUME = 1;

    private double volume;
    private static AudioPlayer player;

    /**
     * Constructs audio manager and sets default player to play MENU track
     */
    public AudioManager() {
        volume = INITIAL_VOLUME;
        createSequentialPlayer(new ArrayList<>(Arrays.asList(TRACK.MENU)));
    }

    /**
     * creates multiple audio player
     * @param tracks list of TRACK enums as audio sequences for the player
     */
    public void createSequentialPlayer(ArrayList<TRACK> tracks) {
        ArrayList<Media> mediaFiles = new ArrayList<>();
        for(TRACK track: tracks) mediaFiles.add(track.getMedia());

        if(player != null && player.isPlaying()) player.stop();
        player = new SequentialAudioPlayer(mediaFiles);
        player.setVolume(volume);
    }

    /**
     * creates parallel audio player
     * @param tracks list of TRACK enums as audio layers for the player
     */
    public void createParallelPlayer(ArrayList<TRACK> tracks) {
        ArrayList<Media> mediaFiles = new ArrayList<>();
        for(TRACK track: tracks) mediaFiles.add(track.getMedia());

        if(player != null && player.isPlaying()) player.stop();
        player = new ParallelAudioPlayer(mediaFiles);
        player.setVolume(volume);
    }

    public void play() { player.play(); }
    public void pause() { player.pause(); }
    public void stop() { player.stop(); }

    /**
     * sets volume for the current player
     * @param volume
     */
    public void setMusicVolume(double volume) {
        this.volume = volume;
        player.setVolume(volume);
    }

    /**
     * sets volume for every sound effect
     * @param volume
     */
    public void setEffectVolume(double volume) {
        for(SFX sfx: SFX.values()) {
            sfx.setVolume(volume);
        }
    }
}
