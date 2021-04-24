package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.shooting.BulletType;
import com.bham.bc.components.shooting.LaserType;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class StateTrigger extends RespawnTrigger {

    public StateTrigger(int x, int y,int respawnCooldown) {
        super(x,y);
        setCooldown(respawnCooldown*FRAME_RATE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[]{ new Image("file:src/main/resources/img/tiles/triggers/state1.png") };
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).toState1();
            ((Player) entity).gunChange(BulletType.IceBullet);
            ((Player) entity).laserChange(LaserType.ThunderLaser);
            deactivate();
        }
    }
}

