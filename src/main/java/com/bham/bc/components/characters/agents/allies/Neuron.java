package com.bham.bc.components.characters.agents.allies;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.characters.agents.Agent;
import com.bham.bc.entity.ai.goals.Goal_Think;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.utils.Regulator;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import static com.bham.bc.components.Controller.services;
import static com.bham.bc.components.shooting.BulletType.DEFAULT;
import static com.bham.bc.entity.EntityManager.entityManager;

public class Neuron extends Agent {
    /** Path to the image of this ally */
    public static final String IMAGE_PATH = "img/characters/Neuron.png";

    /** The width and the height the ally's image should have when rendered */
    public static final int SIZE = 30;

    /** HP the ally should start with */
    public static final double HP = 100;

    /** Speed the ally should start with */
    public static final double SPEED = 3;

    private final Goal_Think BRAIN;
    private final Regulator BRAIN_REGULATOR;

    /**
     * Constructs an enemy instance with initial speed value set to 1
     *
     * @param x top left x coordinate of the ally
     * @param y top left y coordinate of the ally
     */
    public Neuron(double x, double y) {
        super(x, y, SPEED, HP, Side.ALLY);
        mass=1;

        try{
            entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
        } catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }

        BRAIN = new Goal_Think(this);
        BRAIN_REGULATOR = new Regulator(5);

        GUN.setRate(600);
        GUN.setDamageFactor(5);
    }

    @Override
    protected StateMachine createFSM(){
        return null;
    }

    @Override
    public void update() {
        BRAIN.process();
        move();
        targetingSystem.update();

        if (BRAIN_REGULATOR.isReady()){
            BRAIN.decideOnGoals();
        }

        //parallel with any seek, follow path
        //agent will shoot whenever it see a target
        takeAimAndShoot();
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


    public void takeAimAndShoot() {
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

//    @Override
//    public void render(GraphicsContext gc) {
//        super.render(gc);
//        if (navigationService!=null) navigationService.render(gc);
//        targetingSystem.render(gc);
//    }
}
