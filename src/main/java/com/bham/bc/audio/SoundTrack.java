package com.bham.bc.audio;

import javafx.scene.media.Media;

/**
 * Enum for all tracks in the game
 */
public enum SoundTrack {
    TAKE_LEAD("audio/music/take-the-lead.mp3"),
    NIGHT_BREAK("audio/music/night-break.mp3"),
    CORRUPTION("audio/music/corruption.mp3"),
    REVOLUTION("audio/music/voxel-revolution.mp3"),
    DEEP_SPACE("audio/music/deep-space.mp3"),
    RHYTHMIC_BOUNCE("audio/music/rhythmic-bounce.mp3");

    /** Path to audio file */
    private final String PATH;

    /**
     * Constructs Track enum with a given path to audio file
     * @param path path to track
     */
    SoundTrack(String path) {
        PATH = path;
    }

    /**
     * Creates a new media from a path created by this enum constant
     * @return new {@code Media} instance bound to the name of this enum's value
     */
    public Media createAudio() {
        return new Media(getClass().getClassLoader().getResource(PATH).toExternalForm());
    }
}
