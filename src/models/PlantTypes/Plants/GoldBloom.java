package models.PlantTypes.Plants;

import models.MatchState;
import models.PlantTypes.SunProducer;

/** Instantly produces a large one-time burst of sun, then vanishes. */
public class GoldBloom extends SunProducer {
    private static final int BURST = 375;
    private boolean spent;

    @Override
    public void act(MatchState state) {
        if (spent || isDead()) {
            return;
        }
        spent = true;
        state.addSun(BURST);
        System.out.println("plant " + name + " produced " + BURST + " sun at (" + (int) x + ", " + (int) y + ")");
        this.health = 0;
        state.getMap().removePlant(this);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
