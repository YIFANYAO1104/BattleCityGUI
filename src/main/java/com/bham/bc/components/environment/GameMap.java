package com.bham.bc.components.environment;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.physics.MapDivision;
import com.bham.bc.entity.graph.HandyGraphFunctions;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bham.bc.components.Controller.services;

/**
 * <h1>Game Map</h1>
 *
 * <p>This class takes care of the map the current game session uses. It generates an appropriate map, adds obstacles and respawning triggers
 * to the game, initializes home and enemy territories and sets up a graph. <b>Note: </b> the class itself only passes triggers that come
 * along from the JSON file to the controller. It also does not check for obstacle intersection, {@link MapDivision} does that for it - this
 * class only takes care of their visuals.</p>
 */
public class GameMap {
    // Parameters
    private static int tileWidth = 0;
    private static int tileHeight = 0;
    private static int numTilesX = 0;
    private static int numTilesY = 0;

    // Obstacles and territories
    private ArrayList<Obstacle> interactiveObstacles;
    private ArrayList<Obstacle> noninteractiveObstacles;
    private Circle homeTerritory;
    private Circle[] enemySpawnAreas;

    // Graph
    private SparseGraph<NavNode, GraphEdge> graphSystem;

    /**
     * Constructs the map for the active game session. All obstacles are from a JSON file parsed with {@link MapLoader}.
     * @param mapType type of map to be loaded - it contains a path for the JSON file
     */
    public GameMap(MapType mapType) {
        MapLoader mapLoader = new MapLoader(mapType.getName());

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
     * @param mapLoader map loader that will provide all the generated obstacles and triggers
     */
    private void initElements(MapLoader mapLoader) {
        interactiveObstacles = mapLoader.getObstacles().stream().filter(o -> !o.getAttributes().contains(Attribute.WALKABLE)).collect(Collectors.toCollection(ArrayList::new));
        noninteractiveObstacles = mapLoader.getObstacles().stream().filter(o -> o.getAttributes().contains(Attribute.WALKABLE)).collect(Collectors.toCollection(ArrayList::new));
        mapLoader.getTriggers().forEach(t -> services.addInteractiveTrigger(t));
    }

    /**
     * Initializes spawning areas and the home territory
     *
     * <p>This method gets the center points of appropriate areas and creates a circle representing the hit-box of those territories.
     * It sets the circle as big as it can get without touching other areas (those that do not belong to the same group) and applies
     * padding to it by reducing its radius by <i>GameCharacter.MAX_RADIUS</i> property. This is to ensure that, if a character is
     * spawned on the edge of that territory, it does not collide with interactive obstacles.</p>
     *
     * @param mapLoader map loader which will provide the territory locations
     */
    private void initAreas(MapLoader mapLoader) {
        // Set up iteration parameters
        int maxChecks = 9;
        double radius = 0;
        double step = Math.min(tileWidth, tileHeight);
        List<Obstacle> allObstacles = mapLoader.getObstacles();

        // Get the center points of each territory
        Point2D homeCenter = new Point2D(getWidth()/2., getHeight()/2.);
        try {
            homeCenter = allObstacles.stream().filter(o -> o.getAttributes().contains(Attribute.HOME_CENTER)).map(Obstacle::getPosition).toArray(Point2D[]::new)[0];  // use first match
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("This map does not have a home spawn center defined! Setting it in the middle of the map...");
        }

        Point2D[] enemySpawnCenters = allObstacles.stream().filter(o -> o.getAttributes().contains(Attribute.ENEMY_SPAWN_CENTER)).map(Obstacle::getPosition).toArray(Point2D[]::new);

        if(enemySpawnCenters.length == 0) {
            System.out.println("This map does not have any enemy spawn centers defined! Setting one in the middle of the map...");
            enemySpawnCenters = new Point2D[] { new Point2D(getWidth()/2., getHeight()/2.) };
        }

        // Initialize the territories
        homeTerritory = new Circle(homeCenter.getX(), homeCenter.getY(), 0);
        enemySpawnAreas = new Circle[enemySpawnCenters.length];

        // Find out home territory size by incrementing its radius and checking if it intersects with non-home-territory obstacles
        for(int i = 0; i < maxChecks; i++) {
            radius += step;
            homeTerritory = new Circle(homeCenter.getX(), homeCenter.getY(), radius);
            if(allObstacles.stream().anyMatch(o -> !o.getAttributes().contains(Attribute.HOME_AREA) && o.intersects(homeTerritory))) break;
        }
        // Apply padding to home territory
        homeTerritory.setRadius(Math.max(1, homeTerritory.getRadius() - GameCharacter.MAX_SIZE*.5));

        // Find enemy spawn areas for each spawn block by incrementing its radius and checking if it intersects with non-enemy-spawn obstacles
        for(int i = 0; i < enemySpawnCenters.length; i++) {
            int finalI = i;
            radius = 0;
            enemySpawnAreas[i] = new Circle(enemySpawnCenters[i].getX(), enemySpawnCenters[i].getY(), 0);

            for(int j = 0; j < maxChecks; j++) {
                radius += step;
                enemySpawnAreas[i].setRadius(radius);
                if((allObstacles.stream().anyMatch(o -> !o.getAttributes().contains(Attribute.ENEMY_SPAWN_AREA) && o.intersects(enemySpawnAreas[finalI])))) break;
            }
            // Apply padding to enemy spawn territory
            enemySpawnAreas[i].setRadius(Math.max(1, enemySpawnAreas[i].getRadius() - GameCharacter.MAX_SIZE*.5));
        }
    }

    /**
     * Initializes the graph for the current map considering triggers locations
     * @param mapLoader map loader that will provide the information about constant triggers the graph can point to
     */
    public void initGraph(MapLoader mapLoader) {
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        graphSystem = new SparseGraph<>(false); //single direction turn off
        hgf.GraphHelper_CreateGrid(graphSystem, getWidth(), getHeight(), getHeight() / (GameCharacter.MAX_SIZE/2), getWidth() / (GameCharacter.MAX_SIZE/2)); //make network
//        ArrayList<Point2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location
//        System.out.println("start");
//        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
//            Point2D vv1 = allNodesLocations.get(index);
//            double maxCharacterRadius = Math.sqrt((GameCharacter.MAX_SIZE/2.0)*(GameCharacter.MAX_SIZE/2.0));
//
//            for (int i = 0; i < interactiveObstacles.size(); i++) {
//                Obstacle w = interactiveObstacles.get(i);
//                w.interacts(graphSystem.getID(), index, new Rectangle(
//                        vv1.getX()-maxCharacterRadius,vv1.getY()-maxCharacterRadius,maxCharacterRadius* 2,maxCharacterRadius * 2));
//            }
//
//        }
//        System.out.println("over");
        //removed unreachable nodes
        // Consider multiple players and enemies spawning at unreachable nodes
        // Player dummyEntity = new Player(mapLoader.getHomeTerritory.getCenterX(), mapLoader.getHomeTerritory.getCenterY());
        // graphSystem = hgf.FLoodFill(graphSystem,graphSystem.getClosestNodeForEntity(dummyEntity));

        for (Trigger trigger : mapLoader.getTriggers()) {
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
    public void renderGraph(GraphicsContext gc, ArrayList<GameCharacter> entities){
        graphSystem.render(gc);
        graphSystem.renderTankPoints(entities,gc);

    }

    /**
     * Renders the circular areas around the specific territories
     * @param gc graphics context the hit-boxes will be drawn on
     */
    public void renderTerritories(GraphicsContext gc) {
        gc.setStroke(Color.PURPLE);
        gc.setLineWidth(2);
        gc.strokeArc(homeTerritory.getCenterX() - homeTerritory.getRadius(), homeTerritory.getCenterY() - homeTerritory.getRadius(), homeTerritory.getRadius()*2, homeTerritory.getRadius()*2, 0, 360, ArcType.OPEN);
        Arrays.stream(enemySpawnAreas).forEach(area -> gc.strokeArc(area.getCenterX() - area.getRadius(), area.getCenterY() - area.getRadius(), area.getRadius()*2, area.getRadius()*2, 0, 360, ArcType.OPEN));
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
