package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.obstacles.ATTRIBUTE;
import com.bham.bc.components.environment.triggers.ExplosiveTrigger;
import com.bham.bc.components.environment.triggers.HealthGiver;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.components.environment.triggers.WeaponGenerator;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import com.bham.bc.utils.cells.MapDivision;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.maploaders.JsonMapLoader;
import com.bham.bc.utils.maploaders.MapLoader;
import com.sun.tools.javah.Gen;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.shape.Shape;

import static com.bham.bc.utils.Constants.*;
import java.util.ArrayList;
import java.util.List;


public class GameMap {
    private List<GenericObstacle> obstacles;
    private TriggerSystem triggerSystem;
    private SparseGraph graphSystem;

    private static int width = MAP_WIDTH;
    private static int height = MAP_HEIGHT;

    protected MapDivision<BaseGameEntity> mapDivision =
            new MapDivision<>(MAP_WIDTH,MAP_HEIGHT,16,16,50);


    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     */
    public GameMap(MapType mapType) {
        MapLoader mapLoader = new JsonMapLoader(mapType.getName());
        //width = mapLoader.getMapWidth();
        //height = mapLoader.getMapHeight();
        obstacles = mapLoader.getObstacles();
        mapDivision.addToMapDivision(new ArrayList<>(obstacles));
        triggerSystem = mapLoader.getTriggerSystem();
        addTriggers();
    }

    public void addTriggers(){
        HealthGiver hg = new HealthGiver(400,400,10,10);
        HealthGiver hg1 = new HealthGiver(600,400,10,10);
        triggerSystem.register(hg);
        triggerSystem.register(hg1);
    }
    /**

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

    public SparseGraph getGraph() { return graphSystem; }


    public void initialGraph(Player p1){
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        graphSystem = new SparseGraph<NavNode, GraphEdge>(false); //single direction turn off
        hgf.GraphHelper_CreateGrid(graphSystem, MAP_WIDTH,MAP_HEIGHT,GRAPH_NUM_CELLS_Y,GRAPH_NUM_CELLS_X); //make network
        ArrayList<Point2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location
        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
            Point2D vv1 = allNodesLocations.get(index);
            collideWithRectangle(graphSystem.getID(),index,new Rectangle(
                    vv1.getX()-HITBOX_RADIUS,vv1.getY()-HITBOX_RADIUS,HITBOX_RADIUS * 2,HITBOX_RADIUS * 2));
        }
        //removed unreachable nodes
        graphSystem = hgf.FLoodFill(graphSystem,graphSystem.getClosestNodeForEntity(p1));

        //let the corresponding navgraph node point to triggers object
        ArrayList<Trigger> triggers = triggerSystem.getTriggers();
        for (Trigger trigger : triggers) {
            NavNode node = graphSystem.getNode(graphSystem.getClosestNodeForEntity(trigger).Index());
            node.setExtraInfo(trigger);
        }
    }

    /**
     * Clears all obstacles in the map
     */
    public void clearAll() {
        obstacles.clear();
        triggerSystem.clear();
    }


    //renderers-------------------------------------------------------------------
    /**
     * The following methods calls all render methods of particular Objects
     * @param gc
     */

    public void renderBottomLayer(GraphicsContext gc) {
        obstacles.forEach(o -> { if(!o.getAttributes().contains(ATTRIBUTE.RENDER_TOP)) o.render(gc); });
        renderTriggers(gc);
    }

    public void renderTopLayer(GraphicsContext gc) {
        obstacles.forEach(o -> { if(o.getAttributes().contains(ATTRIBUTE.RENDER_TOP)) o.render(gc); });
    }

//    public void renderGraph(GraphicsContext gc, ArrayList<Point2D> points){
//        graphSystem.render(gc);     // render network on map
//        for(Point2D p1 : points)  graphSystem.renderTankPoints(p1,gc);
//    }

    public void renderGraph(GraphicsContext gc, ArrayList<BaseGameEntity> entities){
        graphSystem.render(gc);     // render network on map
        graphSystem.renderTankPoints(entities,gc);
    }
    public void renderTriggers(GraphicsContext gc) { triggerSystem.render(gc); }



    public void update() {
        mapDivision.UpdateObstacles(new ArrayList<>(obstacles));
        obstacles.removeIf(o -> !o.exists());
        obstacles.forEach(GenericObstacle::update);
    }

    public MapDivision<BaseGameEntity> getMapDivision() {
        return mapDivision;
    }

    private void addWeaponGenerator(){
        WeaponGenerator w = new WeaponGenerator(466, 466, Weapon.ArmourGun, 30,30,30);
        triggerSystem.register(w);

    }

    public void addTrigger(Trigger t) {
        triggerSystem.register(t);
    }


    public void handleAll(ArrayList<GameCharacter> characters, ArrayList<Bullet> bullets) {
        obstacles.forEach(obstacle -> {
            characters.forEach(obstacle::handleCharacter);
            bullets.forEach(obstacle::handleBullet);
        });

        triggerSystem.handleAll(characters, obstacles);
    }


    public void collideWithRectangle(int ID,int indexOfNode, Rectangle r1){
        for (int i = 0; i < obstacles.size(); i++) {
            GenericObstacle w = obstacles.get(i);
            w.interactWith(ID,indexOfNode,r1);
        }
    }

    // Temp until physics
    public boolean intersectsObstacles(Shape shape) {
        return obstacles.stream().anyMatch(o -> !o.getAttributes().contains(ATTRIBUTE.PASSABLE) && o.intersectsShape(shape));
    }

    public List<BaseGameEntity> getObstacles() {
        return new ArrayList<>(obstacles);
    }
}
