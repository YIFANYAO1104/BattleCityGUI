package com.bham.bc.view.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * <h1>Menu Slider</h1>
 *
 * <p>Custom menu slider which is represented through <i>HBox</i> node provided by JavaFX. This slider contains
 * slider body (track), slider ball (thumb), slider label (name) and its value label (%).</p>
 */
public class MenuSlider extends HBox {

    public static final int WIDTH = 440;
    public static final int HEIGHT = 50;

    private final Slider SLIDER;

    /**
     * Constructs a slider with its name and initial value
     *
     * @param name         label's name above the slider
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
        SLIDER.valueProperty().addListener((obsVal, oldVal, newVal) -> SLIDER.lookup(".track").setStyle(String.format("-fx-background-color: linear-gradient(to right, -fx-primary-color %d%%, -fx-secondary-color %d%%);", newVal.intValue(), newVal.intValue())));

        // Label for slider
        Label sliderLabel = new Label();
        sliderLabel.setText(name);
        sliderLabel.setPrefWidth(WIDTH*.25);
        sliderLabel.getStyleClass().add("slider-label");

        // Label for slider's value
        Label valueLabel = new Label();
        valueLabel.textProperty().bind(SLIDER.valueProperty().asString("%.0f%%"));
        valueLabel.getStyleClass().add("value-label");

        // Add all children
        getChildren().addAll(sliderLabel, SLIDER, valueLabel);
    }

    /**
     * Gets slider's value property which is used to change the audio volume
     * @return DoubleProperty object to be bind with audio volume changes
     */
    public DoubleProperty getValueProperty() {
        return SLIDER.valueProperty();
    }
}