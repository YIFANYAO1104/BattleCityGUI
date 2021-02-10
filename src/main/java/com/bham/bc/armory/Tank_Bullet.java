package main.java.com.bham.bc.armory;

import main.java.com.bham.bc.*;
import main.java.com.bham.bc.environment.CommonWall;
import main.java.com.bham.bc.environment.Home;
import main.java.com.bham.bc.environment.MetalWall;

import java.awt.*;
import java.util.List;

/**
 * Parent Class of Different Bullet
 */
public class Tank_Bullet extends MovingEntity{

    /**
     * Speed of Bullet. May be override by child Class
     */
    public static  int speedX = 12;
    public static  int speedY = 12;

    protected int x, y;
    /**
     * Size of Bullet. May be override by child Class
     */
    public static final int width = 10;
    public static final int length = 10;

    protected Direction diretion;

    protected tankGame tc;
    protected boolean live = true;

    protected boolean good;

    /**
     * Constructor Of Bullet, Will generate valid ID as Entity
     * @param ID
     */
    protected Tank_Bullet(int ID) {
        super();
    }


    public boolean hitTanks(List<Tank> tanks) {
        for (int i = 0; i < tanks.size(); i++) {
            if (hitTank(tanks.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * When bullet intersects with Tank
     * When tank gets hit -> Generate a Bomb Tank
     * Add bombtank to bombTank List
     * Modify tanks 's health after hit, or set tanks's status to dead.
     * @param t
     * @return
     */
    public boolean hitTank(Tank t) {

        if (this.live && this.getRect().intersects(t.getRect()) && t.isLive()
                && this.good != t.isGood()) {

            BombTank e = new BombTank(t.getX(), t.getY(), tc);
            tc.bombTanks.add(e);
            if (t.isGood()) {
                t.setLife(t.getLife() - 50);
                if (t.getLife() <= 0)
                    t.setLive(false);
            } else {
                t.setLive(false);
            }

            this.live = false;

            return true;
        }
        return false;
    }

    /**
     * When Bullet hit Wall(intersects),
     * remove the Wall from Game Map's Wall list since it is destroyed
     * @param w
     * @return
     */
    public boolean hitWall(CommonWall w) {
        if (this.live && this.getRect().intersects(w.getRect())) {
            this.live = false;
            this.tc.gameMap.removeOtherWall(w);
            this.tc.gameMap.removeHomeWall(w);
            return true;
        }
        return false;
    }

    /**
     * When Bullet Hit Bullet, then remove both Bullet, Both Bullets Die
     * @param w
     * @return
     */
    public boolean hitBullet(Tank_Bullet w){
        if (this.live && this.getRect().intersects(w.getRect())){
            this.live=false;
            this.tc.bullets.remove(w);
            return true;
        }
        return false;
    }

    /**
     * When Bullet Hits Metal Wall, bullet dies. Metal Wall will have no change
     * @param w
     * @return
     */
    public boolean hitWall(MetalWall w) {
        if (this.live && this.getRect().intersects(w.getRect())) {
            this.live = false;
            return true;
        }
        return false;
    }

    /**
     * When Bullet hits home, Bullet dies, and Home's status -> Dead
     * @param h
     * @return
     */
    public boolean hitHome(Home h) {
        if (this.live && this.getRect().intersects(h.getRect())) {
            this.live = false;
            h.setLive(false);
            return true;
        }
        return false;
    }
    public Rectangle getRect() {
        return new Rectangle(x, y, width, length);
    }


    public boolean isLive() {
        return live;
    }

    @Override
    public void Update() {

    }

    @Override
    public void Render(Graphics g) {

    }

    @Override
    protected void move() {

    }
}