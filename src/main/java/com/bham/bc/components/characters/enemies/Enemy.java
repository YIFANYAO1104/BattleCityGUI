package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.components.characters.Character;
import com.bham.bc.components.characters.SIDE;
import com.bham.bc.entity.DIRECTION;
import com.bham.bc.entity.ai.StateMachine;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import static com.bham.bc.components.CenterController.backendServices;

public abstract class Enemy extends Character {

    public static String IMAGE_PATH;
    public static int WIDTH;
    public static int HEIGHT;
    public static int MAX_HP;
    private static StateMachine stateMachine;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x     top left x coordinate of the character
     * @param y     top left y coordinate of the character
     * @param speed value which defines the initial velocity
     * @param hp
     * @param side
     */
    protected Enemy(double x, double y, double speed, double hp, SIDE side) {
        super(x, y, speed, hp, side);
    }

    /**
     * Abstract method which all child classes must fill with their own unique Finite State Machine
     * @return The StateMachine for that specific enemy
     */
    protected abstract StateMachine createFSM();

    /**
     * Abstract method which all chile classes must fill depending on their Finite State Machine
     */
    @Override
    public abstract void update();

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
    public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

    @Override
    public Shape getHitBox() {
        Rectangle hitBox = new Rectangle(x, y, WIDTH, HEIGHT);
        hitBox.getTransforms().add(new Rotate(angle, x + WIDTH/2,y + HEIGHT/2));

        return hitBox;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.Msg.id){
            case 0:
                System.out.println("you are deafeated by id "+ msg.Sender);
                return true;
            case 3:
                //this.direction = (DIRECTION) msg.ExtraInfo;
                System.out.println("chang the direction");
                return true;
            default:
                System.out.println("no match");
                return false;
        }

    }

    /**
     * AI behaviour to idly patrol around the map.
     * They will move a certain distance, then randomly switch direction
     */
    protected void patrol() {
        /*
        TODO: fix this method
        DIRECTION[] directions = DIRECTION.values();
        if (step == 0){
            direction = directions[r.nextInt(directions.length)];
            step = r.nextInt(10)+ 5;
        } else {
            step--;
            move();
        }
         */
    }

    /**
     * In this behaviour the AI tries to get closer to the player.
     * Once it has gotten close to the player it will try and shoot at the player
     * It will use a pathfinding algorithm to navigate to the player
     * This algorithm will not be used after each update as this could cause strain on the game
     * Instead every so often the tank will update it's location of the player, and pathfind to it
     */
    protected void attackPlayer() {
        //TODO
    }

    /**
     * In this behaviour the AI will try to obtain a power-up close to it
     * It will make use of pathfinding algorithm to obtain it.
     * Since the power-up is a stationary item, it will not need continually re-update the pathfinding
     */
    protected void findPowerup(){
        //TODO
    }

    /**
     * In this behaviour the AI will try to maximise it's distance away from the player rather than get close to it
     * It can do this by selecting a point on the map and pathfinding to that point.
     * Very rarely it could update with the players new position to alter it's path if need be
     */
    protected void flee(){
        //TODO
    }

    /**
     * In this behaviour the AI will pathfind to the home base and try to attack it
     */
    protected void attackHomeBase(){
        //TODO
    }

    @Override
    public String toString() { return "Enemy"; }
}
