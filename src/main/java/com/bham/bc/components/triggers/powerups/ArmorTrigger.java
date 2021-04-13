package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

public class ArmorTrigger extends  RespawnTrigger{

    private int HP;

    public ArmorTrigger(int x, int y, int HP, int Respawn){
        super(x,y);
        this.HP = HP;
        setCooldown(Respawn*FRAME_RATE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[]{ new Image("file:src/main/resources/img/tiles/triggers/armor.png")};
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            //entity.armorUP(HP);
            deactivate();
        }
    }
}
