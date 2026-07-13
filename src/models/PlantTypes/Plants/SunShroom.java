package models.PlantTypes.Plants;

import models.MatchState;
import models.PlantTypes.SunProducer;

/**
 * Grows through three stages, producing more sun as it matures:
 * 25 for the first 24s, 50 until 72s, then 75.
 */
public class SunShroom extends SunProducer {
    private static final int STAGE2_TICKS = 240;
    private static final int STAGE3_TICKS = 720;
    private int ageTicks;

    public SunShroom() {
        this.sunPerCycle = 25;
        this.plantFoodSun = 225;
    }

    @Override
    public void act(MatchState state) {
        ageTicks++;
        if (ageTicks >= STAGE3_TICKS) {
            sunPerCycle = 75;
        } else if (ageTicks >= STAGE2_TICKS) {
            sunPerCycle = 50;
        }
        super.act(state);
    }
}
