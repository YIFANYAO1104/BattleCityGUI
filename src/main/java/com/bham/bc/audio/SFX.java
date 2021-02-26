package com.bham.bc.audio;

import javafx.scene.media.AudioClip;

public enum SFX {
    CLICK("file:src/main/resources/audio/sfx/Blip_select11.wav");

    private AudioClip clip;

    SFX(String path) {
        clip = new AudioClip(path);
    }

    public void play() { new Thread(() -> clip.play()).start(); }

    public void setVolume (double value) { clip.setVolume(value); }
}
