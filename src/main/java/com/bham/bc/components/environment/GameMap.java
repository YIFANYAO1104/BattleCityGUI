package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.components.environment.triggers.WeaponGenerator;
import com.bham.bc.entity.triggers.TriggerSystem;
import com.bham.bc.components.characters.Character;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.graph.node.Vector2D;
import com.bham.bc.utils.maploaders.JsonMapLoader;
import com.bham.bc.utils.maploaders.MapLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameMap {

    private List<GenericObstacle> obstacles;
    private TriggerSystem triggerSystem;
    private SparseGraph graphSystem;

    private int width;
    private int height;


    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     */
    public GameMap(String resourceName) {
        MapLoader mapLoader = new JsonMapLoader(resourceName);
        width = mapLoader.getMapWidth();
        height = mapLoader.getMapHeight();
        obstacles = mapLoader.getObstacles();
        triggerSystem = mapLoader.getTriggerSystem();
    }


    public void initGraph(Point2D location) {
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        graphSystem = new SparseGraph<>(false); //single direction turn off
        hgf.GraphHelper_CreateGrid(graphSystem, Constants.MAP_WIDTH, Constants.MAP_HEIGHT,64,64); //make network
        ArrayList<Vector2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location

        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
            Vector2D vv1 = allNodesLocations.get(index);
            collideWithRectangle(graphSystem.getID(),index,new Rectangle(vv1.getX()-16,vv1.getY()-16,32.0,32.0));
        }

        //removed unreachable nodes
        graphSystem = hgf.FLoodFill(graphSystem,graphSystem.TrickingTank(new Vector2D(location)));
    }


    /**
     * Removes an obstacle from the game map
     * @param obstacle obstacle to remove
     */
    public void removeObstacle(GenericObstacle obstacle){ obstacles.remove(obstacle); }



    /**
     * Clears all obstacles in the map
     */
    public void clearAll() { clearHomeWalls(); clearTriggers(); }

    /**
     * Clean all home walls
     */
    public void clearHomeWalls() { obstacles.clear(); }

    public void clearTriggers() { triggerSystem.clear(); }


    //renderers-------------------------------------------------------------------
    /**
     * The following methods calls all render methods of particular Objects
     * @param gc
     */

    public void renderBottomLayer(GraphicsContext gc) {
        obstacles.stream().forEach(o -> { if(!o.renderTop()) o.render(gc); });
    }

    public void renderTopLayer(GraphicsContext gc) {
        obstacles.stream().forEach(o -> { if(o.renderTop()) o.render(gc); });
    }

    public void renderGraph(GraphicsContext gc, ArrayList<Point2D> points){
        graphSystem.render(gc);     // render network on map
        for(Point2D p1 : points)  graphSystem.TrickingTank(new Vector2D(p1), gc);
    }

    public void renderTriggers(GraphicsContext gc) {
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


    public void updateObstacles() { obstacles.stream().forEach(GenericObstacle::update); }

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

    public void update(Character t){
        collideWithObstacles(t);
        collideWithTriggers(t);
    }

    /**
     * A method to loop all objects, and use as parameter for Collide Method
     * To determine if a Tank Collides such home Walls and need to turn back to it's previous position
     * @param t
     */
    public void collideWithObstacles(Character t){
        for (int i = 0; i < obstacles.size(); i++) {
            GenericObstacle w = obstacles.get(i);
            w.handleCharacter(t);
        }
    }

    public void collideWithRectangle(int ID,int indexOfNode, Rectangle r1){
        for (int i = 0; i < obstacles.size(); i++) {
            GenericObstacle w = obstacles.get(i);
            w.interactWith(ID,indexOfNode,r1);
        }
    }

    public void collideWithTriggers(Character character){ triggerSystem.update(character); }
}
