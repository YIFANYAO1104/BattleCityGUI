/**
 * Desc: class to select a target from the opponents currently in a bot's
 * perceptive memory.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bd.entity.ai;


import com.bham.bd.components.Controller;
import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.components.environment.Attribute;
import com.bham.bd.components.environment.Obstacle;
import com.bham.bd.entity.BaseGameEntity;
import com.bham.bd.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.bham.bd.components.Controller.services;

/**
 * This class takes care of how agent select it's current target
 */
public class TargetingSystem {

	/**
	 * selection policy, by distance or by health
	 */
	private TargetingPolices.TargetEvaluator targetEvaluator;
	/**
	 * the host of target system
	 */
	private GameCharacter agent;
	/**
	 * the selected agent to be attacked
	 */
	private GameCharacter targetBot;
	/**
	 * the selected obstacle to be attacked
	 */
	private Obstacle targetObstacle;
	/**
	 * The switch for targeting obstacle behavior update
	 */
	private boolean hitObsOn = false;
	/**
	 * The switch for targeting agent behavior update
	 */
	private boolean attackTargetOn = false;

	//debug---------------------------------------------
	private List<Shape> visionBoxes = new LinkedList<>();
	private List<Rectangle> obstacleBoxes = new LinkedList<>();

	public boolean isHitObsOn() {
		return hitObsOn;
	}
	public void hitObsOn(){
		hitObsOn = true;
	}
	public void hitObsOff(){
		hitObsOn = false;
	}

	public boolean isAttackTargetOn() {
		return attackTargetOn;
	}
	public void attackTargetOn(){
		attackTargetOn = true;
	}
	public void attackTargetOff(){
		attackTargetOn = false;
	}

	public Obstacle getTargetObstacle() {
		return targetObstacle;
	}

	public TargetingSystem(GameCharacter owner) {
		agent = owner;
		targetBot = null;
		targetObstacle = null;
		targetEvaluator = null;
	}

	private GameCharacter pickCharacter(){
		double minDist = Double.MAX_VALUE;
		GameCharacter targetBot = null;

		//grab a list of all the opponents the owner can sense
		ArrayList<GameCharacter> characters = Controller.services.getCharacters();

		for (GameCharacter character : characters) {
			//make sure the bot is alive and that it is not the owner
			if (character.exists()  && (character.getSide() != agent.getSide())) {
				double dist = new TargetingPolices.DistanceEvaluator().evaluate(agent,character);

				if (dist < minDist) {
					minDist = dist;
					targetBot = character;
				}
			}
		}
		if (targetBot!=null){
			visionBoxes.add(targetBot.getHitBox());
		}
//		if (targetBot!=null)System.out.println("TargetBot="+ targetBot+targetBot.getID());
		return targetBot;
	}

	private Obstacle pickObstacle(){
		double minDist = Double.MAX_VALUE;
		Obstacle targetObstacle = null;
		List<BaseGameEntity> baseGameEntities = services.getMapDivision()
				.getIntersectedEntities(agent.getCenterPosition(), 25);
		for (BaseGameEntity b : baseGameEntities) {
			if (b instanceof Obstacle && ((Obstacle) b).getAttributes().contains(Attribute.BREAKABLE)){
				double dist = b.getCenterPosition().distance(agent.getCenterPosition());

				if (dist < minDist) {
					minDist = dist;
					targetObstacle = (Obstacle) b;
				}
			}
		}
//		if (targetObstacle!=null)System.out.println("TargetObs="+ targetObstacle+targetObstacle.getID());
		return targetObstacle;
	}

	public void update() {
		targetBot = pickCharacter();
		targetObstacle=pickObstacle();
	}

	/**
	 * To check 2 scenarios:
	 * 1. The target you selected was dead
	 * 2. Only you in the game, no other target we could attack
	 * @return true if there is a live target in the range
	 */
	public boolean isTargetBotPresent() {
		return targetBot != null;
	}

	/**
	 * returns a pointer to the target. null if no target current.
	 * @return a pointer to the target
	 */
	public GameCharacter getTargetBot() {
		return targetBot;
	}

	/**
	 * sets the target pointer to null
	 */
	public void clearTarget() {
		targetBot = null;
		targetObstacle = null;
	}

	/**
	 * returns true if there is unobstructed line of sight between the target
	 * and the owner
	 */
	public boolean isTargetBotShootable() {
		if (targetBot==null) return false;
		Point2D start = agent.getCenterPosition();
		Point2D end = targetBot.getCenterPosition();
		Point2D agentSize = agent.getSize();
		double rayCastWidth = agentSize.getX()-15;

		double angle = GeometryEnhanced.clockWiseAngle(end.subtract(start),new Point2D(0,1));
		double dis = start.distance(end);

		Point2D center = start.midpoint(end);
		Point2D topLeft = center.subtract(rayCastWidth*0.5,dis/2);
		Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), rayCastWidth, dis);
		hitBox.getTransforms().add(new Rotate(angle, center.getX(),center.getY()));


		visionBoxes.add(hitBox);
		List<BaseGameEntity> baseGameEntities = services.getMapDivision()
				.getIntersectedEntities(hitBox);
		for (BaseGameEntity b : baseGameEntities) {
			if (b instanceof Obstacle){
				obstacleBoxes.add(((Obstacle) b).getHitBox());
				return false;
			} else if (b==agent){
				continue;
			} else if (b instanceof GameCharacter){
				if (((GameCharacter)b).getSide()==agent.getSide()){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * To check whether there is an obstacle between agent and it's target
	 * @return true if no obstacle
	 */
	public boolean isTargetBotWalkable() {
		if (targetBot==null) return false;
		Point2D start = agent.getCenterPosition();
		Point2D end = targetBot.getCenterPosition();
		Point2D agentSize = agent.getSize();
		double rayCastWidth = agentSize.getX();

		double angle = GeometryEnhanced.clockWiseAngle(end.subtract(start),new Point2D(0,1));
		double dis = start.distance(end);

		Point2D center = start.midpoint(end);
		Point2D topLeft = center.subtract(rayCastWidth*0.5,dis/2);
		Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), rayCastWidth, dis);
		hitBox.getTransforms().add(new Rotate(angle, center.getX(),center.getY()));


		visionBoxes.add(hitBox);
		List<BaseGameEntity> baseGameEntities = services.getMapDivision()
				.getIntersectedEntities(hitBox);
		for (BaseGameEntity b : baseGameEntities) {
			if (b instanceof Obstacle){
				obstacleBoxes.add(((Obstacle) b).getHitBox());
				return false;
			} else if (b==agent){
				continue;
			} else if (b instanceof GameCharacter){
				if (((GameCharacter)b).getSide()==agent.getSide()){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * To check whether the agent is close enough to the target
	 * @return true if the distance is smaller than 100
	 */
	public boolean isReachingSafeDistance(){
		if (targetBot==null) return false;
		return agent.getCenterPosition().distance(targetBot.getCenterPosition())<=100;
	}

	/**
	 * To check whether the agent is close enough to the target and is close enough to agent
	 * @return true if the target is close enough and there is a path for shooting out a bullet
	 */
	public boolean isTargetBotVisiable() {
		if (targetBot==null) return false;
		return isTargetBotShootable()&&
				agent.getCenterPosition().distance(targetBot.getCenterPosition())<=100;
	}

	public void targetObstacle() {
		targetBot = null;
	}

	public boolean isTargetObstaclePresent() {
		return targetObstacle != null;
	}

	public void render(GraphicsContext gc) {
		if (targetBot!=null){
			Point2D t1 = targetBot.getCenterPosition();
			gc.setFill(Color.WHITE);
			gc.fillRoundRect(t1.getX(),t1.getY(),4,4,1,1);
		}

		if (targetObstacle!=null){
			GeometryEnhanced.renderHitBox(gc,targetObstacle.getHitBox());
			Point2D t2 = targetObstacle.getCenterPosition();
			gc.setFill(Color.BLUE);
			gc.fillRoundRect(t2.getX(),t2.getY(),4,4,1,1);
		}

		for (Shape box : visionBoxes) {
			GeometryEnhanced.renderHitBox(gc,box);
		}
		visionBoxes.clear();

		for (Rectangle box : obstacleBoxes) {
			GeometryEnhanced.renderHitBox(gc,box);
		}
		obstacleBoxes.clear();
	}
}
