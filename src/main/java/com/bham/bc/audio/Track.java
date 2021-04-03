package com.bham.bc.audio;

import javafx.scene.media.Media;

import java.io.File;

/**
 * Enum for constructing Media files
 * TODO: consider adding "TRACK" prefix for every name
 */
public enum Track {
    // Menu soundtracks
    BREAK("src/main/resources/audio/music/menu/night-break.mp3"),

    // Survival mode soundtracks
    CORRUPTION("src/main/resources/audio/music/survival/corruption.mp3"),
    LEAD("src/main/resources/audio/music/survival/take-the-lead.mp3"),
    REVOLUTION("src/main/resources/audio/music/survival/voxel-revolution.mp3"),

    // Challenge mode soundtracks
    STEM1("src/main/resources/audio/music/challenge/rule-the-world-acapella.wav"),
    STEM2("src/main/resources/audio/music/challenge/rule-the-world-instruments.wav");

    private Media media;

    /**
     * Constructs TRACK as Media object
     * @param path path to track
     */
    Track(String path) { media = new Media(new File(path).toURI().toString()); }

    /**
     * gets media reference
     * @return media object
     */
    public Media getMedia() { return media; }
}
