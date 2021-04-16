package com.bham.bc.audio;

import javafx.scene.media.Media;

/**
 * Enum for all tracks in the game
 */
public enum SoundTrack {
    BREAK("src/main/resources/audio/music/menu/night-break.mp3"),
    CORRUPTION("src/main/resources/audio/music/survival/corruption.mp3"),
    LEAD("src/main/resources/audio/music/survival/take-the-lead.mp3"),
    REVOLUTION("src/main/resources/audio/music/survival/voxel-revolution.mp3"),

    TEST1("audio/music/challenge/DeepSpaceB.mp3"),
    //TEST1("audio/sfx/select.wav"),
    TEST2("audio/music/challenge/RhytmicBounceA.mp3"),
    TEST3("audio/music/challenge/RhytmicBounceB.mp3");

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
