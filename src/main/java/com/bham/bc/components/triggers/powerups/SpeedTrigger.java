package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class SpeedTrigger extends RespawnTrigger {
    private int speed;


    public SpeedTrigger(int x, int y, int respawnCooldown, int speed) {
        super(x,y);
        this.speed = speed;
        setCooldown(respawnCooldown*FRAME_RATE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[]{new Image("file:src/main/resources/img/tiles/triggers/flash.png")};
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            //((GameCharacter) entity).speedUp(speed);
            deactivate();
        }
    }
}
