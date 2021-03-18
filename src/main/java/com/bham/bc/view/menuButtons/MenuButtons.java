package com.bham.bc.view.menuButtons;

import com.bham.bc.audio.MusicThread;


import com.bham.bc.view.menu.CustomMenuSubscene;
import com.bham.bc.view.menu.PauseMenu;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.net.MalformedURLException;

/**
 * @author : YiFan Yaao
 * @version : 1.0
 * @project: BattleCityGUI
 * @name : MenuParent.java
 * @data : 2021/2/27
 * @time : 9:54
 */
public class MenuButtons extends Button {
    private ButtonSubscene buttonSubscene;


    public MenuButtons(String name){
        super(name);
    }

    public MenuButtons(){
        super();
    }


    public void createButtonSubscene(AnchorPane root){
        buttonSubscene=new ButtonSubscene();
        root.getChildren().add(buttonSubscene);
    }





    public ButtonSubscene initialization(AnchorPane root){
        createButtonSubscene(root);
        return buttonSubscene;



    }



    public static void showSubScene(ButtonSubscene subscene){
        if (CustomMenuSubscene.sceneToHide!=null){
            CustomMenuSubscene.sceneToHide.moveSubcene();

        }
        if (subscene== PauseMenu.chooseSubScene){
            subscene.moveSubcene("choose");

        }else {
            subscene.moveSubcene();

        }

        CustomMenuSubscene.sceneToHide=subscene;

    }


    public static void clickVoice() throws MalformedURLException {
        MusicThread musicThread=new MusicThread("src/main/resources/GUIResources/clickVoice.wav");
        musicThread.start();

    }
    public static void clickeOnVoice(){
        MusicThread musicThread=new MusicThread("src/main/resources/GUIResources/isClickedOn.wav");
        musicThread.start();
    }
}
