package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class UntrappedTrigger extends RespawnTrigger {

    public UntrappedTrigger(int x, int y, int Respwan){
        super(x,y);
        setCooldown(FRAME_RATE*Respwan);
    }

    protected Image[] getDefaultImage() {
        return new Image[]{
                new Image("file:src/main/resources/img/tiles/triggers/clear.png")
        };
    }

    @Override
    public void render(GraphicsContext gc) {
        if(active){
            gc.drawImage(entityImages[0],this.x,this.y);
            renderRegion(gc);
        }
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).setUNTRAPPED();
            deactivate();
        }
    }
}
