package com.bham.bc.audio;

import javafx.scene.media.AudioClip;

/**
 * Enum for all sound effects in the game
 */
public enum SoundEffect {
    SELECT("audio/sfx/select.mp3"),
    DESTROY_CHARACTER("audio/sfx/destroy-character.mp3"),
    DESTROY_SOFT("audio/sfx/destroy-soft.mp3"),
    HIT_CHARACTER("audio/sfx/hit-character.mp3"),
    HIT_SOFT("audio/sfx/hit-soft.mp3"),
    HIT_HARD("audio/sfx/hit-hard.mp3"),
    SHOT_DEFAULT("audio/sfx/shot-default.mp3"),
    SHOT_RAPID("audio/sfx/shot-rapid.mp3"),
    EXPLODE("audio/sfx/explode.mp3"),
    POWERUP("audio/sfx/powerup.mp3"),
    TRAP("audio/sfx/trap.mp3");

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
