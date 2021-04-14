package com.bham.bc.audio;

import javafx.scene.media.AudioClip;

/**
 * Enum for playing SFX
 */
public enum Sfx {
    SELECT("file:src/main/resources/audio/sfx/select.wav");

    private AudioClip clip;

    /**
     * Constructs SFX while utilizing AudioClip
     * @param path path to SFX file
     */
    Sfx(String path) { clip = new AudioClip(path); }

    /**
     * executes play method on a separate thread
     */
    public void play() { new Thread(() -> clip.play()).start(); }

    /**
     * sets the volume of the sound effect on a separate thread
     * @param volume
     */
    public void setVolume (double volume) { new Thread(() -> clip.setVolume(volume)).start(); }
}
