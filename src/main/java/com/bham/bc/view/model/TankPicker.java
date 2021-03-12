package com.bham.bc.view.model;

/**
 * @author : YiFan Yaao
 * @version : 1.0
 * @project: BattleCityGUI
 * @name : TankPicker.java
 * @data : 2021/2/26
 * @time : 13:59
 */
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class TankPicker extends VBox {

    private ImageView circleImage;
    private ImageView TankImage;

    private String circleNotChoosen = "file:src/main/resources/GUIResources/grey_circle.png";
    private String circleChoosen = "file:src/main/resources/GUIResources/red_choosen.png";

    private TankType Tank;

    private boolean isCircleChoosen;


    public TankPicker(TankType Tank) {
        circleImage = new ImageView(circleNotChoosen);

        TankImage = new ImageView(Tank.getUrl());
        this.Tank = Tank;
        isCircleChoosen = false;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.getChildren().add(circleImage);
        this.getChildren().add(TankImage);

    }

    public TankType getTank() {
        return Tank;
    }

    public boolean getCircleChoosen() {
        return isCircleChoosen;
    }

    public void setIsCircleChoosen(boolean isCircleChoosen) {
        this.isCircleChoosen = isCircleChoosen;
        String imageToSet = this.isCircleChoosen ? circleChoosen : circleNotChoosen;
        circleImage.setImage(new Image(imageToSet));
    }
}
