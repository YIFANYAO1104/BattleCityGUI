package com.bham.bc.audio;

import javafx.scene.media.MediaPlayer;

/**
 * Class to analyse any music using spectrum listener
 */
public class AudioAnalyser {
    MediaPlayer track;
    float[] frequencies;

    /**
     * Constructs 1 analyser for 1 media track
     * @param track media player to analyse
     * @param interval amount of time between checking frequency values
     * @param numBands amount of frequency ranges
     */
    public AudioAnalyser(MediaPlayer track, double interval, int numBands) {
        frequencies = new float[numBands];
        this.track = track;
        this.track.setAudioSpectrumInterval(interval);
        this.track.setAudioSpectrumNumBands(numBands);
        this.track.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> updateFrequencies(magnitudes));

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
    public float[] getFrequencies() {
        return frequencies;
    }
}
