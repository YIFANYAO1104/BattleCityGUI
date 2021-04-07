package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;


public class LandmineTrigger extends RespawnTrigger{


    public LandmineTrigger(int x,int y,int Respawn){
        super(x,y);
        setCooldown(Respawn*FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[]{ new Image("file:src/main/resources/img/tiles/triggers/Landmine.png")};
    }

    @Override
    public void handleCharacter(GameCharacter character) {
        if(active && intersects(character)){
            character.destroyed();
            deactivate();
        }
    }
}
