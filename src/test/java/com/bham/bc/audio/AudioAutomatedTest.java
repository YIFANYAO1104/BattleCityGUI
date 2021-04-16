package com.bham.bc.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import org.junit.Test;
import sun.audio.AudioPlayer;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.fail;

public class AudioAutomatedTest {

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
    public void volumeShouldNotGoOverUpperBound() {
        AudioManager audioManager = new AudioManager();
        audioManager.loadParallelPlayer(false, SoundTrack.CORRUPTION);
        audioManager.setMusicVolume(999);
        //ssertT
        //assert audioManager.getVolume() <= 1;
    }
}
