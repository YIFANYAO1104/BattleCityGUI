package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.obstacles.Attribute;
import com.bham.bc.components.environment.triggers.HealthGiver;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.components.environment.triggers.WeaponGenerator;
import com.bham.bc.components.environment.triggers.Freeze;
import com.bham.bc.components.environment.triggers.TripleBullet;
import com.bham.bc.components.environment.triggers.Immune;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.triggers.*;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import com.bham.bc.utils.Constants;
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

import static com.bham.bc.utils.Constants.*;
import java.util.ArrayList;
import java.util.List;


public class GameMap {
    private List<GenericObstacle> obstacles;
    private List<GenericObstacle> bg;
    private List<GenericObstacle> top;
    private TriggerSystem triggerSystem;
    private SparseGraph graphSystem;

    private static int width = MAP_WIDTH;
    private static int height = MAP_HEIGHT;

    protected MapDivision<BaseGameEntity> mapDivision =
            new MapDivision<>(MAP_WIDTH,MAP_HEIGHT,16,16,50);


    public void addBombTrigger(){
        ExplosiveTrigger bt = new ExplosiveTrigger(500,500,5);
        triggerSystem.register(bt);
        SpeedTrigger sp = new SpeedTrigger(380,400,20,100);
        HealthGiver hg = new HealthGiver(410,400,10,100);
        HealthGiver hg1 = new HealthGiver(600,400,10,100);
        ArmorTrigger at = new ArmorTrigger(500,570,500,100);
        TrappedTrigger tt = new TrappedTrigger(630,400,100);
        UntrappedTrigger ut = new UntrappedTrigger(560,460,100);
        TeleportTrigger t1 = new TeleportTrigger(420,560,100);
        TeleportTrigger t2 = new TeleportTrigger(520,455,100);
        SpeedTrigger sp2 = new SpeedTrigger(350,670,12,100);
        LandmineTrigger l1 = new LandmineTrigger(470,540,100);
        SpeedTrigger sp1 = new SpeedTrigger(640,630,3,100);
        TrappedTrigger tt2 = new TrappedTrigger(370,650,100);
        LandmineTrigger l2 = new LandmineTrigger(403, 630,100);
        StateTrigger s1 = new StateTrigger(440,500,100);
        TeleportTrigger t3 = new TeleportTrigger(660,660,100);
        TeleportTrigger t4 = new TeleportTrigger(350,370,100);
        t4.setDestination(t3);


        t1.setDestination(t2);
        triggerSystem.register(at);
        triggerSystem.register(sp);
        triggerSystem.register(hg);
        triggerSystem.register(hg1);
        triggerSystem.register(tt);
        triggerSystem.register(ut);
        triggerSystem.register(sp1);
        triggerSystem.register(tt2);
        triggerSystem.register(l2);
        triggerSystem.register(l1);
        triggerSystem.register(t1);
        triggerSystem.register(t2);
        triggerSystem.register(s1);
        triggerSystem.register(sp2);
        triggerSystem.register(t3);
        triggerSystem.register(t4);
    }
    /**
     * Constructor Of Game Map (Adding All Initial Objects to the Map)
     */
    public GameMap(MapType mapType) {
        JsonMapLoader mapLoader = new JsonMapLoader(mapType.getName());
        //width = mapLoader.getMapWidth();
        //height = mapLoader.getMapHeight();
        obstacles = mapLoader.getObstacles();
        bg = mapLoader.getPassables();
        top = mapLoader.getCoverings();
        mapDivision.addToMapDivision(new ArrayList<>(obstacles));
        triggerSystem = mapLoader.getTriggerSystem();
        addTriggers();
    }

    public void addTriggers(){
        HealthGiver hg = new HealthGiver(400,400,10,10);
        HealthGiver hg1 = new HealthGiver(600,400,10,10);
        //Immune I = new Immune(325,325,10,10);
        //TripleBullet T = new TripleBullet(350,350,10,10);
        //Freeze F = new Freeze(375,375,5,10);
        triggerSystem.register(hg);
        triggerSystem.register(hg1);
        //triggerSystem.register(I);
        //triggerSystem.register(T);
        //triggerSystem.register(F);
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
        bg.forEach(o -> o.render(gc));
        obstacles.forEach(o -> o.render(gc));
//        obstacles.forEach(o -> { if(!o.getAttributes().contains(Attribute.RENDER_TOP)) o.render(gc); });
        renderTriggers(gc);
    }

    public void renderTopLayer(GraphicsContext gc) {
        top.forEach(o -> o.render(gc));
//        obstacles.forEach(o -> { if(o.getAttributes().contains(Attribute.RENDER_TOP)) o.render(gc); });
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


//    public void handleAll(ArrayList<GameCharacter> characters, ArrayList<Bullet> bullets) {
//        obstacles.forEach(obstacle -> {
//            characters.forEach(obstacle::handleCharacter);
//            bullets.forEach(obstacle::handleBullet);
//        });
//
//        triggerSystem.handleAll(characters, obstacles);
//    }

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

        triggerSystem.handleAll(characters, obstacles);
    }



    public void collideWithRectangle(int ID,int indexOfNode, Rectangle r1){
        for (int i = 0; i < obstacles.size(); i++) {
            GenericObstacle w = obstacles.get(i);
            w.interactWith(ID,indexOfNode,r1);
        }
    }

    // Temp until physics
    //Really useful for path smoothing!
    public boolean intersectsObstacles(Shape shape) {
        return obstacles.stream().anyMatch(o -> !o.getAttributes().contains(Attribute.PASSABLE) && o.intersectsShape(shape));
    }

    public List<BaseGameEntity> getObstacles() {
        return new ArrayList<>(obstacles);
    }
}
