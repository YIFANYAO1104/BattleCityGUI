package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

public class InverseTrap extends RespawnTrigger{
	private int activationTime;

    public InverseTrap(int x, int y, int activationTime, int respawnCooldown){
        super(x,y);
        this.activationTime = activationTime * FRAME_RATE;
        setCooldown(respawnCooldown*FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[]{ new Image("file:src/main/resources/img/triggers/traps/trap.png")};
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateInverse(activationTime);
            deactivate();
        }
    }
}