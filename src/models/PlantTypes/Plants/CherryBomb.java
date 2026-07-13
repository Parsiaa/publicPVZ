package models.PlantTypes.Plants;

import models.MatchState;
import models.PlantTypes.Explosive;

/** Detonates instantly in a 3x3 area as soon as it is planted. */
public class CherryBomb extends Explosive {
    @Override
    public void act(MatchState state) {
        if (!isDead()) {
            explode(state, 1);
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
