package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.obstacles.*;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.components.environment.triggers.WeaponGenerator;
import com.bham.bc.entity.triggers.TriggerSystem;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.utils.maploaders.JsonMapLoader;
import com.bham.bc.utils.maploaders.MapLoader;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameMap {


    /**
     * Creating container for Home Walls On Map
     */
    private List<GenericObstacle> obstacles;
    // private Home home;
    private TriggerSystem triggerSystem;



    //init only--------------------------------------------------------------
    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     */
    public GameMap() {
        MapLoader mapLoader = new JsonMapLoader("/test4.json");
        obstacles = mapLoader.getObstacles();
        triggerSystem = mapLoader.getTriggerSystem();
    }
    //init only--------------------------------------------------------------



    /**
     * Check if home is still alive or not
     * @return
     */
    public boolean isHomeLive(){
        //return home.isLive();
        return true;
    }

    public void setHomeLive(){
        //home.setLive(true);
    }

    /**
     * Remove the home wall from map
     * @param s
     */
    public void removeHomeWall(SoftTile s){
        obstacles.remove(s);
    }



    //clear-----------------------------------------------------------------------
    /**
     * Clear all objects on the map (up to what kind of objects is designed)
     */
    public void clearAll(){
        clearHomeWalls();
        clearTriggers();
    }

    /**
     * Clean all home walls
     */
    public void clearHomeWalls(){
        obstacles.clear();
    }

    public void clearTriggers(){
        triggerSystem.clear();
    }
    //clear-----------------------------------------------------------------------


    //renderers-------------------------------------------------------------------
    /**
     * The following methods calls all render methods of particular Objects
     * @param gc
     */

    public void renderHome(GraphicsContext gc){
        //if (home != null) home.render(gc);
    }

    public void renderBottomLayer(GraphicsContext gc) {
        for(GenericObstacle go: obstacles) {
            if(!go.renderTop()) go.render(gc);
        }
        renderTriggers(gc);
    }

    public void renderTopLayer(GraphicsContext gc) {
        for(GenericObstacle go: obstacles) {
            if(go.renderTop()) go.render(gc);
        }
    }

    public void renderTriggers(GraphicsContext gc){
        triggerSystem.render(gc);
    }

    //renderers------------------------------------------------------


    //hit--------------------------------------------------------------
    /**
     * To Check if the Bullet hits the Wall
     * To check if the Bullet hits Home also
     * @param m
     */
    public void update(Bullet m){
        hitObstacles(m);
        hitHome(m);
    }

    /**
     * This method is to check if Bullet hits common walls
     * @param m
     */
    public void hitObstacles(Bullet m){
        for (int j = 0; j < obstacles.size(); j++) {
            GenericObstacle cw = obstacles.get(j);
            cw.handleBullet(m);
        }
    }
    /**
     * We use the Home Object as parameter for Bullet's hotHome Method
     * To check if home gets hit
     * @param m
     */
    public void hitHome(Bullet m){
        //home.handleBullet(m);
    }

    public void updateObstacles() {
        Iterator<GenericObstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            GenericObstacle curObj = it.next();

                curObj.update();

        }
    }
    //hit--------------------------------------------------------------
    private void addWeaponGenerator(){
        WeaponGenerator w = new WeaponGenerator(466, 466, Weapon.ArmourGun, 30,30,30);
        triggerSystem.register(w);

    }


    //collide--------------------------------------------------------------
    /**
     * A method to loop all objects, and use as parameter for tanks's Collide Method
     * To determine if a Tank Collides such Walls and need to turn back to it's previous position
     * @param t
     */

    public void update(Tank t){
        collideWithHome(t);
        collideWithObstacles(t);
        collideWithTriggers(t);
    }
    /**
     * To determine if a Tank Collides Home and need to turn back
     * @param t
     */
    public void collideWithHome(Tank t){
        //home.handleCharacter(t);
    }
    /**
     * A method to loop all objects, and use as parameter for Collide Method
     * To determine if a Tank Collides such home Walls and need to turn back to it's previous position
     * @param t
     */
    public void collideWithObstacles(Tank t){
        for (int i = 0; i < obstacles.size(); i++) {
            GenericObstacle w = obstacles.get(i);
            w.handleCharacter(t);
        }
    }


    public void collideWithTriggers(Tank t){
        triggerSystem.update(t);
    }
    //collide--------------------------------------------------------------
}
