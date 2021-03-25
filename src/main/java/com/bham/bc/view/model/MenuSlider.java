package com.bham.bc.view.model;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author : YiFan Yaao
 * @version : 1.0
 * @project: BattleCityGUI
 * @name : MenuSlider.java
 * @data : 2021/3/24
 * @time : 19:54
 */
public class MenuSlider extends VBox {
    private Slider slider;

    private Rectangle progressRec ;

    public MenuSlider(String name) {
        // Stylesheet for menu slider's elements
        getStylesheets().add(MenuSlider.class.getResource("../../../../../GUIResources/MenuSlider.css").toExternalForm());

        // The actual slider
        slider = new Slider();
        slider.setValue(100);
        slider.setEffect(new Glow(1));
        slider.setId("color-slider");

        // Label for slider
        Label sliderLabel = new Label();
        sliderLabel.setText(name);
        sliderLabel.setId("volume");

        // Label for slider's value
        Label valueLabel = new Label(slider.valueProperty().toString());
        valueLabel.textProperty().bind(slider.valueProperty().asString());
        valueLabel.setId("num");


        // Slider's body
        progressRec = new Rectangle();
        // Bind both width and height to match the size of Slider
        progressRec.heightProperty().bind(slider.heightProperty().subtract(7));
        progressRec.widthProperty().bind(slider.widthProperty());

        progressRec.setFill(Color.web("#2D819D"));


        // Make the corners of Rectangle to be rounded
        progressRec.setArcHeight(15);
        progressRec.setArcWidth(15);
        progressRec.setTranslateX(0);
        progressRec.setTranslateY(0);



        getChildren().addAll(sliderLabel);
        getChildren().add(progressRec);
        slider.setTranslateY(-15);
        getChildren().add(slider);
        getChildren().add(valueLabel);


    }


    public DoubleProperty getValueProperty(){
        return slider.valueProperty();
    }

    public void setRecStyle(Number newVal){
        // Using linear gradient we can fill two colors to show the progress
        // the new_val gets values between 0 - 100
        String style = String.format("-fx-fill: linear-gradient(to right, #2D819D %d%%, #969696 %d%%);",
                newVal.intValue(), newVal.intValue());
        // set the Style
        progressRec.setStyle(style);
    }
}
