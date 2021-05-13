package com.bham.bc.entity.ai.goals;

import com.bham.bc.components.characters.GameCharacter;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;

/**
 * a calculator for scoring behaviors
 */
public class UtilityCalculator {

    /**
     * the owner
     */
    GameCharacter agent;

    /**
     * the max hp of owner
     */
    private double agentMaxHp;
    /**
     * the max health of owner
     */
    private double agentHealth;

    /**
     * the minimum damage of owner
     */
    private double agentMinDamage;
    /**
     * the maximum damage of owner
     */
    private double agentMaxDamage;

    /**
     * constant for scoring attack
     */
    private final double BASE_ATTACK_SCORE = 0.4f;
    /**
     * constant for scoring health
     */
    private final double HEALTH_SCORE_LOGISTIC_RANGE = 6.f;
    /**
     * constant for scoring health
     */
    private final double HEALTH_SCORE_STEEPNESS = 1.848431643f;  // e * 0.68 where e == Euler's number

    /**
     * Constructs the UtilityCalculator Object and initialize it
     * @param agent the owner of the goal
     */
    public UtilityCalculator(GameCharacter agent) {
        this.agent= agent;
        this.agentMaxHp = agent.getFullHp();
        this.agentMinDamage = agent.getMinDamage();
        this.agentMaxDamage = agent.getMaxDamage();
        this.agentHealth = agent.getHp();
    }

    /**
     * function to decide on goals
     * @return a goal type {@link GoalTypes}
     */
    public int getDecision() {
        GameCharacter opponent = agent.getTargetingSystem().getTargetBot();
        if (opponent==null) {
            return GoalTypes.explore;
        }
        this.agentHealth = agent.getHp();
        double threat = Math.min(opponent.getMaxDamage() / (double) agentHealth, 1.f);

        List<ScoreGoalPairs> scores = new LinkedList<>();
        // attack
        double score = scoreAttack(opponent.getHp());
        scores.add(new ScoreGoalPairs(score, GoalTypes.attack_closest_target));
//        System.out.println("attack = "+score);

        // heal
        score = scoreHealth(threat);
        scores.add(new ScoreGoalPairs(score, GoalTypes.get_health));
//        System.out.println("health = "+score);

        double max = -1;
        int goal = -1;
        for (ScoreGoalPairs scoreGoalPairs : scores) {
            if (scoreGoalPairs.getKey()>max) {
                max = scoreGoalPairs.getKey();
                goal = scoreGoalPairs.getValue();
            }
        }
        if (goal==-1) {
            throw new RuntimeException("faulty goal calculation");
        }
        return goal;
    }

    /**
     * score for behavior attack
     * @param opponentHp the health of opponent
     * @return score
     */
    private double scoreAttack(double opponentHp)
    {
        double inverseRatio = 1 - ((double)(opponentHp - agentMinDamage) / (agentMaxDamage - agentMinDamage));
        double score = (inverseRatio * (1 - BASE_ATTACK_SCORE)) + BASE_ATTACK_SCORE;
        return Math.max(Math.min(score, 1.f), BASE_ATTACK_SCORE);
    }


    /**
     * score for behavior get healt item
     * @param threatScore the threat of opponent
     * @return score
     */
    private double scoreHealth(double threatScore)
    {
        double exponent = -(((double) agentHealth / agentMaxHp) * (HEALTH_SCORE_LOGISTIC_RANGE * 2)) + HEALTH_SCORE_LOGISTIC_RANGE;
        double denominator = 1 + Math.pow(HEALTH_SCORE_STEEPNESS, exponent);
        double score = 1 - (1 / denominator);
        return score * threatScore;
    }

    /**
     * internal class mapping marking scores and goals
     */
    class ScoreGoalPairs extends AbstractMap.SimpleEntry<Double, Integer>{
        ScoreGoalPairs(Double score,Integer goal){
            super(score,goal);
        }
    }
}


