package main.java.com.bham.bc.environment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


import main.java.com.bham.bc.Tank;
import main.java.com.bham.bc.tankGame;
import main.java.com.bham.bc.armory.Tank_Bullet;

/**
 * Bulilding the Game Map
 */
public class GameMap {

    tankGame tankGame;
    /**
     * Creating container for All kinds of Objects On Map
     */
    private List<CommonWall> homeWall = new ArrayList<CommonWall>();
    private List<CommonWall> otherWall = new ArrayList<CommonWall>();
    private List<MetalWall> metalWall = new ArrayList<MetalWall>();
    private List<River> theRiver = new ArrayList<River>();
    /**
     * Initialize Home to Null
     */
    private Home home = null;
    /**
     * Creating container Of Trees
     */
    List<Tree> trees = new ArrayList<Tree>();

    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     * @param tankGame
     */
    public GameMap(tankGame tankGame) {
        this.tankGame = tankGame;
        addHomeWall();
        addOtherWall();
        addMetalWall();
        addRiver();
        addTree();
        addHome();
    }

    public boolean isHomeLive(){
        return home.isLive();
    }

    /**
     * Set Home to Alive
     */
    public void setHomeLive(){
        home.setLive(true);
    }


    /**
     * Remove Home Wall from The Home Wall List since it is destoryed
     * @param w
     */
    public void removeHomeWall(CommonWall w){
        homeWall.remove(w);
    }
    /**
     * Remove Particular CommonWall from The Wall List since it is destoryed
     * @param w
     */
    public void removeOtherWall(CommonWall w){
        otherWall.remove(w);
    }

    /**
     * Clean All Objects (Except All tanks ,blood Object, and Home )
     */
    public void clearAllExceptHome(){
        metalWall.clear();
        otherWall.clear();
        theRiver.clear();
        trees.clear();
    }


//    public void removeMetalWall(){
//        metalWall.clear();
//    }

    /**
     * Clean all Home walls
     */
    public void clearHomeWalls(){
        homeWall.clear();
    }
    /**
     * Clean all Common walls
     */
    public void clearOtherWalls(){
        otherWall.clear();
    }
    /**
     * Clean all Metal walls
     */
    public void clearMetalWalls(){
        metalWall.clear();
    }
    /**
     * Clean all Trees
     */
    public void clearTrees(){
        trees.clear();
    }

    /**
     * A method to draw All 2D Objects
     * @param g
     */
    public void renderAll(Graphics g){
        renderHomeWall(g);
        renderOtherWall(g);
        renderMetalWall(g);
        renderRiver(g);
    }

    /**
     * The following methods calls all render methods of particular Objects
     * @param g
     */
    public void renderHome(Graphics g){
        home.render(g);
    }

    public void renderHomeWall(Graphics g){
        for (int i = 0; i < homeWall.size(); i++) {
            CommonWall w = homeWall.get(i);
            w.render(g);
        }
    }

    public void renderOtherWall(Graphics g){
        for (int i = 0; i < otherWall.size(); i++) {
            CommonWall cw = otherWall.get(i);
            cw.render(g);
        }
    }

    public void renderMetalWall(Graphics g){
        for (int i = 0; i < metalWall.size(); i++) {
            MetalWall mw = metalWall.get(i);
            mw.render(g);
        }
    }

    public void renderRiver(Graphics g){
        for (int i = 0; i < theRiver.size(); i++) {
            River r = theRiver.get(i);
            r.render(g);
        }
    }

    public void renderTrees(Graphics g){
        for (int i = 0; i < trees.size(); i++) {
            Tree tr = trees.get(i);
            tr.render(g);
        }
    }


    /**
     * We loop the Wall List and use every wall object as parameter for Bullet's hitWall Method
     * To Check if the Bullet hits the Wall
     * @param m
     */
    public void hitWall(Tank_Bullet m){
        for (int j = 0; j < metalWall.size(); j++) {
            MetalWall mw = metalWall.get(j);
            m.hitWall(mw);
        }

        for (int j = 0; j < otherWall.size(); j++) {
            CommonWall w = otherWall.get(j);
            m.hitWall(w);
        }

        for (int j = 0; j < homeWall.size(); j++) {
            CommonWall cw = homeWall.get(j);
            m.hitWall(cw);
        }
    }

    /**
     * We use the Home Object as parameter for Bullet's hotHome Method
     * To check if home gets hit
     * @param m
     */
    public void hitHome(Tank_Bullet m){
        m.hitHome(home);
    }

    /**
     * A method to loop all objects, and use as parameter for tanks's Collide Method
     * To determine if Tank Collides such Walls and need to turn back
     * @param t
     */
    public void collideWithAll(Tank t){
        for (int j = 0; j < homeWall.size(); j++) {
            CommonWall cw = homeWall.get(j);
            t.collideWithWall(cw);
//            cw.draw(g);
        }
        for (int j = 0; j < otherWall.size(); j++) {
            CommonWall cw = otherWall.get(j);
            t.collideWithWall(cw);
//            cw.draw(g);
        }
        for (int j = 0; j < metalWall.size(); j++) {
            MetalWall mw = metalWall.get(j);
            t.collideWithWall(mw);
//            mw.draw(g);
        }
        for (int j = 0; j < theRiver.size(); j++) {
            River r = theRiver.get(j);
            t.collideRiver(r);
//            r.draw(g);
            // t.draw(g);
        }
    }
    /**
     * A method to loop all objects, and use as parameter for tanks's Collide Method
     * To determine if Tank Collides such Walls and need to turn back
     * @param t
     */
    public void collideWithWalls(Tank t){
        for (int i = 0; i < metalWall.size(); i++) {
            MetalWall w = metalWall.get(i);
            t.collideWithWall(w);
        }

        for (int i = 0; i < otherWall.size(); i++) {
            CommonWall cw = otherWall.get(i);
            t.collideWithWall(cw);
//            if (Player2)homeTank2.collideWithWall(cw);
//            cw.draw(g);
        }

        for (int i = 0; i < homeWall.size(); i++) {
            CommonWall w = homeWall.get(i);
            t.collideWithWall(w);
//            if (Player2)homeTank2.collideWithWall(w);
//            w.draw(g);
        }
    }
    /**
     * A method to loop all river, and use as parameter for tanks's Collide Method
     * To determine if Tank Collides such Walls and need to turn back
     * We also Check if there is Player 2, and if there is, check if it collides with River.
     * @param t
     */
    public void collideWithRiver(Tank homeTank,
                                 Tank homeTank2,
                                 Boolean Player2,
                                 Graphics g){
        for (int i = 0; i < theRiver.size(); i++) {
            River r = theRiver.get(i);
            homeTank.collideRiver(r);
            if(Player2) homeTank2.collideRiver(r);
//            r.draw(g);
        }
    }

    public void collideWithHome(Tank t){
        t.collideHome(home);
    }


    /**
     * All the following Adding Methods-> Initialize All objects using random coordinate to the Map
     * Build the Whole complete Game Map
     */
    private void addRiver(){
        theRiver.add(new River(85, 100, tankGame));
    }

    private void addHomeWall(){
        for (int i = 0; i < 10; i++) {
            if (i < 4)
                homeWall.add(new CommonWall(350, 580 - 21 * i, tankGame));
            else if (i < 7)
                homeWall.add(new CommonWall(372 + 22 * (i - 4), 517, tankGame));
            else
                homeWall.add(new CommonWall(416, 538 + (i - 7) * 21, tankGame));

        }
    }

    private void addOtherWall(){
        for (int i = 0; i < 32; i++) {
            if (i < 16) {
                otherWall.add(new CommonWall(200 + 21 * i, 300, tankGame));
                otherWall.add(new CommonWall(500 + 21 * i, 180, tankGame));
                otherWall.add(new CommonWall(200, 400 + 21 * i, tankGame));
                otherWall.add(new CommonWall(500, 400 + 21 * i, tankGame));
            } else if (i < 32) {
                otherWall.add(new CommonWall(200 + 21 * (i - 16), 320, tankGame));
                otherWall.add(new CommonWall(500 + 21 * (i - 16), 220, tankGame));
                otherWall.add(new CommonWall(222, 400 + 21 * (i - 16), tankGame));
                otherWall.add(new CommonWall(522, 400 + 21 * (i - 16), tankGame));
            }
        }
    }

    private void addMetalWall(){
        for (int i = 0; i < 20; i++) {
            if (i < 10) {
                metalWall.add(new MetalWall(140 + 30 * i, 150, tankGame));
                metalWall.add(new MetalWall(600, 400 + 20 * (i), tankGame));
            } else if (i < 20)
                metalWall.add(new MetalWall(140 + 30 * (i - 10), 180, tankGame));

        }
    }

    private void addTree() {
        for (int i = 0; i < 4; i++) {
            if (i < 4) {
                trees.add(new Tree(0 + 30 * i, 360, tankGame));
                trees.add(new Tree(220 + 30 * i, 360, tankGame));
                trees.add(new Tree(440 + 30 * i, 360, tankGame));
                trees.add(new Tree(660 + 30 * i, 360, tankGame));
            }
        }
    }

    private void addHome() {
        home = new Home(373, 557, tankGame);
    }

}
