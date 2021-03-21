package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.triggers.BombTrigger;
import com.bham.bc.components.environment.triggers.HealthGiver;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.components.environment.triggers.WeaponGenerator;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import com.bham.bc.components.characters.Character;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.GraphNode;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.maploaders.JsonMapLoader;
import com.bham.bc.utils.maploaders.MapLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

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

    private SparseGraph graphSystem;

//    private ArrayList<GraphNode> a2; // temp value to render the graphlines

    public void addBombTrigger(int x,int y){
        BombTrigger bt = new BombTrigger(x,y,10);
        triggerSystem.register(bt);
    }


    //init only--------------------------------------------------------------
    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     */
    public GameMap(MapType mapType) {
        MapLoader mapLoader = new JsonMapLoader(mapType.getName());
        obstacles = mapLoader.getObstacles();
        triggerSystem = mapLoader.getTriggerSystem();
        initTriggers();
    }

    //init only--------------------------------------------------------------

    public void initTriggers(){
        HealthGiver hg = new HealthGiver(400,400,10,100);
        HealthGiver hg1 = new HealthGiver(600,400,10,100);
        triggerSystem.register(hg);
        triggerSystem.register(hg1);
//        addBombTrigger(500,500);
    }

    public void initialGraph(Point2D location){
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        graphSystem = new SparseGraph<NavNode, GraphEdge>(false); //single direction turn off
        hgf.GraphHelper_CreateGrid(graphSystem, Constants.MAP_WIDTH,Constants.MAP_HEIGHT,64,64); //make network
        ArrayList<Point2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location
        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
            Point2D vv1 = allNodesLocations.get(index);
            collideWithRectangle(graphSystem.ID(),index,new Rectangle(vv1.getX()-16,vv1.getY()-16,32,32));
        }
        //removed unreachable nodes
        graphSystem = hgf.FLoodFill(graphSystem,graphSystem.getClosestNodeForPlayer(location));
//        a2 = hgf.Astar(graphSystem,location,new Point2D(450,550));

        //let the corresponding navgraph node point to triggers object
        ArrayList<Trigger> triggers = triggerSystem.getTriggers();
        for (Trigger trigger : triggers) {
            NavNode node = graphSystem.getNode(graphSystem.getClosestNodeForPlayer(new Point2D(trigger.getX(),trigger.getY())).Index());
            node.setExtraInfo(trigger);
        }
        graphSystem.freeInvalidObtsatcleNodes();
    }

    /**
     * when remove the obstacle, travel all invalid nnodes to check the if hit with obstale and upate them
     */

    public void updateGraph(Point2D location){
//        for(Object n1 : graphSystem.getObstacleNodes()){
//            NavNode node = (NavNode) n1;
//
//        }
        NavNode nnnnn1 = graphSystem.getClosestNodeForPlayer(location);

//        NavNode nnn2 =  graphSystem.getNode(613);
//        System.out.println("index of node is " + nnnnn1.isHit());
    }

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
     * @param obstacle obstacle to remove
     */
    public void removeObstacle(GenericObstacle obstacle){
        obstacles.remove(obstacle);
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

    public void renderGraph(GraphicsContext gc, ArrayList<Point2D> points){
        graphSystem.render(gc);     // render network on map

        for(Point2D p1 : points)  graphSystem.renderTankPoints(p1,gc);

//        HandyGraphFunctions hgf = new HandyGraphFunctions();
//
//        graphSystem.renderlines(gc,hgf.Astar(graphSystem,new Point2D(660,330),new Point2D(450,550)));
        // tricking tanks on the map! with red points
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

    public void update(Character t){
        collideWithHome(t);
        collideWithObstacles(t);
        collideWithTriggers(t);
    }
    /**
     * To determine if a Tank Collides Home and need to turn back
     * @param t
     */
    public void collideWithHome(Character t){
        //home.handleCharacter(t);
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

    public void collideWithTriggers(Character t){
        triggerSystem.update(t);
    }
    //collide--------------------------------------------------------------

    public SparseGraph getGraph(){
        return this.graphSystem;
    }
}
