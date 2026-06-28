package models;

import models.Enums.ObstacleType;

public abstract class Obstacle extends Entity {
    private ObstacleType type;
    private int currentHealth;

    
    public Obstacle(ObstacleType type, int currentHealth) {
        this.type = type;
        this.currentHealth = currentHealth;
    }

    public ObstacleType getType() {
        return type;
    }
    public void setType(ObstacleType type) {
        this.type = type;
    }
    public int getCurrentHealth() {
        return currentHealth;
    }
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    
    public void dropLoot(MatchState state) {
        //TODO
    }

}
