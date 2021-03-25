package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.components.characters.SIDE;
import com.bham.bc.entity.ai.*;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.Arrays;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * <h1>Shooter - far-end operative</h1>
 *
 * <p>This type of enemy has 4 main states determined by free path condition and its HP</p>
 *
 * <ul>
 *     <li><b>Search</b> - searches for the closest ally if its HP is over 20% and it does not have
 *     "run away" property on. It doesn't care about anything else</li>
 *
 *     <li><b>Attack</b> - shoots at any ally if there are no obstacles in between and if its HP
 *     is over 20%. If the bullets are slow and the target is far, it kinda wastes its energy but
 *     ¯\_(ツ)_/¯</li>
 *
 *     <li><b>Retreat</b> - turns on "run away" property and searches for a "health giver" power-up
 *     or tries to retreat to a safe distance. If there is nowhere to retreat, it turns off "run away"
 *     property and remains in <b>Attack</b> state</li>
 *
 *     <li><b>Regenerate</b> - stops doing everything and regenerates its HP to 80%. Once it hits 80%,
 *     it turns of "run away" property</li>
 * </ul>
 */
public class Shooter extends Enemy {

    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/enemy1";
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    public static final int MAX_HP = 100;

    private final StateMachine stateMachine;
    private FreePathCondition noObstaclesCondition;
    private IntCondition badHealthCondition;
    private IntCondition goodHealthCondition;
    private AndCondition attackCondition;
    private BooleanCondition canRetreatCondition;
    private AndCondition retreatCondition;
    private IntCondition safeDistance;

    /**
     * Constructs an enemy instance with initial speed value set to 1
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Shooter(int x, int y) {
        super(x, y, 1, MAX_HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
        this.stateMachine = createFSM();
    }

    protected StateMachine createFSM(){
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.MOVE }, null);
        State attackState = new State(new Action[]{ Action.AIMANDSHOOT }, null);
        State retreatState = new State(new Action[]{ Action.RETREAT }, null);
        State regenerateState = new State(new Action[]{ Action.REGENERATE }, null);

        // Define all conditions required to change any state
        badHealthCondition = new IntCondition(0, 20);
        goodHealthCondition = new IntCondition(80,100);
        noObstaclesCondition = new FreePathCondition();
        canRetreatCondition = new BooleanCondition();
        attackCondition = new AndCondition(new NotCondition(badHealthCondition), noObstaclesCondition);
        retreatCondition = new AndCondition(canRetreatCondition, badHealthCondition);
        safeDistance = new IntCondition(0, 50); // TODO: Figure out if this is a good distance

        // Define all state transitions that could happen
        Transition searchPossibility = new Transition(searchState, goodHealthCondition);
        Transition attackPossibility = new Transition(attackState, attackCondition);
        Transition retreatPossibility = new Transition(retreatState, retreatCondition);
        Transition regeneratePossibility = new Transition(regenerateState, safeDistance);

        // Define how the states can transit from one another
        searchState.setTransitions(new Transition[]{ attackPossibility });
        attackState.setTransitions(new Transition[]{ retreatPossibility});
        retreatState.setTransitions(new Transition[]{ regeneratePossibility});
        regenerateState.setTransitions(new Transition[]{ searchPossibility});

        return new StateMachine(searchState);
    }

    /**
     * Sample method for shooting a default bullet
     *
     * <p>This method creates a new instance of {@link com.bham.bc.components.armory.DefaultBullet}
     * based on player's position and angle</p>
     *
     * TODO: generalize the method once weapon class is defined of more bullet types appear
     *
     * @return instance of DefaultBullet
     */
    public DefaultBullet fire() {
        double centerBulletX = x + WIDTH/2;
        double centerBulletY = y - DefaultBullet.HEIGHT/2;

        Rotate rot = new Rotate(angle, x + WIDTH/2, y + HEIGHT/2);
        Point2D rotatedCenterXY = rot.transform(centerBulletX, centerBulletY);

        double topLeftBulletX = rotatedCenterXY.getX() - DefaultBullet.WIDTH/2;
        double topLeftBulletY = rotatedCenterXY.getY() - DefaultBullet.HEIGHT/2;

        DefaultBullet b = new DefaultBullet(topLeftBulletX, topLeftBulletY, angle, side);
        backendServices.addBullet(b);
        return b;
    }


    /** TODO: replace this method */
    @Deprecated
    public boolean isPlayerClose() {
        /*
        double rx = x - 15 < 0 ? 0 : x - 15;
        double ry = y - 15 < 0 ? 0 : y - 15;

        Rectangle detectRegion = new Rectangle(rx, ry,60,60);
        if (this.exists && detectRegion.intersects(backendServices.getHomeHitBox().getBoundsInLocal())) return true;
        */

        return false;
    }

    /** TODO: replace this method */
    @Deprecated
    private void aimAtAndShoot(){
        /*
         //Enemy tank switch direction after every 'step' times
         //After the tank changes direction, generate another random steps

        if (step == 0) {
            DIRECTION[] directons = DIRECTION.values();
            //[3,14]
            step = r.nextInt(12) + 3;
            //[0,8]
            int mod=r.nextInt(9);


             //Condition: If Enemy Tank finds Player tank around
             //Logic: check if Player tank is in the same horizontal or vertical line of Enemy Tank
             //If Player tank is found in the line, switch enemy tank's direction and chase Player Tank
             //Else randomly choose direction to move forward

            if (playertankaround()){
                BackendServices cC = backendServices;
                if(x==cC.getPlayerX()){
                    if(y>cC.getPlayerY()){
                        direction=directons[1];
                    } else if (y<cC.getPlayerY()){
                        direction=directons[3];
                    }
                }else if(y==cC.getPlayerY()){
                    if(x>cC.getPlayerX()) {
                        direction=directons[0];
                    } else if (x<cC.getPlayerX()) {
                        direction=directons[2];
                    }
                } else{ //change my direction
                    int rn = r.nextInt(directons.length);
                    direction = directons[rn];
                }
                rate=2;
            } else {
                if (1<=mod&&mod<=3) {
                    rate=1;
                } else {
                    int rn = r.nextInt(directons.length);
                    direction = directons[rn];
                    rate=1;
                }
            }
        }
        step--;


        //If Player Tank is near around, having a specific probability to fire (low probability)
        if(rate==2){
            if (r.nextInt(40) > 35) this.fire();
        }else if (r.nextInt(40) > 38) this.fire();
        */
    }

    @Override
    public void update() {
        double distanceToPlayer = getCenterPosition().distance(backendServices.getPlayerCenterPosition());

        safeDistance.setTestValue((int) distanceToPlayer);
        badHealthCondition.setTestValue((int) this.hp);
        goodHealthCondition.setTestValue((int) this.hp);
        noObstaclesCondition.setTestValues(getCenterPosition(), backendServices.getPlayerCenterPosition());
        // TODO: canRetreatCondition.setTestValue(SOME FUNCTION);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case MOVE:
                    //move();
                    break;
                case AIMANDSHOOT:
                    aimAtAndShoot();
                    break;
                case RETREAT:
                    // TODO: retreat();
                    break;
                case REGENERATE:
                    // TODO: regenerate();
                    break;
            }
        });
    }

    /**
     * Obtains the distance from the tank to the player
     * @return
     */
    //TODO
    private int getDistanceToPlayer(){
        return 0;
    }

    @Override
    public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

    @Override
    public Shape getHitBox() {
        Rectangle hitBox = new Rectangle(x, y, WIDTH, HEIGHT);
        hitBox.getTransforms().add(new Rotate(angle, x + WIDTH/2,y + HEIGHT/2));

        return hitBox;
    }

    @Override
    public String toString() { return "Default Enemy"; }
}
