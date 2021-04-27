package com.bham.bc.view.model;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * <h1>Menu Slider</h1>
 *
 * <p>Custom menu slider which is represented through <i>HBox</i> node provided by JavaFX. This slider contains
 * slider body (track), slider ball (thumb), slider label (name) and its value label (%).</p>
 */
public class MenuSlider extends HBox {
    /**
     * width of custom slider
     */
    public static final int WIDTH = 480;
    /**
     * height of custom slider
     */
    public static final int HEIGHT = 50;

    /**
     * instance of Slider
     */
    private final Slider SLIDER;

    /**
     * Constructs a slider with its name and initial value.
     *
     * @param name         label's name to the left of the slider
     * @param initialValue value the slider should be set to initially
     */
    public MenuSlider(String name, int initialValue) {
        setMaxWidth(WIDTH);
        setPrefHeight(HEIGHT);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(20);
        setPadding(new Insets(0, 0, 0, 15));
        getStyleClass().add("menu-slider");

        // The actual slider
        SLIDER = new Slider();
        SLIDER.setValue(initialValue);
        SLIDER.setEffect(new Glow(.8));
        SLIDER.setPrefWidth(WIDTH*.5);
        SLIDER.getStyleClass().add("slider");

        // Slider track
        StackPane sliderTrack = new StackPane();
        sliderTrack.setMaxSize(WIDTH*.46, HEIGHT*.18);
        sliderTrack.getStyleClass().add(".slider-track");
        sliderTrack.setStyle(
                "-fx-background-radius: 20px;" + "-fx-border-width: 0;" + "-fx-effect: innershadow(gaussian, -fx-darken-color, 4, .1, 0, 0);" +
                String.format("-fx-background-color: linear-gradient(to right, -fx-primary-color %d%%, -fx-secondary-color %d%%);", initialValue, initialValue));
        SLIDER.valueProperty().addListener((obsVal, oldVal, newVal) -> sliderTrack.setStyle(
                "-fx-background-radius: 20px;" + "-fx-border-width: 0;" + "-fx-effect: innershadow(gaussian, -fx-darken-color, 4, .1, 0, 0);" +
                String.format("-fx-background-color: linear-gradient(to right, -fx-primary-color %d%%, -fx-secondary-color %d%%);", newVal.intValue(), newVal.intValue())));

        // Container for the slider and its track
        StackPane sliderContainer = new StackPane();
        sliderContainer.setAlignment(Pos.CENTER);
        sliderContainer.getChildren().addAll(sliderTrack, SLIDER);

        // Label for slider
        Label sliderLabel = new Label();
        sliderLabel.setText(name);
        sliderLabel.setPrefWidth(WIDTH*.26);
        sliderLabel.getStyleClass().add("slider-label");

        // Label for slider's value
        Label valueLabel = new Label();
        valueLabel.textProperty().bind(SLIDER.valueProperty().asString("%.0f%%"));
        valueLabel.getStyleClass().add("value-label");

        // Add all children
        getChildren().addAll(sliderLabel, sliderContainer, valueLabel);
    }

    /**
     * Gets slider's value property which is used to change the audio volume.
     * @return DoubleProperty object to be bind with audio volume changes
     */
    public DoubleProperty getValueProperty() {
        return SLIDER.valueProperty();
    }
}
