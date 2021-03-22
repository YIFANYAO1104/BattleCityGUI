package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.triggers.BombTrigger;
import com.bham.bc.components.environment.triggers.HealthGiver;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.components.environment.triggers.WeaponGenerator;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.maploaders.JsonMapLoader;
import com.bham.bc.utils.maploaders.MapLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import com.bham.bc.components.characters.GameCharacter;

import java.util.ArrayList;
import java.util.List;


public class GameMap {
    private List<GenericObstacle> obstacles;
    private TriggerSystem triggerSystem;
    private SparseGraph graphSystem;

    private static int width = Constants.MAP_WIDTH;
    private static int height = Constants.MAP_HEIGHT;

    public void addBombTrigger(){
        BombTrigger bt = new BombTrigger(500,500,5);
        triggerSystem.register(bt);
        HealthGiver hg = new HealthGiver(400,400,10,100);
        HealthGiver hg1 = new HealthGiver(600,400,10,100);
        triggerSystem.register(hg);
        triggerSystem.register(hg1);
    }
    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     */
    public GameMap(MapType mapType) {
        MapLoader mapLoader = new JsonMapLoader(mapType.getName());
        //width = mapLoader.getMapWidth();
        //height = mapLoader.getMapHeight();
        obstacles = mapLoader.getObstacles();
        triggerSystem = mapLoader.getTriggerSystem();
        addBombTrigger();
    }

    /**
     * Gets map's width (tileSize * amountInX)
     * @return current map's width or 0 if no map is loaded
     */
    public static int getWidth() { return width; }

    /**
     * Gets map's height (tileSize * amountInY)
     * @return current map's height or 0 if no map is loaded
     */
    public static int getHeight() {return height; }


    public void initialGraph(Point2D location){
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        graphSystem = new SparseGraph<NavNode, GraphEdge>(false); //single direction turn off
        hgf.GraphHelper_CreateGrid(graphSystem, Constants.MAP_WIDTH,Constants.MAP_HEIGHT,Constants.GRAPH_NUM_CELLS_Y,Constants.GRAPH_NUM_CELLS_X); //make network
        ArrayList<Point2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location
        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
            Point2D vv1 = allNodesLocations.get(index);
            collideWithRectangle(graphSystem.getID(),index,new Rectangle(vv1.getX()-Enemy.WIDTH/2,vv1.getY()-Enemy.HEIGHT/2,Enemy.WIDTH,Enemy.HEIGHT));
        }
        //removed unreachable nodes
        graphSystem = hgf.FLoodFill(graphSystem,graphSystem.getClosestNodeForPlayer(location));

        //let the corresponding navgraph node point to triggers object
        ArrayList<Trigger> triggers = triggerSystem.getTriggers();
        for (Trigger trigger : triggers) {
            NavNode node = graphSystem.getNode(graphSystem.getClosestNodeForPlayer(trigger.getPosition()).Index());
            node.setExtraInfo(trigger);
        }
    }

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
        obstacles.forEach(o -> { if(!o.renderTop()) o.render(gc); });
        renderTriggers(gc);
    }

    public void renderTopLayer(GraphicsContext gc) {
        obstacles.forEach(o -> { if(o.renderTop()) o.render(gc); });
    }

    public void renderGraph(GraphicsContext gc, ArrayList<Point2D> points){
        graphSystem.render(gc);     // render network on map
        for(Point2D p1 : points)  graphSystem.renderTankPoints(p1,gc);
    }

    public void renderTriggers(GraphicsContext gc) { triggerSystem.render(gc); }



    public void update() {
        obstacles.removeIf(o -> !o.exists());
        obstacles.forEach(GenericObstacle::update);
    }

    private void addWeaponGenerator(){
        WeaponGenerator w = new WeaponGenerator(466, 466, Weapon.ArmourGun, 30,30,30);
        triggerSystem.register(w);

    }


    public void handleAll(Player player, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        obstacles.forEach(obstacle -> {
            obstacle.handleCharacter(player);
            enemies.forEach(obstacle::handleCharacter);
            bullets.forEach(obstacle::handleBullet);
        });

        List<GameCharacter> temp = new ArrayList<GameCharacter>();
        temp.add(player);
        temp.addAll(enemies);
        triggerSystem.handleAll(temp, obstacles);
//        enemies.forEach(enemy -> triggerSystem.update(enemy));
    }


    public void collideWithRectangle(int ID,int indexOfNode, Rectangle r1){
        for (int i = 0; i < obstacles.size(); i++) {
            GenericObstacle w = obstacles.get(i);
            w.interactWith(ID,indexOfNode,r1);
        }
    }

    public SparseGraph getGraph(){
        return this.graphSystem;
    }
}
