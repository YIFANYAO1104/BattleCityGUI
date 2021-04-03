package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.obstacles.Attribute;
import com.bham.bc.components.environment.triggers.HealthGiver;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.components.environment.triggers.WeaponGenerator;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.components.environment.triggers.*;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import com.bham.bc.utils.cells.MapDivision;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.maploaders.JsonMapLoader;
import com.bham.bc.utils.maploaders.MapLoader;
//import com.sun.tools.javah.Gen;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.shape.Shape;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.Constants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Game Map</h1>
 *
 * <p>This class takes care of the the current game session uses. It generates an appropriate map, adds obstacles and triggers
 * to it and sets up graph and map division.<b>Note: </b> the class itself does not handle triggers (power-ups and traps), it
 * asks the {@link TriggerSystem} to do it for it. It also does not check for obstacle intersection, {@link MapDivision} does
 * that for it. This is because it makes easier to handle things and both triggers and obstacles are considered as parts of
 * the map, unlike characters and bullets.</p>
 */
public class GameMap {
    // Parameters
    private static int tileWidth = 0;
    private static int tileHeight = 0;
    private static int numTilesX = 0;
    private static int numTilesY = 0;

    // Elements
    private List<GenericObstacle> interactiveObstacles;
    private List<GenericObstacle> noninteractiveObstacles;

    // Graph, nodes and division
    private SparseGraph graphSystem;
    private MapDivision<BaseGameEntity> mapDivision;

    /**
     * Constructs the map for the active game session. All obstacles are from a JSON file parsed with {@link JsonMapLoader}.
     * @param mapType type of map to be loaded - it contains a path for the JSON file
     */
    public GameMap(MapType mapType) {
        MapLoader mapLoader = new JsonMapLoader(mapType.getName());

        initParams(mapLoader);
        initElements(mapLoader);
        initDivision();
        initGraph1();
    }

    // INITIALIZERS --------------------------------------------------------------------
    /**
     * Initializes the size parameters of the map
     * @param mapLoader map loader that will provide the information about the loaded map
     */
    private void initParams(MapLoader mapLoader) {
        tileWidth = mapLoader.getTileWidth();
        tileHeight = mapLoader.getTileHeight();
        numTilesX = mapLoader.getNumTilesX();
        numTilesY = mapLoader.getNumTilesY();
    }

    /**
     * Initializes (loads) interactive and non-interactive obstacles as well as constant triggers
     * <ul>
     *     <li><b>Noninteractive</b> - obstacles that don't affect any entities in the game</li>
     *     <li><b>Interactive</b> - obstacles that do affect game entities in some way (hit-boxes are considered)</li>
     * </ul>
     * @param mapLoader map loader that will provide all the generated obstacles
     */
    private void initElements(MapLoader mapLoader) {
        interactiveObstacles = mapLoader.getObstacles().stream().filter(o -> !o.getAttributes().contains(Attribute.PASSABLE)).collect(Collectors.toList());
        noninteractiveObstacles = mapLoader.getObstacles().stream().filter(o -> o.getAttributes().contains(Attribute.PASSABLE)).collect(Collectors.toList());
        mapLoader.getTriggers().forEach(trigger -> backendServices.addTrigger(trigger));
    }

    /**
     * Initializes map division - divides map to separate parts for faster collision checks
     */
    private void initDivision() {
        mapDivision = new MapDivision<>(getWidth(), getHeight(), numTilesX, numTilesY, 50);
        System.out.println(interactiveObstacles.size());
        mapDivision.addToMapDivision(new ArrayList<>(interactiveObstacles));
    }

    private void initGraph1() {

    }

    /**
     * Initializes the graph for the map
     * @param p1
     */
    public void initGraph(Player p1){
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        graphSystem = new SparseGraph<NavNode, GraphEdge>(false); //single direction turn off
        hgf.GraphHelper_CreateGrid(graphSystem, getWidth(), getHeight(),GRAPH_NUM_CELLS_Y,GRAPH_NUM_CELLS_X); //make network
        ArrayList<Point2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location
        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
            Point2D vv1 = allNodesLocations.get(index);
            collideWithRectangle(graphSystem.getID(),index,new Rectangle(
                    vv1.getX()-HITBOX_RADIUS,vv1.getY()-HITBOX_RADIUS,HITBOX_RADIUS * 2,HITBOX_RADIUS * 2));
        }
        //removed unreachable nodes
        graphSystem = hgf.FLoodFill(graphSystem,graphSystem.getClosestNodeForEntity(p1));

        //let the corresponding navgraph node point to triggers object
        ArrayList<Trigger> triggers = new ArrayList<>();//triggerSystem.getTriggers();
        for (Trigger trigger : triggers) {
            NavNode node = graphSystem.getNode(graphSystem.getClosestNodeForEntity(trigger).Index());
            node.setExtraInfo(trigger);
        }
    }
    // ---------------------------------------------------------------------------------

    // GETTERS -------------------------------------------------------------------------
    /**
     * Gets map's width (tileWidth * numTilesX)
     * @return current map's width or 0 if no map is loaded
     */
    public static int getWidth() {
        return tileWidth * numTilesX;
    }

    /**
     * Gets map's height (tileHeight * numTilesY)
     * @return current map's height or 0 if no map is loaded
     */
    public static int getHeight() {
        return tileHeight * numTilesY;
    }

    /**
     * Gets the width of the tile
     * @return width of any tile or 0 if no map is loaded
     */
    public static int getTileWidth() {
        return tileWidth;
    }

    /**
     * Gets the height of the tile
     * @return height of any tile or 0 if no map is loaded
     */
    public static int getTileHeight() {
        return tileHeight;
    }

    /**
     * Gets the amount of tiles in X direction
     * @return number of tiles making up the total width of the map or 0 if it is not loaded
     */
    public static int getNumTilesX() {
        return numTilesX;
    }

    /**
     * Gets the amount of tiles in Y direction
     * @return number of tiles making up the total height of the map or 0 if it is not loaded
     */
    public static int getNumTilesY() {
        return numTilesY;
    }

    /**
     * Gets the graph system of this game map
     * @return {@link SparseGraph} generated for this map
     */
    public SparseGraph getGraph() {
        return graphSystem;
    }

    /**
     * Gets the map division system of this game map
     * @return {@link MapDivision} generated for this map
     */
    public MapDivision<BaseGameEntity> getMapDivision() {
        return mapDivision;
    }
    // ---------------------------------------------------------------------------------

    // FRAME ITERATIONS ----------------------------------------------------------------




    /**
     * Clears all obstacles in the map
     */
    public void clearAll() {
        interactiveObstacles.clear();
    }


    //renderers-------------------------------------------------------------------
    /**
     * The following methods calls all render methods of particular Objects
     * @param gc
     */

    public void renderBottomLayer(GraphicsContext gc) {
        noninteractiveObstacles.forEach(o -> { if(!o.getAttributes().contains(Attribute.RENDER_TOP)) o.render(gc); });
        interactiveObstacles.forEach(o -> o.render(gc));
        //triggerSystem.render(gc);
    }

    public void renderTopLayer(GraphicsContext gc) {
        noninteractiveObstacles.forEach(o -> { if(o.getAttributes().contains(Attribute.RENDER_TOP)) o.render(gc); });
    }

    public void renderGraph(GraphicsContext gc, ArrayList<BaseGameEntity> entities){
        graphSystem.render(gc);
        graphSystem.renderTankPoints(entities,gc);
    }



    public void update() {
        mapDivision.UpdateObstacles(new ArrayList<>(interactiveObstacles));
        interactiveObstacles.removeIf(o -> !o.exists());
        interactiveObstacles.forEach(GenericObstacle::update);
    }


    /**
     * Use the map division reduce the at elast two orders of magnitude of computation.
     * @param characters
     * @param bullets
     */
    public void handleAll(ArrayList<GameCharacter> characters, ArrayList<Bullet> bullets) {
        //Update
        characters.forEach(c1->{
            mapDivision.CalculateNeighborsArray(c1,32.0).forEach(o1->{
                try {
                    GenericObstacle oo1 = (GenericObstacle)o1;
                    oo1.handleCharacter(c1);
                }catch (Exception e){}
            });
        });

        bullets.forEach(b1->{
            mapDivision.CalculateNeighborsArray(b1,32.0).forEach(o1->{
                try {
                    GenericObstacle oo1 = (GenericObstacle)o1;
                    oo1.handleBullet(b1);
                }catch (Exception e){}
            });
        });

        //triggerSystem.handleAll(characters, interactiveObstacles);
    }



    public void collideWithRectangle(int ID,int indexOfNode, Rectangle r1){
        for (int i = 0; i < interactiveObstacles.size(); i++) {
            GenericObstacle w = interactiveObstacles.get(i);
            w.interactWith(ID,indexOfNode,r1);
        }
    }

    // Temp until physics
    //Really useful for path smoothing!
    public boolean intersectsObstacles(Shape shape) {
        return interactiveObstacles.stream().anyMatch(o -> !o.getAttributes().contains(Attribute.PASSABLE) && o.intersectsShape(shape));
    }

    public List<BaseGameEntity> getInteractiveObstacles() {
        return new ArrayList<>(interactiveObstacles);
    }
}
