package models.PlantTypes;

import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;

public class Explosive extends Plant {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0 && Math.abs(zombie.getX() - x) <= 1 && Math.abs(zombie.getY() - y) <= 1) {
                explode(state, 1);
                return;
            }
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        explode(state, 2);
    }

    public void explode(MatchState state, int radius) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0
                    && Math.abs(zombie.getX() - x) <= radius
                    && Math.abs(zombie.getY() - y) <= radius) {
                zombie.takeDamage(baseDamage, DamageType.FIRE);
            }
        }
        this.health = 0;
        state.getMap().removePlant(this);
        System.out.println("Plant " + name + " exploded at (" + (int) x + ", " + (int) y + ")!");
    }
}
