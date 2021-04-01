package com.bham.bc.components.environment.triggers;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static com.bham.bc.utils.Constants.*;
import static com.bham.bc.utils.Constants.FRAME_RATE;

public class UntrappedTrigger extends RespawnTrigger {

    public static int width = TILE_WIDTH;
    public static int height = TILE_HEIGHT;

    public UntrappedTrigger(int x, int y, int Respwan){
        super(GetNextValidID(),x,y);
        addRectangularTriggerRegion(new Point2D(x,y),new Point2D(width,height));
        setRespawnDelay(FRAME_RATE*Respwan);
        initImages();
    }
    public void initImages(){
        entityImages = new Image[]{
                new Image("file:src/main/resources/img/tiles/clear.png")
        };

    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public void render(GraphicsContext gc) {
        if(isActive()){
            gc.drawImage(entityImages[0],this.x,this.y);
            renderRegion(gc);
        }

    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void tryTriggerC(GameCharacter entity) {
        if(entity instanceof Player){
            if(isActive()&& rectIsTouchingTrigger(entity.getPosition(),entity.getRadius())){
                entity.setUNTRAPPED();
                deactivate();
            }
        }

    }

    @Override
    public void tryTriggerO(GenericObstacle entity) {

    }

    @Override
    public ItemType getItemType() {
        return null;
    }
}
