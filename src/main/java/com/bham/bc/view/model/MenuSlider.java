package com.bham.bc.view.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Represents the custom menu slider with labels for the slider name and its value
 */
public class MenuSlider extends VBox {

    public static final int WIDTH = 360;
    public static final int HEIGHT = 80;

    private final Slider SLIDER;

    /**
     * Constructs a slider with its name and initial value
     *
     * @param name         label's name above the slider
     * @param initialValue value the slider should be set to initially
     */
    public MenuSlider(String name, int initialValue) {
        setMaxWidth(WIDTH);
        setMaxHeight(HEIGHT);

        // The actual slider
        SLIDER = new Slider();
        SLIDER.setValue(initialValue);
        SLIDER.setEffect(new Glow(.6));
        SLIDER.setId("slider");
        SLIDER.valueProperty().addListener((obsVal, oldVal, newVal) -> SLIDER.lookup(".track").setStyle(String.format("-fx-background-color: linear-gradient(to right, -fx-primary-color %d%%, -fx-secondary-color %d%%);", newVal.intValue(), newVal.intValue())));

        // Label for slider
        Label sliderLabel = new Label();
        sliderLabel.setText(name);
        sliderLabel.setId("slider-label");

        // Label for slider's value
        Label valueLabel = new Label();
        valueLabel.textProperty().bind(SLIDER.valueProperty().asString("%.0f%%"));
        valueLabel.setId("value-label");

        // Container for slider and its value
        HBox sliderAndValue = new HBox(SLIDER, valueLabel);

        // Add all children
        getChildren().addAll(sliderLabel, sliderAndValue);
    }

    /**
     * Gets slider's value property which is used to change the audio volume
     * @return DoubleProperty object to be bind with audio volume changes
     */
    public DoubleProperty getValueProperty() {
        return SLIDER.valueProperty();
    }
}
