package models.PlantTypes;

import java.util.ArrayList;
import models.MatchState;
import models.Plant;
import models.Enums.PlantTag;

public class Mint extends Plant {
    private boolean hasActivated;

    @Override
    public void act(MatchState state) {
        if (isDead() || hasActivated) {
            return;
        }
        hasActivated = true;
        for (Plant plant : new ArrayList<>(state.getMap().getAllPlants())) {
            if (plant == this || plant.isDead()) {
                continue;
            }
            if (sharesFamily(plant)) {
                plant.triggerPlantFood(state);
            }
        }
        this.health = 0;
        state.getMap().removePlant(this);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }

    private boolean sharesFamily(Plant plant) {
        if (tags == null || plant.getTags() == null) {
            return false;
        }
        for (PlantTag tag : tags) {
            if (plant.hasTag(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasActivated() { return hasActivated; }
}
