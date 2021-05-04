package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.composite.Goal_Think;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.utils.Regulator;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import static com.bham.bc.components.Controller.services;
import static com.bham.bc.components.shooting.BulletType.DEFAULT;
import static com.bham.bc.entity.EntityManager.entityManager;

public class Neuron extends Enemy {

    public static final String IMAGE_PATH = "img/characters/Neuron.png";
    public static final int SIZE = 30;
    public static final double HP = 100;
    public static final double SPEED = 3;

    private Goal_Think brain;
    private Regulator brainRegulator;

    /**
     * Constructs an enemy instance with initial speed value set to 1
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Neuron(double x, double y) {
        super(x, y, SPEED, HP);
        mass=1;
        try{
        entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
        }catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }
        brain = new Goal_Think(this);
        brainRegulator = new Regulator(1);

        GUN.setRate(600);
        GUN.setDamageFactor(3);
    }

    @Override
    protected StateMachine createFSM(){
        return null;
    }

//    @Override
//    public void update() {
//        if(freezeTicks!=0){
//            System.out.println("\n -----------"+ freezeTicks);
//            freezeTicks--;
//            return;
//        }
//        noObstCondition.setTestValues(getCenterPosition(), services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
//
//        Action[] actions = stateMachine.update();
//        Arrays.stream(actions).forEach(action -> {
//            switch(action) {
//                case SEARCH_ALLY:
//                    search(ItemType.ALLY);
//                    goBackCondition.setTestValue(hp <= HP * .2);
//                    break;
//                case ATTACK_ALLY:
//                    face(ItemType.ALLY);
//                    shoot(0.8);
//                    goBackCondition.setTestValue(hp <= HP * .2);
//                    break;
//                case ATTACK_OBST:
//                    setMaxSpeed(shootObstacle() ? SPEED * .3 : SPEED);
//                    break;
//                case RETREAT:
//                    search(ItemType.ENEMY_AREA);
//                    if(Arrays.stream(services.getEnemyAreas()).anyMatch(this::intersects)) {
//                        changeHp(HP);
//                        goBackCondition.setTestValue(false);
//                    }
//                    break;
//                case REGENERATE:
//                    changeHp(HP * .003);
//                    goBackCondition.setTestValue(hp < HP * .8);
//                case SET_RATE:
//                    GUN.setRate(500);
//                    GUN.setDamageFactor(3);
//                    break;
//                case RESET_RATE:
//                    GUN.setRate(1000);
//                    GUN.setDamageFactor(1);
//                    break;
//                case SET_SEARCH:
//                    steering.setDecelerateOn(false);
//                    steering.seekOn();
//                    break;
//                case RESET_SEARCH:
//                    steering.seekOff();
//                    steering.setDecelerateOn(true);
//                    pathEdges.clear();
//                    break;
//            }
//        });
//        move();
//    }

    @Override
    public void update() {
        //process the currently active goal. Note this is required even if the bot
        //is under user control. This is because a goal is created whenever a user
        //clicks on an area of the map that necessitates a path planning request.
        brain.process();

        //Calculate the steering force and update the bot's velocity and position
        move();

        //if the bot is under AI control but not scripted
        targetingSystem.update();

        //appraise and arbitrate between all possible high level goals
        if (brainRegulator.isReady()){
            System.out.println("r"+System.currentTimeMillis());
            brain.decideOnGoals();
        }

        //parallel with any seek, follow path
        //agent will shoot whenever it see a target
        TakeAimAndShoot();
    }

    @Override
    public void destroy() {
        entityManager.removeEntity(this);
        exists = false;

        Trigger dissolve = new Dissolve(getPosition(), entityImages[0], getAngle());
        services.addTrigger(dissolve);
    }

    @Override
    public Circle getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .4);
    }

    @Override
    public String toString() {
        return "Neuron";
    }


    public void TakeAimAndShoot() {
        if (targetingSystem.isAttackTargetOn()) {
            if (targetingSystem.isTargetBotShootable()){
                if (GUN.getBulletType()==DEFAULT) {
                    Point2D futurePos = predictTargetPosition(targetingSystem.getTargetBot());

                    face(futurePos);
                    shoot(0.8);
                    return;
                }
                else {
                    System.out.println("laser is undercons");
                }
            }
        }

        if (targetingSystem.isHitObsOn()){
            if (targetingSystem.getTargetObstacle()!=null){
                face(targetingSystem.getTargetObstacle().getCenterPosition());
                GUN.shoot();
                return;
            }
        }
    }

    /**
     * predicts where the target will be located in the time it takes for a
     * projectile to reach it. This uses a similar logic to the Pursuit steering
     * behavior. Used by TakeAimAndShoot.
     */
    private Point2D predictTargetPosition(GameCharacter target) {
        //if the target is ahead and facing the agent shoot at its current pos
        Point2D ToEnemy = target.getCenterPosition().subtract(this.getCenterPosition());

        double perdictTime = ToEnemy.magnitude() / (GUN.getBulletSpeed() + target.getMaxSpeed());

        //return the predicted future position of the enemy
        return target.getVelocity()
                .multiply(perdictTime)
                .add(target.getCenterPosition());
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);
        targetingSystem.render(gc);
    }
}
