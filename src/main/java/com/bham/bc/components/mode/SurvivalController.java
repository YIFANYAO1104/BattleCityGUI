package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.*;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.environment.triggers.HealthGiver;

/**
 * Represents a controller for the survival game mode
 */
public class SurvivalController extends CenterController {

    /**
     * Constructs the controller by selecting a specific map and creating components
     */
    public SurvivalController(MapType mapType){
        super();
        gameMap = new GameMap(mapType);
        player = new Player(16*36, 16*36);
        gameMap.initGraph(player);
        player.initNavigationService(gameMap.getGraph());
        characters.add(player);
    }

    private void initMap() {

    }

    private void initTriggers() {
        HealthGiver hg = new HealthGiver(400,400,10,10);
        HealthGiver hg1 = new HealthGiver(600,400,10,10);
        //WeaponGenerator w = new WeaponGenerator(466, 466, Weapon.ArmourGun, 30,30,30);
        //Immune I = new Immune(325,325,10,10);
        //TripleBullet T = new TripleBullet(350,350,10,10);
        //Freeze F = new Freeze(375,375,5,10);
        triggerSystem.register(hg);
        triggerSystem.register(hg1);
        //triggerSystem.register(w);
        //triggerSystem.register(I);
        //triggerSystem.register(T);
        //triggerSystem.register(F);

        /*
        ExplosiveTrigger bt = new ExplosiveTrigger(500,500,5);
        triggerSystem.register(bt);
        SpeedTrigger sp = new SpeedTrigger(380,400,20,100);
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
        */
    }

    /**
     * Spawns all the initial characters
     */
    private void initCharacters() {
        //characters.add(new Kamikaze(16*26, 16*26));
        characters.add(new Shooter(16*26, 16*26));
        //characters.add(new Teaser(16*36, 16*28));
        //characters.add(new Tank(16*28, 16*36));
        //characters.add(new Trapper(16*32, 16*32));
    }

    @Override
    public void startGame() {
        initCharacters();
    }

    @Override
    public boolean isGameOver() {
        return false;
    }
}
