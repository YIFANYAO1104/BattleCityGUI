package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioManager {
    // Only one music for the menu, could be changed to a playlist
    public static final String MENU_MUSIC = "file:src/main/resources/audio/bg/bgm_0.mp3";

    private static Media media;
    private static MediaPlayer menuTrack;


    public AudioManager() {
        media = new Media(MENU_MUSIC);
        menuTrack = new MediaPlayer(media);
        menuTrack.setAutoPlay(true);
    }

    public void setMedia() {
        menuTrack.stop();
    }



    public void setMusicVolume(double volume) {}


    public void setEffectVolume(double volume) {
        for(SFX sfx: SFX.values()) {
            sfx.setVolume(volume);
        }
    }
}
