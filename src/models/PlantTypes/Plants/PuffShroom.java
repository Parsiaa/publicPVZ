package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.Shooter;

/** Short-range shooter with a limited 60-second lifespan. */
public class PuffShroom extends Shooter {
    protected int rangeTiles = 3;
    protected int lifespanTicks = 600;
    private int ageTicks;

    @Override
    public void act(MatchState state) {
        ageTicks++;
        if (ageTicks >= lifespanTicks) {
            this.health = 0;
            state.getMap().removePlant(this);
            System.out.println("Plant " + name + " withered away at (" + (int) x + ", " + (int) y + ").");
            return;
        }
        super.act(state);
    }

    @Override
    protected boolean fire(MatchState state) {
        Zombie target = state.getMap().getFirstZombieAhead((int) y, x);
        if (target == null || target.getX() - x > rangeTiles) {
            return false;
        }
        return fireInRow(state, (int) y, x, baseDamage);
    }

    /** Refreshes the lifespan (used by the plant-food effect). */
    public void resetLifespan() {
        ageTicks = 0;
    }
}
