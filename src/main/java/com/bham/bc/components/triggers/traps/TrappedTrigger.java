package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class TrappedTrigger extends RespawnTrigger{

    public TrappedTrigger(int x, int y, int Respawn){
        super(x,y);
        setCooldown(Respawn*FRAME_RATE*2);
    }

    protected Image[] getDefaultImage() {
        return new Image[]{ new Image("file:src/main/resources/img/tiles/triggers/trap.png")};
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).setTRAPPED();
            deactivate();
        }
    }
}
