package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.armory.Bullets01;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.CenterController;
import com.bham.bc.components.characters.Tank;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.Random;

import static com.bham.bc.components.CenterController.centerController;

public class Enemy extends Tank {
    public static int count = 0;
    /**
     * the STABLE direction of the Player Tank
     * It's value should not be 'stop'
     * Mainly for renderer
     */
    private Direction Kdirection = Direction.U;
    /**
     *  initialize the health of tank to 200 hp
     *  */
    private int life = 200;

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
    public Enemy(int x, int y, Direction dir) {
        super(1,1, x, y, 35,35, dir);

        initImages();
    }
    /**
     * List of Enemy tank photos of different directions,should be replace Later
     */
    private void initImages() {
        entityImages = new Image[] {
                new Image("file:src/main/resources/img/tankD.gif"),
                new Image("file:src/main/resources/img/tankU.gif"),
                new Image("file:src/main/resources/img/tankL.gif"),
                new Image("file:src/main/resources/img/tankR.gif"),
        };
    }
    /**This method  render all kinds of tanks needed by choosing the particular image in the entityImage list
     * After render the image of tank, this method calls move() and aimAndShoot() to update */
    @Override
    public void render(GraphicsContext gc) {

        if (!live) {
            centerController.removeEnemy(this);
            return;
        }

        switch (Kdirection) {
            case D:
                    gc.drawImage(entityImages[0], x, y);
                break;

            case U:
                    gc.drawImage(entityImages[1], x, y);
                break;
            case L:
                    gc.drawImage(entityImages[2], x, y);
                break;

            case R:
                    gc.drawImage(entityImages[3], x, y);
                break;
        }


    }


    /**This is a method to indicate if Player Tank is in the area of the Rectangle around Enemy Tank,
     if they are in the same rectangle region then return true
     */
    public boolean playertankaround(){
        int rx=x-15,ry=y-15;
        if((x-15)<0) rx=0;
        if((y-15)<0)ry=0;
        Rectangle a=new Rectangle(rx, ry,60,60);
        if (this.live && a.intersects(centerController.getHomeHitBox().getBoundsInLocal())) {
            return true;
        }
        return false;
    }
    /**This method create the firing bullet
     * Use Kdirection to set the direction of Bullet
     * Add bullet to list of bullets
     */
    public Bullets01 fire() {
        if (!live)
            return null;
        int x=0;
        int y=0;
        switch (Kdirection) {
            case D:
                x = this.x + this.width / 2 - Bullets01.width / 2;
                y = this.y + this.length;
                break;

            case U:
                x = this.x + this.width / 2 - Bullets01.width / 2;
                y = this.y - Bullets01.length;
                break;
            case L:
                x = this.x - Bullets01.width;
                y = this.y + this.length / 2 - Bullets01.length / 2;
                break;

            case R:
                x = this.x + this.width;
                y = this.y + this.length / 2 - Bullets01.length / 2;
                break;
        }
        Bullets01 m = new Bullets01(this.ID(),x, y, Kdirection);
        centerController.addBullet(m);
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
                if (this.live && t.isLive()
                        && this.isIntersect(t)) {
                    this.changToOldDir();
                    t.changToOldDir();
                    return true;
                }
            }
        }
        return false;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    @Override
    public void update() {
        move();
        aimAtAndShoot();
    }
    /**
     * Method to implements the movement of enemy tanks
     * Record the current coordinate as old coordinates and move to the specific direction
     * Note: Always check the constraints of boundary:
     * x AND y coordinate of enemy tank can not go outside of the frame
     */
    protected void move() {
        this.oldX = x;
        this.oldY = y;

        switch (direction) {
            case L:
                x -= speedX;
                break;
            case U:
                y -= speedY;
                break;
            case R:
                x += speedX;
                break;
            case D:
                y += speedY;
                break;
            case STOP:
                break;
        }

        if (this.direction != Direction.STOP) {
            this.Kdirection = this.direction;
        }

        //guarantee the tank is in Frame
        if (x < 0) x = 0;
        if (y < 40) y = 40;
        if (x + this.width > Constants.MAP_WIDTH) x = Constants.MAP_WIDTH - this.width;
        if (y + this.length > Constants.MAP_HEIGHT) y = Constants.MAP_HEIGHT - this.length;
    }

    private void aimAtAndShoot(){
        /**
         * Enemy tank switch direction after every 'step' times
         * After the tank changes direction, generate another random steps
         */
        if (step == 0) {
            Direction[] directons = Direction.values();
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
            if (playertankaround()){
                CenterController cC = centerController;
                if(x==cC.getHomeTankX()){
                    if(y>cC.getHomeTankY()){
                        direction=directons[1];
                    } else if (y<cC.getHomeTankY()){
                        direction=directons[3];
                    }
                }else if(y==cC.getHomeTankY()){
                    if(x>cC.getHomeTankX()) {
                        direction=directons[0];
                    } else if (x<cC.getHomeTankX()) {
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

            default:
                System.out.println("no match");
                return false;
        }

    }

    @Override
    public String toString() {
        return "enemy type";
    }

    @Override
    public void increaseHealth(int health) {
        if(this.life+health<=200){
            this.life = this.life+health;
        } else{
            this.life = 200;
        }
    }
}
