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
import com.bham.bc.entity.BaseGameEntity;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
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

	public List<Rectangle> getBoxes() {
		return boxes;
	}

	private List<Rectangle> boxes = new LinkedList<>();

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
		System.out.println("TargetBot="+ targetBot);
		return targetBot;
	}

	private Obstacle pickObstacle(){
		double minDist = Double.MAX_VALUE;
		Obstacle targetObstacle = null;
		List<BaseGameEntity> baseGameEntities = services.getMapDivision()
				.calculateNeighborsArray(agent.getCenterPosition(), 25);
		for (BaseGameEntity b : baseGameEntities) {
			if (b instanceof Obstacle && ((Obstacle) b).getAttributes().contains(Attribute.BREAKABLE)){
				double dist = b.getCenterPosition().distance(agent.getCenterPosition());

				if (dist < minDist) {
					minDist = dist;
					targetObstacle = (Obstacle) b;
				}
			}
		}
		System.out.println("TargetObs="+ targetObstacle);
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
		Point2D start = agent.getCenterPosition();
		Point2D end = targetBot.getCenterPosition();
		double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
		Rectangle path = new Rectangle(start.getX(), start.getY(), start.distance(end), agent.getHitBoxRadius() * 2);
		path.getTransforms().add(new Rotate(Math.toDegrees(angle), start.getX(), start.getY()));
		boxes.add(path);
		List<BaseGameEntity> baseGameEntities = services.getMapDivision()
				.calculateNeighborsArray(path);
		for (BaseGameEntity b : baseGameEntities) {
			if (b instanceof Obstacle){
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

	public boolean isTargetBotWithinFOV() {
//		return agent.GetSensoryMem().isOpponentWithinFOV(m_pCurrentTarget);
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
		Point2D t1 = targetBot.getCenterPosition();
		gc.setFill(Color.WHITE);
		gc.fillRoundRect(t1.getX(),t1.getY(),4,4,1,1);

		Point2D t2 = targetObstacle.getCenterPosition();
		gc.setFill(Color.BLUE);
		gc.fillRoundRect(t2.getX(),t2.getY(),4,4,1,1);


	}

	public void renderHitBoxes(AnchorPane hitBoxPane){
		for (Rectangle box : boxes) {
			box.setStroke(Color.RED);
			box.setStrokeWidth(5);
			hitBoxPane.getChildren().add(box);
		}
	}

}
