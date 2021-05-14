package com.bham.bd.audio;

import javafx.scene.media.MediaException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * <h1>Automated audio tests</h1>
 *
 * <p>This class runs a couple of JUnits tests to check some instantiation of players and the volume parameters.</p>
 * <ul>
 *     <li>{@link #shouldCreateAudioClipFromEveryEffect()} - ensures an audio object for every {@link SoundEffect} can be created properly</li>
 *     <li>{@link #shouldCreateMediaFromEveryTrack()} - ensures an audio object for every {@link SoundTrack} can be created properly</li>
 *     <li>{@link #shouldHaveNoEffectOnNullPlayer()} - ensures no UI method call (e.g., play(), pause()) has effect on non-loaded {@link AudioManager}</li>
 *     <li>{@link #shouldNotCreateEmptySequentialPlayer(SoundTrack[])} - ensures {@link SequentialAudioPlayer} is not created on empty array of sound tracks</li>
 *     <li>{@link #shouldNotCreateEmptySequentialPlayer(SoundTrack[][])} - ensures {@link SequentialAudioPlayer} is not created on empty array of arrays of sound tracks</li>
 *     <li>{@link #shouldNotCreateEmptySequentialPlayer(SoundTrack[])} - ensures {@link ParallelAudioPlayer} is not created on empty array of sound tracks</li>
 *     <li>{@link #volumeUpperBound()} - ensures anything above 1 is set back to 1 for audio volume</li>
 *     <li>{@link #volumeLowerBound()} - ensures anything below 0 is set back to 0 for audio volume</li>
 * </ul>
 */
@RunWith(JUnitParamsRunner.class)
public class AudioAutomatedTest {

    /**
     * Provides an array of parameters to test audio manager's upper volume bound
     * @return array of possible values which should not set the volume over 1.0
     */
    private Object[] volumeUpperBound() {
        return new Object[][]{
                { 1 },
                { 1.000001 },
                { 78.456 },
                { 9999 }
        };
    }

    /**
     * Provides an array of parameters to test audio manager's lower volume bound
     * @return array of possible values which should not set the volume below 0.0
     */
    private Object[] volumeLowerBound() {
        return new Object[][]{
                { 0 },
                { -0.000001 },
                { -4.894 },
                { -9999 }
        };
    }

    /**
     * Provides an array of parameters to test 1D creation of an empty player
     * <p><b>Note:</b> we don't test for potential {@code null} values a sound track array might have:</p>
     * <ul>
     *     <li>{@code new SoundTrack[]{ null } }</li>
     *     <li>{@code new SoundTrack[]{ null, null } }</li>
     * </ul>
     * <p>This is because such errors are caught and we already did a test ensuring no value for any {@link SoundTrack} converted
     * to {@link javafx.scene.media.Media} is null.</p>
     *
     * @return array of possible values which should not load the player
     */
    private Object[] oneDimensionalEmptyPlayer() {
        return new Object[][]{
                { null },
                { new SoundTrack[]{} }
        };
    }

    /**
     * Provides an array of parameters to test 2D creation of an empty player
     * <p><b>Note:</b> we don't test for potential {@code null} values a sound track array might have:</p>
     * <ul>
     *     <li>{@code new SoundTrack[][]{ new SoundTrack[]{ null } } }</li>
     *     <li>{@code new SoundTrack[][]{ new SoundTrack[]{ null, null }, new SoundTrack[]{} } }</li>
     * </ul>
     * <p>This is because such errors are caught and we already did a test ensuring no value for any {@link SoundTrack} converted
     * to {@link javafx.scene.media.Media} is null.</p>
     *
     * @return array of possible values which should not load the player
     */
    private Object[] twoDimensionalEmptyPlayer() {
        return new Object[][]{
                { new SoundTrack[][]{} },
                { new SoundTrack[][]{ null } },
                { new SoundTrack[][]{ new SoundTrack[]{} } },
                { new SoundTrack[][]{ new SoundTrack[]{}, null } },
                { new SoundTrack[][]{ new SoundTrack[]{}, new SoundTrack[]{} } }
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
    @Parameters(method = "oneDimensionalEmptyPlayer")
    @TestCaseName("[{index}] Empty sequential player (1D) creation: {params}")
    public void shouldNotCreateEmptySequentialPlayer(SoundTrack[] soundTracks) {
        AudioManager audioManager = new AudioManager();
        audioManager.loadSequentialPlayer(false, soundTracks);
        assertNull(audioManager.getPlayer());
    }

    @Test
    @Parameters(method = "twoDimensionalEmptyPlayer")
    @TestCaseName("[{index}] Empty sequential player (2D) creation: {params}")
    public void shouldNotCreateEmptySequentialPlayer(SoundTrack[][] soundTracks) {
        AudioManager audioManager = new AudioManager();
        audioManager.loadSequentialPlayer(false, soundTracks);
        assertNull(audioManager.getPlayer());
    }

    @Test
    @Parameters(method = "oneDimensionalEmptyPlayer")
    @TestCaseName("[{index}] Empty parallel player (1D) creation: {params}")
    public void shouldNotCreateEmptyParallelPlayer(SoundTrack[] soundTracks) {
        AudioManager audioManager = new AudioManager();
        audioManager.loadParallelPlayer(false, soundTracks);
        assertNull(audioManager.getPlayer());
    }

    @Test
    @Parameters(method = "volumeUpperBound")
    @TestCaseName("[{index}] Upper bound volume test: {params}")
    public void volumeShouldNotGoOverUpperBound(double volume) {
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
