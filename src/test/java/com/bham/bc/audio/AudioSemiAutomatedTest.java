package com.bham.bc.audio;

import com.bham.bc.application.TestApplication;
import com.bham.bc.view.GameSession;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.junit.Test;

import static junit.framework.TestCase.fail;

/**
 * <h1>Semi-automated audio tests</h1>
 *
 * <p>This class requires user interaction to verify the audio manager functions are working correctly.</p>
 * <ul>
 *     <li>{@link #loopPropertyShouldWork()} - to check how loop property works on each player</li>
 *     <li>{@link #shouldFinishOnLastStem()} - to check if the parallel player's {@code onEndOfAudio} property works correctly</li>
 *     <li>{@link #hasEffectOnAudio()} - to check if audio manager responds correctly to its method calls</li>
 * </ul>
 */
public class AudioSemiAutomatedTest {

    /**
     * Gets the layout to test 3 types of player with loop property off and the same 3 types of players with
     * loop property off. Stopping the player and controlling audio here is also possible.
     * @return layout with 6 buttons which start a player, 1 which stops it and a slider to control volume
     */
    private VBox getLoopPlayground() {
        new JFXPanel();
        AudioManager audioManager = new AudioManager();

        // Description
        Label description = new Label("Check if loop property works well for the provided test set of songs");

        // Label to indicate non-loop buttons
        Label loopOffLabel = new Label("Should not restart playing after songs end:");

        // Buttons which play tracks just once
        Button seqLoopOff1D = new Button("Play 2 songs in sequence 1D");
        Button seqLoopOff2D = new Button("Play 1 batch in sequence 2D");
        Button parLoopOff1D = new Button("Play 2 songs in parallel 1D");

        seqLoopOff1D.setOnMouseClicked(e -> {
            audioManager.loadSequentialPlayer(false, SoundTrack.DEEP_SPACE, SoundTrack.RHYTHMIC_BOUNCE);
            audioManager.playMusic();
        });

        seqLoopOff2D.setOnMouseClicked(e -> {
            audioManager.loadSequentialPlayer(false, new SoundTrack[][]{ new SoundTrack[]{ SoundTrack.DEEP_SPACE, SoundTrack.RHYTHMIC_BOUNCE }, });
            audioManager.playMusic();
        });

        parLoopOff1D.setOnMouseClicked(e -> {
            audioManager.loadParallelPlayer(false, SoundTrack.DEEP_SPACE, SoundTrack.RHYTHMIC_BOUNCE);
            audioManager.playMusic();
        });

        // Layout for non-loop buttons
        VBox noLoopLayout = new VBox();
        noLoopLayout.setSpacing(60);
        noLoopLayout.setAlignment(Pos.CENTER);
        noLoopLayout.setPrefWidth(GameSession.WIDTH/2.);
        noLoopLayout.getChildren().addAll(loopOffLabel, seqLoopOff1D, seqLoopOff2D, parLoopOff1D);

        // Label to indicate loop buttons
        Label loopOnLabel = new Label("Should restart playing after songs end:");

        // Buttons which play tracks endlessly
        Button seqLoopOn1D = new Button("Play 2 songs in sequence 1D");
        Button seqLoopOn2D = new Button("Play 1 batch in sequence 2D");
        Button parLoopOn1D = new Button("Play 2 songs in parallel 1D");

        seqLoopOn1D.setOnMouseClicked(e -> {
            audioManager.loadSequentialPlayer(true, SoundTrack.DEEP_SPACE, SoundTrack.RHYTHMIC_BOUNCE);
            audioManager.playMusic();
        });

        seqLoopOn2D.setOnMouseClicked(e -> {
            audioManager.loadSequentialPlayer(true, new SoundTrack[][]{ new SoundTrack[]{ SoundTrack.DEEP_SPACE, SoundTrack.RHYTHMIC_BOUNCE }, });
            audioManager.playMusic();
        });

        parLoopOn1D.setOnMouseClicked(e -> {
            audioManager.loadParallelPlayer(true, SoundTrack.DEEP_SPACE, SoundTrack.RHYTHMIC_BOUNCE);
            audioManager.playMusic();
        });

        // Layout for loop buttons
        VBox loopLayout = new VBox();
        loopLayout.setSpacing(60);
        loopLayout.setAlignment(Pos.CENTER);
        loopLayout.setPrefWidth(GameSession.WIDTH/2.);
        loopLayout.getChildren().addAll(loopOnLabel, seqLoopOn1D, seqLoopOn2D, parLoopOn1D);

        // Layout for loop and non-loop layouts
        HBox noLoopAndLoopLayout = new HBox();
        noLoopAndLoopLayout.getChildren().addAll(noLoopLayout, loopLayout);

        // Stop button
        Button stopBtn = new Button("Stop");
        stopBtn.setOnMouseClicked(e -> audioManager.stopMusic());

        // Volume slider
        Slider volumeSld = new Slider();
        volumeSld.setMaxWidth(200);
        volumeSld.valueProperty().set(audioManager.getMusicVolume() * 100);
        volumeSld.valueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));

        // Container for the whole layout
        VBox container = new VBox();
        container.setPrefSize(GameSession.WIDTH, GameSession.HEIGHT);
        container.setPadding(new Insets(50, 30, 50, 30));
        container.setSpacing(40);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(description, noLoopAndLoopLayout, stopBtn, volumeSld);

        return container;
    }

    /**
     * Gets layout with a button which starts a parallel player with loop property on
     * @return layout with start and stop buttons and a slider to control music volume
     */
    public VBox getParallelPlayground() {
        new JFXPanel();
        AudioManager audioManager = new AudioManager();

        // Description
        Label description = new Label("Check if parallel player finishes after the longest track\nends and restarts playing only after that");
        description.setTextAlignment(TextAlignment.CENTER);

        // Start button
        Button startBtn = new Button("Play 2 songs in parallel (loop enabled)");

        startBtn.setOnMouseClicked(e -> {
            audioManager.loadParallelPlayer(true, SoundTrack.DEEP_SPACE, SoundTrack.RHYTHMIC_BOUNCE);
            audioManager.playMusic();
        });

        // Stop button
        Button stopBtn = new Button("Stop the player");
        stopBtn.setOnMouseClicked(e -> audioManager.stopMusic());

        // Volume slider
        Slider volumeSld = new Slider();
        volumeSld.setMaxWidth(200);
        volumeSld.valueProperty().set(audioManager.getMusicVolume() * 100);
        volumeSld.valueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));

        // Container for all elements
        VBox container = new VBox();
        container.setPrefSize(GameSession.WIDTH, GameSession.HEIGHT);
        container.setPadding(new Insets(50, 30, 50, 30));
        container.setSpacing(40);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(description, startBtn, stopBtn, volumeSld);

        return container;
    }

    /**
     * Gets layout to test audio player functions (e.g., playMusic(), setVolume())
     * @return layout with start and stop buttons and a slider to control music volume
     */
    public VBox getInputPlayground() {
        new JFXPanel();
        AudioManager audioManager = new AudioManager();
        audioManager.loadSequentialPlayer(false, SoundTrack.REVOLUTION);

        // Description
        Label description = new Label("Check if all buttons behave appropriately, try out different volume values");
        description.setTextAlignment(TextAlignment.CENTER);

        // Play effect button
        Button effectBtn = new Button("Play effect");
        effectBtn.setOnMouseClicked(e -> audioManager.playEffect(SoundEffect.SELECT));

        // Play music button
        Button playBtn = new Button("Play music");
        playBtn.setOnMouseClicked(e -> audioManager.playMusic());

        // Pause music  button
        Button pauseBtn = new Button("Pause music");
        pauseBtn.setOnMouseClicked(e -> audioManager.pauseMusic());

        // Stop music button
        Button stopBtn = new Button("Stop music");
        stopBtn.setOnMouseClicked(e -> audioManager.stopMusic());

        // Music volume numeric text field
        TextField musicVolumeField = new TextField();
        musicVolumeField.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("[0-9]*") ? change : null));

        // Music volume button
        Button setMusicVolume = new Button("Set % music volume");
        setMusicVolume.setOnMouseClicked(e -> { audioManager.setMusicVolume(Integer.parseInt(musicVolumeField.getText())*.01); musicVolumeField.clear(); });

        // Layout for music volume button and input
        HBox musicVolumeLayout = new HBox(musicVolumeField, setMusicVolume);
        musicVolumeLayout.setSpacing(40);
        musicVolumeLayout.setAlignment(Pos.CENTER);

        // Music volume numeric text field
        TextField effectsVolumeField = new TextField();
        effectsVolumeField.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("[0-9]*") ? change : null));

        // Music volume button
        Button setEffectsVolume = new Button("Set % effects volume");
        setEffectsVolume.setOnMouseClicked(e -> { audioManager.setEffectsVolume(Integer.parseInt(effectsVolumeField.getText())*.01); effectsVolumeField.clear(); });

        // Layout for effects volume button and input
        HBox effectsVolumeLayout = new HBox(effectsVolumeField, setEffectsVolume);
        effectsVolumeLayout.setSpacing(40);
        effectsVolumeLayout.setAlignment(Pos.CENTER);

        // Container for all elements
        VBox container = new VBox();
        container.setPrefSize(GameSession.WIDTH, GameSession.HEIGHT);
        container.setPadding(new Insets(50, 30, 50, 30));
        container.setSpacing(40);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(description, effectBtn, playBtn, pauseBtn, stopBtn, effectsVolumeLayout, musicVolumeLayout);

        return container;
    }

    @Test
    public void loopPropertyShouldWork() {
        TestApplication testApplication = new TestApplication();
        testApplication.setTestPane(getLoopPlayground());
        testApplication.run();

        if(testApplication.getFailed()) fail();
    }

    @Test
    public void shouldFinishOnLastStem() {
        TestApplication testApplication = new TestApplication();
        testApplication.setTestPane(getParallelPlayground());
        testApplication.run();

        if(testApplication.getFailed()) fail();
    }

    @Test
    public void hasEffectOnAudio() {
        TestApplication testApplication = new TestApplication();
        testApplication.setTestPane(getInputPlayground());
        testApplication.run();

        if(testApplication.getFailed()) fail();
    }
}

