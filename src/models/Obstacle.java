package models;

import models.Enums.ObstacleType;

public class Obstacle extends Entity {
    private ObstacleType type;
    private int currentHealth;

    public Obstacle(ObstacleType type, int currentHealth) {
        this.type = type;
        this.currentHealth = currentHealth;
    }

    @Override
    public void takeDamage(int amount) {
        this.currentHealth = Math.max(0, currentHealth - amount);
        this.health = currentHealth;
    }

    public boolean isDestroyed() {
        return currentHealth <= 0;
    }

    public void dropLoot(MatchState state) {
        if (type == ObstacleType.GRAVE_SUN) {
            state.addSun(50);
            System.out.println("The grave dropped 50 sun!");
        } else if (type == ObstacleType.GRAVE_FOOD) {
            state.addPlantFood();
            System.out.println("The grave dropped a plant food; you have " + state.getPlantFoods() + " plant foods now.");
        }
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
}
