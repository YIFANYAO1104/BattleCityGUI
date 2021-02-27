package com.bham.bc.audio;

/**
 * Desc: A simple interface to define behaviour of an audio player.
 * Commands are issued on separate threads as background activities for better performance.
 */
public interface AudioPlayer {
    /**
     * executes start command for player(-s) on a separate thread
     */
    void play();

    /**
     * executes pause command for player(-s) on a separate thread
     */
    void pause();

    /**
     * executes stop command for player(-s) on a separate thread
     */
    void stop();

    /**
     * sets volume for player(-s) on a separate thread
     * @param volume volume to set
     */
    void setVolume(double volume);
}
