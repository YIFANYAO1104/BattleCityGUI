package com.bham.bc.components.characters;

/**
 * A class contains many sub classes for Dijkstra Algorithm
 * Sub classes define conditions that when will Dijkstra Algorithm end
 */
public class TargetingPolices {

    public static interface TargetEvaluator {
        public double evaluate(GameCharacter agent1, GameCharacter agent2);
    }

    /**
     * a condition class to check whether a node contains the desired active trigger
     */
    public static class DistanceEvaluator implements TargetEvaluator {
        @Override
        public double evaluate(GameCharacter agent1, GameCharacter agent2){
            return agent1.getCenterPosition().distance(agent2.getCenterPosition());
        }
    }

    public static class HealthEvaluator implements TargetEvaluator {
        @Override
        public double evaluate(GameCharacter agent1, GameCharacter agent2){
            return agent2.getHp();
        }
    }
}
