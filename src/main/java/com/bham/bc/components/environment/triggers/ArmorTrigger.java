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

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class ArmorTrigger extends  RespawnTrigger{
    public static int width = Constants.TILE_WIDTH;
    public static int height = Constants.TILE_HEIGHT;

    private int HP;

    public ArmorTrigger(int x, int y, int HP, int Respawn){
        super(GetNextValidID(),x,y);
        this.HP = HP;
        addRectangularTriggerRegion(new Point2D(x,y), new Point2D(width,height));
        setRespawnDelay(Respawn*FRAME_RATE);
        initImages();


    }
    public void initImages(){
        entityImages = new Image[]{ new Image("file:src/main/resources/img/tiles/armor.png")};
    }
    @Override
    public void tryTriggerC(GameCharacter entity) {
        if(isActive()&&rectIsTouchingTrigger(entity.getPosition(), entity.getRadius())){
            //entity.armorUP(HP);

            deactivate();
        }

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
    public void tryTriggerO(GenericObstacle entity) {

    }

    @Override
    public ItemType getItemType() {
        return null;
    }
}
