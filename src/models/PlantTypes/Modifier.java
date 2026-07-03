package models.PlantTypes;

import models.MatchState;
import models.Plant;

public class Modifier extends Plant {
    @Override
    public void act(MatchState state) {
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Plant plant : state.getMap().getAllPlants()) {
            if (!plant.isDead() && Math.abs(plant.getX() - x) <= 1 && Math.abs(plant.getY() - y) <= 1) {
                plant.heal(plant.getMaxHp() / 2);
            }
        }
    }
}
