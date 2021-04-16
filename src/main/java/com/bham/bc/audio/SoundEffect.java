package com.bham.bc.audio;

import javafx.scene.media.AudioClip;

/**
 * Enum for all sound effects in the game
 */
public enum SoundEffect {
    SELECT("audio/sfx/select.wav");

    private final String PATH;

    /**
     * Constructs SFX enum with a given path to audio file
     * @param path path to SFX
     */
    SoundEffect(String path) {
        PATH = path;
    }

    /**
     * Creates a new audio clip from a path created by this enum constant
     * @return new {@code AudioClip} instance bound to the name of this enum's value
     */
    public AudioClip createAudio() {
        return new AudioClip(getClass().getClassLoader().getResource(PATH).toExternalForm());
    }
}
