package com.bham.bc.audio;

import javafx.scene.media.MediaPlayer;

/**
 * Class to analyse any music using spectrum listener
 */
public class AudioAnalyser {
    MediaPlayer player;
    float[] frequencies;

    /**
     * Constructs 1 analyser for 1 media player
     * @param player media player to analyse
     * @param interval amount of time between checking frequency values
     * @param numBands amount of frequency ranges (usually 128 or 256)
     */
    public AudioAnalyser(MediaPlayer player, double interval, int numBands) {
        frequencies = new float[numBands];
        this.player = player;
        this.player.setAudioSpectrumInterval(interval);
        this.player.setAudioSpectrumNumBands(numBands);
        this.player.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> updateFrequencies(magnitudes));

    }

    /**
     * updates frequency values
     * @param magnitudes values acquired from spectrum listener
     */
    private void updateFrequencies(float[] magnitudes) {
        for(int i = 0; i < magnitudes.length; i++) {
            frequencies[i] = (magnitudes[i] + 60) * 2;
        }
    }

    /**
     * gets current frequency values
     * @return array of frequencies
     */
    public float[] getFrequencies() { return frequencies; }
}
