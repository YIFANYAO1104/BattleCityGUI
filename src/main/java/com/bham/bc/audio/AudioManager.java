package com.bham.bc.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>Manages all audio in the game</h1>
 *
 * <p>This class is used to play sound effects, load any songs to play them in parallel or sequentially, pause and stop any
 * currently playing music. This class uses two helper classes: {@link ParallelAudioPlayer} and {@link SequentialAudioPlayer}
 * which have protected access within the package. No other cass except for this should have access to them. This is to
 * ensure they are instantiated correctly and this class handles that.</p>
 *
 * <p><b>Note:</b> this class can also play parallel batches of songs sequentially but it is never the case we'd want to
 * play sequential batches of songs parallel, therefore this class does not support that.</p>
 */
public class AudioManager {
    public static AudioManager audioManager = new AudioManager();

    private final EnumMap<SoundEffect, AudioClip> SFX;
    private static AudioPlayer player;
    private double effectsVolume;
    private double musicVolume;


    /**
     * Constructs audio manager and sets initial volume to 100%
     */
    public AudioManager() {
        effectsVolume = .7;
        musicVolume = .3;
        SFX = createSFX();
    }

    /**
     * Gets the sequential or parallel player audio manager uses
     * <p><b>Note:</b> classes outside audio package cannot access it - it is mainly used for testing</p>
     * @return currently used {@link AudioPlayer} or {@code null} if it is not loaded
     */
    protected AudioPlayer getPlayer() {
        return player;
    }

    /**
     * Creates an enum set of audio clips
     *
     * <p>Maps all values of {@link SoundEffect} to corresponding audio files acquired by enum's <i>createAudio()</i>
     * method. If that method fails to create an audio clip, the set simply skips it.</p>
     *
     * @return an {@code EnumMap} of sound effects as keys and audio clips as values
     */
    private EnumMap<SoundEffect, AudioClip> createSFX() {
        EnumMap<SoundEffect, AudioClip> sfxMap = new EnumMap<>(SoundEffect.class);

        Arrays.stream(SoundEffect.values()).forEach(sfx -> {
            try {
                sfxMap.put(sfx, sfx.createAudio());
            } catch(NullPointerException | MediaException e) {
                e.printStackTrace();
            }
        });

        return sfxMap;
    }

    /**
     * Creates media files from a given sound track array
     *
     * <p>Goes through each enum value (if any) in the provided array and maps them to corresponding audio files acquired
     * by enum's <i>createAudio()</i> method. If that method fails to create a media file, the list simply skips it.</p>
     *
     * @param soundTracks array of {@link SoundTrack} values to be converted to media files
     * @return an {@code ArrayList} of sound tracks as media objects
     */
    private ArrayList<Media> createTracks(SoundTrack[] soundTracks) {
        return soundTracks == null ? new ArrayList<>() : Arrays.stream(soundTracks).map(track -> {
            try{
                return track.createAudio();
            } catch(NullPointerException | MediaException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Prepares to play a list of audio players in sequence with a possibility to loop indefinitely
     * @param players list of players that will be put in a sequential loop
     */
    private void loadSequence(boolean loop, List<AudioPlayer> players) {
        player = players.size() == 0 ? null : players.get(0);

        for (int i = 0; i < players.size() - (loop ? 0 : 1); i++) {
            AudioPlayer curr = players.get(i);
            AudioPlayer next = players.get((i + 1) % players.size());

            curr.setOnEndOfAudio(() -> {
                curr.stop();
                next.setVolume(musicVolume);
                next.play();
                player = next;
            });
        }
    }

    /**
     * Prepares to play the provided tracks sequentially
     * @param loop        true or false indicating if the player should start over if it has finished
     * @param soundTracks enum values to be converted to media files
     */
    public void loadSequentialPlayer(boolean loop, SoundTrack... soundTracks) {
        if(player != null && player.isPlaying()) player.stop();
        ArrayList<Media> mediaList = createTracks(soundTracks);

        if(mediaList.size() == 0) {
            player = null;
            return;
        }

        loadSequence(loop, Arrays.asList(new SequentialAudioPlayer(mediaList)));
        if(player != null) player.setVolume(musicVolume);
    }

    /**
     * Prepares to play given batches (each song in a batch is played in parallel) in sequence
     *
     * <p><b>Note:</b> as this method overloads {@link #loadSequentialPlayer(boolean, SoundTrack...)}, if we want to create just
     * one batch in a sequence, we would need to pass in an extra empty array. This is redundant, instead we should use a method
     * {@link #loadParallelPlayer(boolean, SoundTrack...)} specifically for just one batch.</p>
     *
     * @param loop              true or false indicating if the sequence should start playing again after it ended
     * @param soundTrackBatches arrays of sound tracks to be played one by one (when one bach of tracks ends playing, another starts)
     */
    public void loadSequentialPlayer(boolean loop, SoundTrack[]... soundTrackBatches) {
        if(player != null && player.isPlaying()) player.stop();
        Stream<ArrayList<Media>> listStream = Arrays.stream(soundTrackBatches).map(this::createTracks).filter(list -> !list.isEmpty());
        loadSequence(loop, listStream.map(ParallelAudioPlayer::new).collect(Collectors.toList()));
        if(player != null) player.setVolume(musicVolume);
    }

    /**
     * Prepares to play the provided tracks in parallel
     * @param loop        true or false indicating if the player should start over if it has finished
     * @param soundTracks enum values to be converted to media files
     */
    public void loadParallelPlayer(boolean loop, SoundTrack... soundTracks) {
        if(player != null && player.isPlaying()) player.stop();
        loadSequentialPlayer(loop, soundTracks, new SoundTrack[]{});
        if(player != null) player.setVolume(musicVolume);
    }


    /**
     * Plays a sound effect if it is contained in the <b>SFX</b> set
     * @param effect type of {@link SoundEffect} to be played
     */
    public void playEffect(SoundEffect effect) {
        if(SFX.containsKey(effect)) {
            SFX.get(effect).play();
        }
    }

    /**
     * Plays the current player if it is set up
     */
    public void playMusic() {
        if(player != null) player.play();
    }

    /**
     * Pauses the current player if it is set up
     */
    public void pauseMusic() {
        if(player != null) player.pause();
    }

    /**
     * Stops the current player if it is set up
     */
    public void stopMusic() {
        if(player != null) player.stop();
    }

    /**
     * Sets volume for the current player on a new thread to interfere less with the running thread
     * @param volume value between 0 and 1 indicating a new balance of the current player
     */
    public void setMusicVolume(double volume) {
        musicVolume = Math.max(0, Math.min(1, volume));
        new Thread(() -> { if(player != null) player.setVolume(musicVolume); }).start();
    }

    /**
     * Sets volume for every sound effect on a new thread to interfere less with the running thread
     * @param volume value between 0 and 1 indicating a new balance of each sound effect
     */
    public void setEffectsVolume(double volume) {
        effectsVolume = Math.max(0, Math.min(1, volume));
        new Thread(() -> SFX.values().forEach(sfx -> sfx.setVolume(effectsVolume))).start();
    }

    /**
     * Gets the current music volume
     * @return value between 0 and 1 indicating the current volume of the player
     */
    public double getMusicVolume() {
        return musicVolume;
    }

    /**
     * Gets the current effects volume
     * @return value between 0 and 1 indicating the current volume of the effects
     */
    public double getEffectsVolume() {
        return effectsVolume;
    }
}
