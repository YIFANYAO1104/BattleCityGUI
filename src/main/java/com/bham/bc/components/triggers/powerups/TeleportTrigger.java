package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;
public class TeleportTrigger extends RespawnTrigger{
    private double destX;
    private double destY;
    RespawnTrigger destination;
    public TeleportTrigger(int x, int y, int Respawn){
        super(x,y);
        setCooldown(FRAME_RATE*Respawn);
    }
    public void setDestination(RespawnTrigger dest){
        this.destination = dest;
        //this.destX = destination.getX();
        //this.destY = destination.getY();

    }

    protected Image[] getDefaultImage() {
        return new  Image[]{ new Image("file:src/main/resources/img/tiles/triggers/teleport.png")};
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            if(destination != null){
                ((Player) entity).teleport(destX,destY);
            }
            deactivate();
        }
    }
}
