package com.bham.bc.view.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private StackPane trackPane;



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


        HBox hBox=new HBox(slider,valueLabel);

        getChildren().addAll(sliderLabel);
        getChildren().add(hBox);





    }


    public DoubleProperty getValueProperty(){
        return slider.valueProperty();
    }

    public void setSliderStyle(){
        trackPane = (StackPane) slider.lookup(".track");

        trackPane.setStyle("-fx-background-color: linear-gradient(to right, #c17e1c 0%, #f3eace 0%);");





        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {

                String style = String.format("-fx-background-color: linear-gradient(to right,#c17e1c %d%%, #f3eace %d%%);",
                        new_val.intValue(), new_val.intValue());
                trackPane.setStyle(style);
            }
        });

         }
}
