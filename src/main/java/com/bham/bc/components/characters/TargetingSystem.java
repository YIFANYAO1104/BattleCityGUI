/**
 * Desc: class to select a target from the opponents currently in a bot's
 * perceptive memory.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.components.characters;


import com.bham.bc.components.Controller;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.triggers.powerups.HealthGiver;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.bham.bc.components.Controller.services;

public class TargetingSystem {


	private GameCharacter agent;
	private GameCharacter targetBot;

	private List<Rectangle> visionBoxes = new LinkedList<>();
	private List<Rectangle> obstacleBoxes = new LinkedList<>();

	public Obstacle getTargetObstacle() {
		return targetObstacle;
	}

	private Obstacle targetObstacle;


	public TargetingSystem(GameCharacter owner) {
		agent = owner;
		targetBot = null;
		targetObstacle = null;
	}

	private GameCharacter pickCharacter(){
		double minDist = Double.MAX_VALUE;
		GameCharacter targetBot = null;

		//grab a list of all the opponents the owner can sense
		ArrayList<GameCharacter> characters = Controller.services.getCharacters();

		for (GameCharacter character : characters) {
			//make sure the bot is alive and that it is not the owner
			if (character.exists()  && (character.getSide() != agent.getSide())) {
				double dist = character.getCenterPosition().distance(agent.getCenterPosition());

				if (dist < minDist) {
					minDist = dist;
					targetBot = character;
				}
			}
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
		if (hitObsOn) {
			targetObstacle=pickObstacle();
		}
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
	 */
	public GameCharacter getTargetBot() {
		return targetBot;
	}

	/**
	 * sets the target pointer to null
	 */
	public void clearTarget() {
		targetBot = null;
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
				if (((GameCharacter)b).getSide()==agent.side){
					return false;
				}
			}
		}
		return true;
	}

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
				if (((GameCharacter)b).getSide()==agent.side){
					return false;
				}
			}
		}
		return true;
	}

	public boolean isReachingSafeDistance(){
		if (targetBot==null) return false;
		return agent.getCenterPosition().distance(targetBot.getCenterPosition())<=100;
	}

	public boolean isTargetBotVisable() {
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

	public boolean isHitObsOn() {
		return hitObsOn;
	}

	private boolean hitObsOn = false;

	public void hitObsOn(){
		hitObsOn = true;
	}
	public void hitObsOff(){
		hitObsOn = false;
	}

	public void render(GraphicsContext gc) {
		if (targetBot!=null){
			Point2D t1 = targetBot.getCenterPosition();
			gc.setFill(Color.WHITE);
			gc.fillRoundRect(t1.getX(),t1.getY(),4,4,1,1);
		}

		if (targetObstacle!=null){
			Point2D t2 = targetObstacle.getCenterPosition();
			gc.setFill(Color.BLUE);
			gc.fillRoundRect(t2.getX(),t2.getY(),4,4,1,1);
		}

		for (Rectangle box : visionBoxes) {
			GeometryEnhanced.renderHitBox(gc,box);
		}
		visionBoxes.clear();

		for (Rectangle box : obstacleBoxes) {
			GeometryEnhanced.renderHitBox(gc,box);
		}
		obstacleBoxes.clear();
	}

	public void statatat(){
		List<BaseGameEntity> ents = services.getMapDivision()
				.getIntersectedEntities(agent.getCenterPosition(), 300);
		int enemyNum = 0;
		int allyNum = 0;
		int softNum = 0;
		int hardNum = 0;
		int healthTriggerNum = 0;
		for (BaseGameEntity ent : ents) {
			if (ent instanceof Obstacle){
				Obstacle temp = (Obstacle)ent;
				if (temp.getAttributes().contains(Attribute.BREAKABLE)) {
					softNum++;
				} else {
					hardNum++;
				}
			} else if (ent instanceof GameCharacter){
				GameCharacter temp = (GameCharacter)ent;
				if(temp.getSide()==Side.ENEMY){
					enemyNum++;
				} else {
					allyNum++;
				}
			} else if (ent instanceof HealthGiver && ((HealthGiver)ent).active()){
				healthTriggerNum++;
			}
		}
		System.out.println("softNum = "+softNum);
		System.out.println("hardNum = "+hardNum);
		System.out.println("enemyNum = "+enemyNum);
		System.out.println("allyNum = "+allyNum);
		System.out.println("healthTriggerNum = "+healthTriggerNum);
	}
}
