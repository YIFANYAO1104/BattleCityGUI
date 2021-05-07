package com.bham.bc.entity.ai;

import com.bham.bc.components.characters.GameCharacter;

/**
 * A class contains many sub classes to define how you select a target
 */
public class TargetingPolices {

    public static interface TargetEvaluator {
        public double evaluate(GameCharacter agent1, GameCharacter agent2);
    }

    /**
     * an evaluation class for distance
     */
    public static class DistanceEvaluator implements TargetEvaluator {
        @Override
        public double evaluate(GameCharacter agent1, GameCharacter agent2){
            return agent1.getCenterPosition().distance(agent2.getCenterPosition());
        }
    }

    /**
     * an evaluation class for distance
     */
    public static class HealthEvaluator implements TargetEvaluator {
        @Override
        public double evaluate(GameCharacter agent1, GameCharacter agent2){
            return agent2.getHp();
        }
    }
}
