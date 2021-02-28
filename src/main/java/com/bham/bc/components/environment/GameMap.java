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
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameMap {


    /**
     * Creating container for Home Walls On Map
     */
    private List<MapObject2D> obstacles = new ArrayList<MapObject2D>();
    private Home home;
    private TriggerSystem triggerSystem = new TriggerSystem();



    //init only--------------------------------------------------------------
    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     */
    public GameMap() {
        MapLoader mapLoader = new JsonMapLoader("/test.json");
        obstacles = mapLoader.getObstacles();
        home = mapLoader.getHome();
        triggerSystem = mapLoader.getTriggerSystem();
//        addWeaponGenerator();
        addIceWall();
//        addMetalWall();
//        addTree();
//        addRiver();
//        addCommonWall();
    }
    //init only--------------------------------------------------------------



    /**
     * Check if home is still alive or not
     * @return
     */
    public boolean isHomeLive(){
        return home.isLive();
    }

    public void setHomeLive(){
        home.setLive(true);
    }

    /**
     * Remove the home wall from map
     * @param w
     */
    public void removeHomeWall(CommonWall w){
        obstacles.remove(w);
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
    public void renderAll(GraphicsContext gc){
        renderHome(gc);
        renderObstacles(gc);
        renderTriggers(gc);
    }

    public void renderHome(GraphicsContext gc){
        if (home != null) home.render(gc);
    }

    public void renderObstacles(GraphicsContext gc){
        for (int i = 0; i < obstacles.size(); i++) {
            MapObject2D w = obstacles.get(i);
            w.render(gc);
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
            MapObject2D cw = obstacles.get(j);
            cw.beHitBy(m);
        }
    }
    /**
     * We use the Home Object as parameter for Bullet's hotHome Method
     * To check if home gets hit
     * @param m
     */
    public void hitHome(Bullet m){
        home.beHitBy(m);
    }

    public void updateObstacles() {
        Iterator<MapObject2D> it = obstacles.iterator();
        while (it.hasNext()) {
            MapObject2D curObj = it.next();
            if (curObj.isToBeRemoved()) {
                it.remove();
            } else {
                curObj.update();
            }
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
        home.collideWith(t);
    }
    /**
     * A method to loop all objects, and use as parameter for Collide Method
     * To determine if a Tank Collides such home Walls and need to turn back to it's previous position
     * @param t
     */
    public void collideWithObstacles(Tank t){
        for (int i = 0; i < obstacles.size(); i++) {
            MapObject2D w = obstacles.get(i);
            w.collideWith(t);
        }
    }
    private void addIceWall(){
        for(int i = 0; i < 5 ; i++){
            obstacles.add(new IceWall(390, 320-21*i));

        }
    }
    private void addRiver(){
        obstacles.add(new River(222,166));
        obstacles.add(new River(298,166));

    }
    private void addTree(){

            obstacles.add(new Tree(189,166));
            obstacles.add(new Tree(150,166));

    }
    private void addMetalWall(){
        for (int i = 0; i < 10; i++) {
            if (i < 4)
                obstacles.add(new MetalWall(230, 450-35*i));
            else if (i < 7)
                obstacles.add(new MetalWall(210 + 35 * (i - 4), 400));
            else
                obstacles.add(new MetalWall(88, 30 + (i - 7) * 35));

        }
    }
    private void addCommonWall(){
        obstacles.add(new CommonWall(380,166));
        obstacles.add(new CommonWall(340,166));

    }


    public void collideWithRectangle(int ID,int indexOfNode, Rectangle r1){
        for (int i = 0; i < obstacles.size(); i++) {
            MapObject2D w = obstacles.get(i);
            w.interactWith(ID,indexOfNode,r1);
        }
    }

    public void collideWithTriggers(Tank t){
        triggerSystem.update(t);
    }
    //collide--------------------------------------------------------------
}
