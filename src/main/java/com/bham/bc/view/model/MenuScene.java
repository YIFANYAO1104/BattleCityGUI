package com.bham.bc.view.model;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MenuScene extends VBox {
    private AnchorPane parent;

    public MenuScene(AnchorPane parent) {
        super(15);
        this.parent = parent;
        setTranslateX(parent.getWidth());
        setTranslateY(parent.getHeight()/3);
    }

    public void show() {
        if(!parent.getChildren().contains(this)) parent.getChildren().add(this);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), this);
        tt.setToX(parent.getWidth()/2 - getWidth()/2);
        tt.play();
    }

    public void hide() {
        TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), this);
        tt1.setToX(parent.getWidth());
        tt1.play();
        tt1.setOnFinished(e -> parent.getChildren().remove(this));
    }

    public void setWidth1(double width) {
        this.setWidth(width);
    }
}
