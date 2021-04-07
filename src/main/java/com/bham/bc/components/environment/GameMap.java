package com.bham.bc.components.environment;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.obstacles.Attribute;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.TriggerSystem;
import com.bham.bc.entity.physics.MapDivision;
import com.bham.bc.entity.graph.HandyGraphFunctions;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import com.bham.bc.components.environment.maploaders.JsonMapLoader;
import com.bham.bc.components.environment.maploaders.MapLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.Constants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Game Map</h1>
 *
 * <p>This class takes care of the the current game session uses. It generates an appropriate map, adds obstacles and triggers
 * to it and sets up graph. <b>Note: </b> the class itself only registers constant triggers that come along from the JSON file,
 * it passes them to {@link TriggerSystem}. It also does not check for obstacle intersection, {@link MapDivision} does that for
 * it. This class only takes care of the visuals of obstacles and provides specific map territories.</p>
 */
public class GameMap {
    // Parameters
    private static int tileWidth = 0;
    private static int tileHeight = 0;
    private static int numTilesX = 0;
    private static int numTilesY = 0;

    // Obstacles and territories
    private List<Obstacle> interactiveObstacles;
    private List<Obstacle> noninteractiveObstacles;
    private Circle homeTerritory;
    private Circle[] enemySpawnAreas;

    // Graph
    private SparseGraph<NavNode, GraphEdge> graphSystem;

    /**
     * Constructs the map for the active game session. All obstacles are from a JSON file parsed with {@link JsonMapLoader}.
     * @param mapType type of map to be loaded - it contains a path for the JSON file
     */
    public GameMap(MapType mapType) {
        MapLoader mapLoader = new JsonMapLoader(mapType.getName());

        initParams(mapLoader);
        initElements(mapLoader);
        initGraph(mapLoader);
        initAreas(mapLoader);
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
    }

    /**
     * Initializes spawning areas and the home territory
     *
     * <p>This method gets the center points of appropriate areas and creates a circle representing the hit-box of those territories.
     * It sets the circle as big as it can get without touching other areas (those that do not belong to the same group) and applies
     * padding to it by reducing its radius by <i>GameCharacter.MAX_RADIUS</i> property. This is to ensure that, if a character is
     * spawned on the edge of that territory, it does not collide with interactive obstacles.</p>
     *
     * TODO: Update this once we have a new map
     *
     * @param mapLoader map loader which will provide the territory locations
     */
    private void initAreas(MapLoader mapLoader) {
        // Set up iteration parameters
        int maxChecks = 9;
        double radius = 0;
        double step = Math.min(tileWidth, tileHeight);
        List<Obstacle> allObstacles = mapLoader.getObstacles();


        // TODO: TEMP, REPLACE WITH BELOW
        Point2D homeCenter = new Point2D(16*32, 16*32);
        Point2D[] enemySpawnCenters = new Point2D[] { new Point2D(16*4, 16*4), new Point2D(16*60, 16*4), new Point2D(16*4, 16*60), new Point2D(16*60, 16*60)};

        // Get the center points of each territory
        // Point2D homeCenter = allObstacles.stream().filter(o -> o.getAttributes().contains(Attribute.HOME_CENTER)).map(GenericObstacle::getPosition).toArray(Point2D[]::new)[0];  // use first match
        // Point2D[] enemySpawnCenters = allObstacles.stream().filter(o -> o.getAttributes().contains(Attribute.ENEMY_SPAWN_CENTER)).map(GenericObstacle::getCenterPosition).toArray(Point2D[]::new);

        // Initialize the territories
        homeTerritory = new Circle(homeCenter.getX(), homeCenter.getY(), 0);
        enemySpawnAreas = new Circle[enemySpawnCenters.length];


        // Find out home territory size by incrementing its radius and checking if it intersects with non-home-territory obstacles
        /*
        for(int i = 0; i < maxChecks; i++) {
            radius += step;
            homeTerritory = new Circle(homeCenter.getX(), homeCenter.getY(), radius);
            if(allObstacles.stream().noneMatch(o -> !o.getAttributes().contains(Attribute.HOME_AREA) && o.intersectsShape(homeTerritory))) break;
        }
        // Apply padding to home territory
        homeTerritory.setRadius(Math.max(1, homeTerritory.getRadius() - GameCharacter.MAX_RADIUS));
        */
        homeTerritory = new Circle(homeCenter.getX(), homeCenter.getY(), 16*4); // TODO: replace with above


        // Find enemy spawn areas for each spawn block by incrementing its radius and checking if it intersects with non-enemy-spawn obstacles
        for(int i = 0; i < enemySpawnCenters.length; i++) {
            int finalI = i;
            radius = 0;
            enemySpawnAreas[i] = new Circle(enemySpawnCenters[i].getX(), enemySpawnCenters[i].getY(), 0);

            for(int j = 0; j < maxChecks; j++) {
                radius += step;
                enemySpawnAreas[i].setRadius(radius);
                // if((allObstacles.stream().noneMatch(o -> !o.getAttributes().contains(Attribute.ENEMY_SPAWN_AREA) && o.intersectsShape(enemySpawnAreas[finalI])))) break;
                if(interactiveObstacles.stream().anyMatch(o -> o.intersectsShape(enemySpawnAreas[finalI]))) break; // TODO replace with above
            }
            // Apply padding to enemy spawn territory
            enemySpawnAreas[i].setRadius(Math.max(1, enemySpawnAreas[i].getRadius() - GameCharacter.MAX_RADIUS));

            enemySpawnAreas[i].setRadius(Math.max(1, enemySpawnAreas[i].getRadius() - 16)); //TODO remove
        }
    }

    /**
     * Initializes the graph for the current map considering triggers locations
     * @param mapLoader map loader that will provide the information about constant triggers the graph can point to
     */
    public void initGraph(MapLoader mapLoader) {
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        graphSystem = new SparseGraph<>(false); //single direction turn off
        hgf.GraphHelper_CreateGrid(graphSystem, getWidth(), getHeight(),GRAPH_NUM_CELLS_Y,GRAPH_NUM_CELLS_X); //make network
        ArrayList<Point2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location
        
        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
            Point2D vv1 = allNodesLocations.get(index);

            for (int i = 0; i < interactiveObstacles.size(); i++) {
                Obstacle w = interactiveObstacles.get(i);
                w.interactWith(graphSystem.getID(), index, new Rectangle(
                        vv1.getX()-HITBOX_RADIUS,vv1.getY()-HITBOX_RADIUS,HITBOX_RADIUS * 2,HITBOX_RADIUS * 2));
            }
        }
        //removed unreachable nodes
        // Consider multiple players and enemies spawning at unreachable nodes
        // Player dummyEntity = new Player(mapLoader.getHomeTerritory.getCenterX(), mapLoader.getHomeTerritory.getCenterY());
        // graphSystem = hgf.FLoodFill(graphSystem,graphSystem.getClosestNodeForEntity(dummyEntity));
    }

    public void attachExtraInfo(List<Trigger> triggers) {
        //let the corresponding navgraph node point to triggers object
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
    public SparseGraph<NavNode, GraphEdge> getGraph() {
        return graphSystem;
    }

    /**
     * Gets a copy of interactive obstacles
     * @return {@link Obstacle} list that do not have PASSABLE attribute
     */
    public List<Obstacle> getInteractiveObstacles() {
        return new ArrayList<>(interactiveObstacles);
    }

    /**
     * Gets the sole area of the home territory that can be taken over by enemies
     * @return circle representing home territory
     */
    public Circle getHomeTerritory() {
        return homeTerritory;
    }

    /**
     * Gets all the areas where the enemies can be spawned
     * @return an array of circles representing areas where at any point int it an enemy can be spawned
     */
    public Circle[] getEnemySpawnAreas() {
        return enemySpawnAreas;
    }
    // ---------------------------------------------------------------------------------

    // FRAME ITERATIONS ----------------------------------------------------------------
    /**
     * Updates all the obstacles
     *
     * <p>Interactive obstacles are removed if they don't exist. Both interactive and non-interactive obstacles
     * are updated but here collision is not handled (e.g. obstacle can update its frame picture).</p>
     */
    public void update() {
        interactiveObstacles.removeIf(o -> !o.exists());
        interactiveObstacles.forEach(Obstacle::update);
        noninteractiveObstacles.forEach(Obstacle::update);
    }

    /**
     * Renders all the obstacles as the bottom layer of all the entities
     * @param gc graphics context on which the the obstacles will be rendered
     */
    public void renderBottomLayer(GraphicsContext gc) {
        noninteractiveObstacles.forEach(o -> { if(!o.getAttributes().contains(Attribute.RENDER_TOP)) o.render(gc); });
        interactiveObstacles.forEach(o -> o.render(gc));
    }

    /**
     * Renders those obstacles which are at the top layer of all the entities
     * @param gc graphics context on which the the obstacles will be rendered
     */
    public void renderTopLayer(GraphicsContext gc) {
        noninteractiveObstacles.forEach(o -> { if(o.getAttributes().contains(Attribute.RENDER_TOP)) o.render(gc); });
    }

    /**
     * Renders graph (its nodes) and active points of the entities
     * @param gc       graphics context on which the nodes will be rendered
     * @param entities entities which will allow active nodes (red) to be rendered at their location
     */
    public void renderGraph(GraphicsContext gc, ArrayList<BaseGameEntity> entities){
        graphSystem.render(gc);
        graphSystem.renderTankPoints(entities,gc);
    }

    /**
     * Renders the circular areas around the specific territories
     * @param gc graphics context the hit-boxed will be drawn on
     */
    public void renderTerritories(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeArc(homeTerritory.getCenterX() - homeTerritory.getRadius(), homeTerritory.getCenterY() - homeTerritory.getRadius(), homeTerritory.getRadius()*2, homeTerritory.getRadius()*2, 0, 360, ArcType.ROUND);
        Arrays.stream(enemySpawnAreas).forEach(area -> gc.strokeArc(area.getCenterX() - area.getRadius(), area.getCenterY() - area.getRadius(), area.getRadius()*2, area.getRadius()*2, 0, 360, ArcType.ROUND));
    }
    // ---------------------------------------------------------------------------------

    /**
     * Clears all obstacles in the map
     */
    public void clear() {
        interactiveObstacles.clear();
        noninteractiveObstacles.clear();
    }
}
