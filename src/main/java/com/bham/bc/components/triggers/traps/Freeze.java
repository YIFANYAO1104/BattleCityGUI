/**
 * Desc: If a player runs over an instance of this class it will freeze for few seconds.
 */
package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class Freeze extends RespawnTrigger {

    public static int width = Constants.TILE_WIDTH;
    public static int length = Constants.TILE_WIDTH;

	private int activationTime;
	
    public Freeze(int x, int y, int activationTime, int respawnCooldown) {

        super(BaseGameEntity.GetNextValidID(), x, y);
        this.activationTime = activationTime;

        //create this trigger's region of fluence
        addRectangularTriggerRegion(new Point2D(x, y), new Point2D(width, length));
        setRespawnDelay(respawnCooldown * FRAME_RATE);
        initImages();
    }

    private void initImages() {
        entityImages = new Image[] {new Image("file:src/main/resources/img/tiles/snowflake.png"), };
    }

    //if triggered, the bot will freeze for few seconds
    @Override
    public void tryTriggerC(GameCharacter gameCharacter) {
        if (isActive() && rectIsTouchingTrigger(gameCharacter.getPosition(), gameCharacter.getRadius())) {
            gameCharacter.toFreeze(activationTime * FRAME_RATE);
            deactivate();
        }
    }
    
    @Override
    public void tryTriggerO(Obstacle entity) {

    }

    //draws a box with a red cross at the trigger's location
    @Override
    public void render(GraphicsContext gc) {
        if (isActive()) {
            gc.drawImage(entityImages[0], this.x, this.y);
        }
    }

    @Override
    public ItemType getItemType() {
        return null;
    }
}