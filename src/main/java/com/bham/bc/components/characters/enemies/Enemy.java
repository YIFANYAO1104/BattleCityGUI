package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.BackendServices;
import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.entity.DIRECTION;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.characters.Character;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.Random;

import static com.bham.bc.components.CenterController.backendServices;

public class Enemy extends Character {
    public static final String IMAGE_PATH = "file:src/main/resources/img/tankU.gif";
    public static final int MAX_HP = 100;
    private int hp = 100;

    /**
     * Attribute to indicate if Enemy is near around
     * If enemy around, rate =2
     * If enemy not near around, rate =1
     * Initialize to 1
     */
    private int rate=1;
    private static Random r = new Random();
    /**
     * Attribute generate randomly to determine how many steps would tank takes before change direction
     */
    private int step = r.nextInt(10)+5 ;
    /**
     *  a constructor of enemy tank,
     *  Create Enemy tank using coordinate ,direction and centerController as parameters */
    public Enemy(int x, int y) {
        super(x, y, 1);

        initImages();
    }
    /**
     * List of Enemy tank photos of different directions,should be replace Later
     */
    private void initImages() {
        entityImages = new Image[] {
                new Image(IMAGE_PATH),
        };
    }
    /**This method  render all kinds of tanks needed by choosing the particular image in the entityImage list
     * After render the image of tank, this method calls move() and aimAndShoot() to update */
    @Override
    public void render(GraphicsContext gc) {

        if (!exists) {
            backendServices.removeEnemy(this);
            return;
        }

        drawRotatedImage(gc, entityImages[0], angle);
    }

    @Override
    public Shape getHitBox() {
        return null;
    }


    /**This is a method to indicate if Player Tank is in the area of the Rectangle around Enemy Tank,
     if they are in the same rectangle region then return true
     */
    public boolean playertankaround(){
        double rx=x-15, ry=y-15;
        if((x-15)<0) rx=0;
        if((y-15)<0)ry=0;
        Rectangle a=new Rectangle(rx, ry,60,60);
        if (this.exists && a.intersects(backendServices.getHomeHitBox().getBoundsInLocal())) {
            return true;
        }
        return false;
    }
    /**This method create the firing bullet
     * Use Kdirection to set the direction of Bullet
     * Add bullet to list of bullets
     */
    public DefaultBullet fire() {
        if (!exists) return null;

        double bulletX = 0;
        double bulletY = 0;

        DefaultBullet m = new DefaultBullet(this.getID(), bulletX, bulletY, angle);
        backendServices.addBullet(m);
        return m;
    }
    /**
     * Since the tanks on the map was put into a list, we need to check if the this.tank collide
     * with any of those tanks, if intersects, change both of tanks's coordinate to previous value
     * so they can not go further
     */
    public boolean collideWithTanks(java.util.List<Enemy> be) {
        for (int i = 0; i < be.size(); i++) {
            MovingEntity t = be.get(i);
            if (this != t) {
                if (this.exists && t.exists()
                        && this.getHitBox().intersects(t.getHitBox().getBoundsInLocal())) {
                    //this.changToOldDir();
                    //t.changToOldDir();
                    return true;
                }
            }
        }
        return false;
    }

    public int getLife() {
        return hp;
    }

    public void setLife(int life) {
        this.hp = life;
    }

    @Override
    public void update() {
        move();
//        aimAtAndShoot();
    }
    /**
     * Method to implements the movement of enemy tanks
     * Record the current coordinate as old coordinates and move to the specific direction
     * Note: Always check the constraints of boundary:
     * x AND y coordinate of enemy tank can not go outside of the frame
     */
    protected void move() {
        if(!directionSet.isEmpty()) {
            x += Math.sin(Math.toRadians(angle)) * speed;
            y -= Math.cos(Math.toRadians(angle)) * speed;
        }
    }

    private void aimAtAndShoot(){
        /**
         * Enemy tank switch direction after every 'step' times
         * After the tank changes direction, generate another random steps
         */
        if (step == 0) {
            DIRECTION[] directons = DIRECTION.values();
            //[3,14]
            step = r.nextInt(12) + 3;
            //[0,8]
            int mod=r.nextInt(9);

            /**
             * Condition: If Enemy Tank finds Player tank around
             * Logic: check if Player tank is in the same horizontal or vertical line of Enemy Tank
             * If Player tank is found in the line, switch enemy tank's direction and chase Player Tank
             * Else randomly choose direction to move forward
             */
            /*
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
             */
        }
        step--;

        /**
         * If Player Tank is near around, having a specific probability to fire (low probability)
         */
        if(rate==2){
            if (r.nextInt(40) > 35) this.fire();
        }else if (r.nextInt(40) > 38) this.fire();
    }

    /**
     * Handle the message received, and choose corresponding output message
     * @param msg
     * @return
     */
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

    @Override
    public String toString() {
        return "enemy type";
    }

    public void increaseHP(int health) { hp = Math.min(hp + health, MAX_HP); }

}
