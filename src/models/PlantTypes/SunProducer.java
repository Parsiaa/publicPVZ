package models.PlantTypes;

import models.MatchState;
import models.Plant;

public class SunProducer extends Plant {
    protected int ticksSinceAction;
    protected boolean hasPendingSun;
    protected int pendingSunAmount;
    /** Sun released per production cycle; overridden per plant (Sunflower 50, Twin 100, ...). */
    protected int sunPerCycle = 50;
    protected int plantFoodSun = 150;

    @Override
    public void act(MatchState state) {
        if (isDead() || isFrozen() || hasPendingSun) {
            return;
        }
        ticksSinceAction++;
        int intervalTicks = (int) Math.max(1, actionInterval * 10);
        if (ticksSinceAction >= intervalTicks) {
            ticksSinceAction = 0;
            produce(state, sunPerCycle);
        }
    }

    protected void produce(MatchState state, int amount) {
        hasPendingSun = true;
        pendingSunAmount = amount;
        state.setUncollectedSuns(state.getUncollectedSuns() + amount);
        System.out.println("plant " + name + " produced a sun at (" + (int) x + ", " + (int) y + ")");
    }

    /** Collects the pending sun; returns the collected amount (0 if none was ready). */
    public int collectSun(MatchState state) {
        if (!hasPendingSun) {
            return 0;
        }
        hasPendingSun = false;
        int amount = pendingSunAmount;
        state.setUncollectedSuns(Math.max(0, state.getUncollectedSuns() - amount));
        state.addSun(amount);
        pendingSunAmount = 0;
        return amount;
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        state.addSun(plantFoodSun);
    }

    public boolean hasPendingSun() { return hasPendingSun; }
}
