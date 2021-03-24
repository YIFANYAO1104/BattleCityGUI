package com.bham.bc.view.model;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.bham.bc.audio.AudioManager.audioManager;

/**
 * @author : YiFan Yaao
 * @version : 1.0
 * @project: BattleCityGUI
 * @name : MenuSlider.java
 * @data : 2021/3/24
 * @time : 19:54
 */
public class MenuSlider extends VBox{
    private Label volume;
    private HBox HBox;

    public MenuSlider(String name){
        volume=new Label(name);
        HBox=new HBox();
        getChildren().addAll(volume,HBox);
    }
    public DoubleProperty createMenuSlider(){
        Glow glow=new Glow();
        glow.setLevel(1);
        Slider volumeSlider = new Slider();
        volumeSlider.setValue(100);
        volumeSlider.setEffect(glow);

        Label num=new Label((int)volumeSlider.valueProperty().getValue().doubleValue()+"");
        num.setStyle(" -fx-font-size: 15px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;");
        num.setEffect(glow);
        HBox.getChildren().addAll(volumeSlider,num);

        volume.setEffect(glow);

        volume.setStyle(" -fx-font-size: 25px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;");

        return volumeSlider.valueProperty();
    }
}
