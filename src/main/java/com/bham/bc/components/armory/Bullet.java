package com.bham.bc.components.armory;

import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

import java.util.List;

import static com.bham.bc.components.CenterController.backendServices;


abstract public class Bullet extends MovingEntity {
    public int ownerID;

    /**
     * Constructs a bullet using an ID to indicate the character that initiated it
     *
     * @param ownerID character ID this bullet belongs to
     * @param x top left position in x axis
     * @param y top left position in y axis
     * @param speed velocity value at which the bullet will move
     * @param angle angle at which the bullet will move
     */
    public Bullet(int ownerID, double x, double y, double speed, double angle) {
        super(x, y, speed);
        this.ownerID = ownerID;
        this.angle = angle;
    }

    /**
     * Checks if the bullet intersects any of the enemies
     * @param enemies list of all enemies in the game map
     * @return true if the bullet intersects some enemy and false otherwise
     */
    public boolean intersectsEnemies(List<Enemy> enemies) { return enemies.stream().anyMatch(this::intersects); }

    /**
     * A method to indicate if the Bullet has hit any specific Enemy Tank
     * The prerequisites of HIT is :
     * 1. The Bullet is alive
     * 2. The Enemy Tank is alive
     * 3. The bullet 's occupied size intersects with enemy's occupied size(Using getRect() to get size)
     * When it hits, we need to dispatch message (to Enemy Tank)
     * @param enemy
     * @return
     * TODO: class should be instanciated in the controller, handled by physics package
     */
    public boolean hitsEnemy(Enemy enemy) {
        if(intersects(enemy)) {
            BombTank bombEffect = new BombTank(enemy.getPosition().getX(), enemy.getPosition().getY());
            backendServices.addBombTank(bombEffect);
            this.exists = false;
            return true;
        }
        return false;
    }

    /**
     * A method to indicate if the Bullet has hit any specific HomeTank Tank
     * If hit , the life of home tank minus 50.
     * @param t
     * @return
     */
    public boolean hitTank(Player t) {

        if (this.exists && this.intersects(t) && t.exists()) {
            /*
            BombTank e = new BombTank(t.getX(), t.getY());

            backendServices.addBombTank(e);

            if (t.isUser()) {
                t.setHp(t.getHp() - 50);
                if (t.getHp() <= 0)
                    t.setAlive(false);
            } else {
                t.setAlive(false);

            }

            this.exists = false;

            return true;

             */
        }
        return false;
    }

    /**
     * Checks if the bullet hits another bullet
     * @param b
     * @return
     */
    @Deprecated
    public boolean hitBullet(Bullet b) {
        if (this.exists && this.intersects(b)){
            this.exists = false;
            backendServices.removeBullet(b);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "Bullet"; }
}