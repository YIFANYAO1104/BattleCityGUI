package com.bham.bc.audio;

import javafx.scene.media.AudioClip;

/**
 * Enum for playing SFX
 */
public enum SFX {
    CLICK("file:src/main/resources/audio/sfx/Blip_select11.wav");

    private AudioClip clip;

    /**
     * Constructs SFX while utilizing AudioClip
     * @param path to SFX file
     */
    SFX(String path) { clip = new AudioClip(path); }

    /**
     * executes play method on a separate thread
     */
    public void play() { new Thread(() -> clip.play()).start(); }

    /**
     * sets the volume of the sound effect on a separate thread
     * @param value of the volume
     */
    public void setVolume (double value) { new Thread(() -> clip.setVolume(value)).start(); }
}
