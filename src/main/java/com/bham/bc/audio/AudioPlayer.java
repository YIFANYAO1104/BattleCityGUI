package com.bham.bc.audio;

/**
 * A simple interface to define behaviour of an audio player.
 */
interface AudioPlayer {
    /**
     * Checks if audio player is playing
     * @return true if player is playing and false if it is paused or stopped
     */
    boolean isPlaying();

    /**
     * Executes start command for player(-s)
     */
    void play();

    /**
     * Executes pause command for player(-s)
     */
    void pause();

    /**
     * Executes stop command for player(-s)
     */
    void stop();

    /**
     * Sets volume for player(-s)
     *
     * <p><b>Note:</b> we don't need to assert the volume is within range <i>[0, 1]</i> because {@link AudioManager} will ensure
     * it is withing those bounds. Even if not, {@link javafx.scene.media.MediaPlayer#clamp(double, double, double)} ensures
     * the proper range.</p>
     *
     * @param volume value between 0 and 1 indicating new volume
     */
    void setVolume(double volume);

    /**
     * Sets a method to run after the audio ends
     * @param value runnable value to be set as <i>ON_END</i> property
     */
    void setOnEndOfAudio(Runnable value);

    /**
     * Gets a method to run after the audio ends
     * @return runnable value as <i>ON_END</i> property or {@code null} if it is not set
     */
    Runnable getOnEndOfAudio();
}
