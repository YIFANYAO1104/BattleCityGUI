package com.bham.bc.audio;

import javafx.scene.media.Media;

import java.util.ArrayList;

public class AudioManager {
    private static final double INITIAL_VOLUME = 1;

    private double volume;
    private static AudioPlayer player;

    public static AudioManager audioManager = new AudioManager();

    /**
     * Constructs audio manager and sets default player to play MENU track
     */
    public AudioManager() {
        volume = INITIAL_VOLUME;
    }

    /**
     * creates multiple audio player
     * @param tracks list of TRACK enums as audio sequences for the player
     */
    public void createSequentialPlayer(TRACK... tracks) {
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
    public void createParallelPlayer(TRACK... tracks) {
        ArrayList<Media> mediaFiles = new ArrayList<>();
        for(TRACK track: tracks) mediaFiles.add(track.getMedia());

        if(player != null && player.isPlaying()) player.stop();
        player = new ParallelAudioPlayer(mediaFiles);
        player.setVolume(volume);
    }

    /**
     * gets the currently used player
     * @return AudioPlayer object
     */
    public AudioPlayer getCurrentPlayer() { return player; }


    public void play() { if(player != null) player.play(); }
    public void pause() { if(player != null) player.pause(); }
    public void stop() { if(player != null) player.stop(); }

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
