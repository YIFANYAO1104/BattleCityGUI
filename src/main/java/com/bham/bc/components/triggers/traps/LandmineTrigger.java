package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;


public class LandmineTrigger extends RespawnTrigger{


    public LandmineTrigger(int x,int y,int Respawn){
        super(x,y);
        setCooldown(Respawn*FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[]{ new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/traps/Landmine.png"))};
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            ((GameCharacter) entity).changeHp(-200);
            deactivate();
        }
    }
}
