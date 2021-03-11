package com.bham.bc.audio;

import javafx.scene.media.Media;

import java.io.File;

/**
 * Enum for constructing Media files
 */
public enum TRACK {
    MENU("src/main/resources/audio/music/bg/bgm_14.wav"),
    STEM1("src/main/resources/audio/music/acp.wav"),
    STEM2("src/main/resources/audio/music/inst.wav");

    private Media media;

    /**
     * Constructs TRACK as Media object
     * @param path path to track
     */
    TRACK(String path) { media = new Media(new File(path).toURI().toString()); }

    /**
     * gets media reference
     * @return media object
     */
    public Media getMedia() { return media; }
}
