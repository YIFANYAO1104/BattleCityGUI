package com.bham.bc.audio;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.MediaException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class AudioAutomatedTest {

    private Object[] volumeUpperBound() {
        return new Object[][]{
                { 1 },
                { 1.000001 },
                { 78.456 },
                { 9999 }
        };
    }

    private Object[] volumeLowerBound() {
        return new Object[][]{
                { 0 },
                { -0.000001 },
                { -4.894 },
                { -9999 }
        };
    }

    @Test
    public void shouldCreateAudioClipFromEveryEffect() {
        Arrays.stream(SoundEffect.values()).forEach(soundEffect -> {
            try {
                soundEffect.createAudio();
            } catch(NullPointerException | MediaException e) {
                System.err.println("Could not instantiate AudioClip from a path constructed by " + soundEffect + " enum value");
                fail();
            }
        });
    }

    @Test
    public void shouldCreateMediaFromEveryTrack() {
        Arrays.stream(SoundTrack.values()).forEach(soundTrack -> {
            try {
                soundTrack.createAudio();
            } catch(NullPointerException | MediaException e) {
                System.err.println("Could not instantiate Media from a path constructed by " + soundTrack + " enum value");
                fail();
            }
        });
    }

    @Test
    public void shouldHaveNoEffectOnNullPlayer() {
        AudioManager audioManager = new AudioManager();

        try {
            audioManager.playMusic();
        } catch(NullPointerException e) {
            System.err.println("playMusic() should have no effect on a non-loaded audio manager");
            fail();
        }

        try {
            audioManager.pauseMusic();
        } catch(NullPointerException e) {
            System.err.println("pauseMusic() should have no effect on a non-loaded audio manager");
            fail();
        }

        try {
            audioManager.stopMusic();
        } catch(NullPointerException e) {
            System.err.println("stopMusic() should have no effect on a non-loaded audio manager");
            fail();
        }

        try {
            audioManager.setMusicVolume(1);
        } catch(NullPointerException e) {
            System.err.println("setMusicVolume() should have no effect on a non-loaded audio manager");
            fail();
        }
    }

    @Test
    @Parameters(method = "volumeUpperBound")
    @TestCaseName("[{index}] Upper bound volume test: {params}")
    public void volumeShouldNotGoOverUpperBound(double volume) {
        new JFXPanel();
        AudioManager audioManager = new AudioManager();

        // We want to try changing music volume from when it is 0.5
        audioManager.setMusicVolume(0.5);
        assertEquals(0.5, audioManager.getMusicVolume(), 0.0);

        // We want to try changing effects volume from when it is 0.5
        audioManager.setEffectsVolume(0.5);
        assertEquals(0.5, audioManager.getEffectsVolume(), 0.0);

        // Now we do actual testing for music volume
        audioManager.setMusicVolume(volume);
        assertTrue(audioManager.getMusicVolume() <= 1);

        // Now we do actual testing for effects volume
        audioManager.setEffectsVolume(volume);
        assertTrue(audioManager.getEffectsVolume() <= 1);
    }

    @Test
    @Parameters(method = "volumeLowerBound")
    @TestCaseName("[{index}] Lower bound volume test: {params}")
    public void volumeShouldNotGoOverLowerBound(double volume) {
        new JFXPanel();
        AudioManager audioManager = new AudioManager();

        // We want to try changing music volume from when it is 0.5
        audioManager.setMusicVolume(0.5);
        assertEquals(0.5, audioManager.getMusicVolume(), 0.0);

        // We want to try changing effects volume from when it is 0.5
        audioManager.setEffectsVolume(0.5);
        assertEquals(0.5, audioManager.getEffectsVolume(), 0.0);

        // Now we do actual testing for music volume
        audioManager.setMusicVolume(volume);
        assertTrue(audioManager.getMusicVolume() >= 0);

        // Now we do actual testing for effects volume
        audioManager.setEffectsVolume(volume);
        assertTrue(audioManager.getEffectsVolume() >= 0);
    }
}
