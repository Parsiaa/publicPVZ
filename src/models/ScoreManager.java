package models;

import java.util.Map;
import models.Enums.*;

public class ScoreManager {
    
    private int currentMeowPoints;
    private Map<Zombie, Double> killTimers; 
    
    
    public void calculateMeowPoints(ScoreEvent event) {
        // TODO
    }

    public void registerEvent(ScoreEvent event) {
        // TODO
    }

    public int getCurrentMeowPoints() { return currentMeowPoints; }
    public void setCurrentMeowPoints(int currentMeowPoints) { this.currentMeowPoints = currentMeowPoints; }

    public Map<Zombie, Double> getKillTimers() { return killTimers; }
    public void setKillTimers(Map<Zombie, Double> killTimers) { this.killTimers = killTimers; }
    
    
}