package com.bham.bc.view.model;

import com.bham.bc.view.CustomStage;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
    private Slider volumeSlider;
    private Label num;
    private Rectangle progressRec ;

    public MenuSlider(String name){

        volume=new Label();
        volume.setText(name);
        volume.setId("volume");
        HBox=new HBox();
        getChildren().addAll(volume,HBox);
        Glow glow=new Glow();
        glow.setLevel(1);
        volumeSlider = new Slider();
        volumeSlider.setValue(100);
        volumeSlider.setEffect(glow);


        num=new Label((int)volumeSlider.valueProperty().getValue().doubleValue()+"");
        num.setId("num");

        num.setEffect(glow);


        volume.setEffect(glow);
        volumeSlider.setId("color-slider");
        getStylesheets().add(MenuSlider.class.getResource("../../../../../GUIResources/MenuSlider.css").toExternalForm());




        // The rectangle which shows the progress
        progressRec = new Rectangle();
        // Bind both width and height to match the size of Slider
        progressRec.heightProperty().bind(volumeSlider.heightProperty().subtract(7));
        progressRec.widthProperty().bind(volumeSlider.widthProperty());

        progressRec.setFill(Color.web("#2D819D"));


        // Make the corners of Rectangle to be rounded
        progressRec.setArcHeight(15);
        progressRec.setArcWidth(15);
        progressRec.setTranslateX(0);
        progressRec.setTranslateY(-16);
        progressRec.setEffect(glow);

        HBox.setSpacing(20);
        HBox.getChildren().addAll(volumeSlider,num);
        getChildren().add(progressRec);




    }
    public DoubleProperty getValueProperty(){


        return volumeSlider.valueProperty();
    }
    public Label getLabelOfVolume(){
        return num;
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
