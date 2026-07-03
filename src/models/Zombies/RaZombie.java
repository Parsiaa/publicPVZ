package models.Zombies;

import models.MatchState;
import models.Zombie;

public class RaZombie extends Zombie {
    private double timeSinceLastSteal;
    private int stolenSunAmount;

    public void act(MatchState state) {
        if (currentHealth <= 0) {
            return;
        }
        timeSinceLastSteal += 0.1;
        if (timeSinceLastSteal >= 1.0 && state.getUncollectedSuns() > 0) {
            int stolen = Math.min(25, state.getUncollectedSuns());
            state.setUncollectedSuns(state.getUncollectedSuns() - stolen);
            stolenSunAmount += stolen;
            timeSinceLastSteal = 0;
            System.out.println("Ra Zombie stole " + stolen + " sun!");
        }
    }

    public void die(MatchState state) {
        if (stolenSunAmount > 0) {
            state.addSun(stolenSunAmount);
            System.out.println("Ra Zombie died and returned " + stolenSunAmount + " stolen sun!");
            stolenSunAmount = 0;
        }
    }

    public double getTimeSinceLastSteal() { return timeSinceLastSteal; }
    public int getStolenSunAmount() { return stolenSunAmount; }
}
