package models.PlantTypes;

import models.MatchState;
import models.Plant;

public class SunProducer extends Plant {
    protected int ticksSinceAction;
    protected boolean hasPendingSun;

    @Override
    public void act(MatchState state) {
        if (isDead() || isFrozen() || hasPendingSun) {
            return;
        }
        ticksSinceAction++;
        int intervalTicks = (int) Math.max(1, actionInterval * 10);
        if (ticksSinceAction >= intervalTicks) {
            ticksSinceAction = 0;
            hasPendingSun = true;
            state.setUncollectedSuns(state.getUncollectedSuns() + 25);
            System.out.println("plant " + name + " produced a sun at (" + (int) x + ", " + (int) y + ")");
        }
    }

    public boolean collectSun(MatchState state) {
        if (!hasPendingSun) {
            return false;
        }
        hasPendingSun = false;
        state.setUncollectedSuns(Math.max(0, state.getUncollectedSuns() - 25));
        state.addSun(25);
        return true;
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        state.addSun(150);
    }

    public boolean hasPendingSun() { return hasPendingSun; }
}
