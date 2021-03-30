package com.bham.bc.components.environment.triggers;


import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.entity.triggers.Trigger;
import javafx.geometry.Point2D;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

import static com.bham.bc.components.CenterController.backendServices;

public class ExplosiveTrigger extends Trigger {

    public static int width = 300;
    public static int length = 300;
    //for printing out
    static int seconds = 0;

    protected int delayTime;

    public ExplosiveTrigger(int x, int y, /*double range,*/ int lifeTime) {

        super(BaseGameEntity.GetNextValidID(),x,y);
        this.delayTime = (lifeTime * FRAME_RATE);
        setInactive();

        seconds = delayTime/FRAME_RATE;
        initImages();
        //create and set this trigger's region of fluence
        addRectangularTriggerRegionSurrounded(new Point2D(x, y),
                new Point2D(entityImages[0].getWidth(),entityImages[0].getHeight()),
                new Point2D(width, length));

    }

    private void initImages() {
        entityImages = new Image[] {new Image("file:src/main/resources/img/BombTrigger.png"), };
    }

    /**
     * when triggered this trigger adds the bot that made the source of the
     * sound to the triggering bot's perception.
     */
    @Override
    public void tryTriggerC(GameCharacter gameCharacter) {
        //is this bot within range of this sound
        if (isActive() && this.rectIsTouchingTrigger(gameCharacter.getPosition(), gameCharacter.getRadius())) {
            gameCharacter.changeHP(-1000);
            backendServices.addBombTank(new BombTank(gameCharacter.getPosition()));
        } else if (gameCharacter.getHitBox().intersects(x,y,entityImages[0].getWidth(),entityImages[0].getHeight())){
            gameCharacter.move(-1);
        }
    }

    public void tryTriggerO(GenericObstacle obs) {
        //is this bot within range of this sound
        if (isActive() && rectIsTouchingTrigger(obs.getPosition(), obs.getRadius())) {
            obs.decreaseHP(1000);
            backendServices.addBombTank(new BombTank(obs.getPosition()));
        }
    }

    @Override
    public ItemType getItemType() {
        return null;
    }

    @Override
    public void update() {
        //debug
        if(delayTime/FRAME_RATE != seconds){
            seconds = delayTime/FRAME_RATE;
            System.out.println("BombTimeLeft: " + seconds);
        }

        --delayTime;
        if (delayTime == 0) {
            setActive();
        } else if (delayTime == -1) {
            setInactive();
            setToBeRemovedFromGame();
//            backendServices.addBombTank(new BombTank(x,y));
        }
    }


}