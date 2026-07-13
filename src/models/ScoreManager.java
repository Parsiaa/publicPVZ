package models;

import java.util.HashMap;
import java.util.Map;
import models.Enums.ScoreEvent;

/**
 * Tracks the MewPoints of a score-run match. Five scoring patterns exist,
 * plus a small base reward for every kill.
 */
public class ScoreManager {

    public static final int POINTS_PER_KILL = 10;

    private int currentMeowPoints;
    private Map<Zombie, Double> killTimers = new HashMap<>();

    public int calculateMeowPoints(ScoreEvent event) {
        switch (event) {
            case FASTKILL: return 25;
            case MUTLI_KILL: return 50;
            case SYNC_KILL: return 75;
            case NO_DAMAGE_WAVE: return 100;
            case PLANT_FOOD_COMBO: return 40;
            default: return 0;
        }
    }

    public void registerEvent(ScoreEvent event) {
        int points = calculateMeowPoints(event);
        currentMeowPoints += points;
        System.out.println("MewPoints +" + points + " (" + event + "). Total: " + currentMeowPoints);
    }

    public void registerKill() {
        currentMeowPoints += POINTS_PER_KILL;
    }

    public int getCurrentMeowPoints() {
        return currentMeowPoints;
    }

    public void setCurrentMeowPoints(int currentMeowPoints) {
        this.currentMeowPoints = currentMeowPoints;
    }

    public Map<Zombie, Double> getKillTimers() {
        return killTimers;
    }

    public void setKillTimers(Map<Zombie, Double> killTimers) {
        this.killTimers = killTimers;
    }
}
