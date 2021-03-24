package com.bham.bc.view.model;

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
public class MenuSlider {
    public VBox vBox;

    public VBox createMenuSlider(String whichVolume){
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

        volumeSlider.valueProperty().addListener((obsVal, oldVal, newVal) -> {
            //SFX
            if (whichVolume.equals("SFX")){
                audioManager.setEffectVolume(newVal.doubleValue()/100);
            }else if (whichVolume.equals("bgMusic")){
                audioManager.setMusicVolume(newVal.doubleValue()/100);
            }

            num.setText(newVal.intValue() + "");
        });
        num.setEffect(glow);
        HBox HBox=new HBox(volumeSlider,num);

        Label volume=new Label("SFX Volume:");
        volume.setEffect(glow);

        volume.setStyle(" -fx-font-size: 25px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;");
        vBox=new VBox(volume,HBox);
        return vBox;
    }
}
