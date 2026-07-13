package models.PlantTypes.Plants;

import models.MatchState;
import models.PlantTypes.Explosive;

/** Arms itself after a delay, then explodes on contact with a zombie. */
public class PotatoMine extends Explosive {
    protected int armTimeTicks = 150;
    private int armTicks;

    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        if (armTicks < armTimeTicks) {
            armTicks++;
            if (armTicks == armTimeTicks) {
                System.out.println("Plant " + name + " is armed at (" + (int) x + ", " + (int) y + ").");
            }
            return;
        }
        super.act(state);
    }

    /** Plant food arms it immediately. */
    @Override
    public void triggerPlantFood(MatchState state) {
        armTicks = armTimeTicks;
    }
}
