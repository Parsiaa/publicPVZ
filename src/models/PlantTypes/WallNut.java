package models.PlantTypes;

import models.MatchState;
import models.Plant;

public class WallNut extends Plant {
    @Override
    public void act(MatchState state) {
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        this.health = maxHp;
    }
}
