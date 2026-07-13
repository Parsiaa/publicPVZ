package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.Explosive;

/** Instantly freezes every zombie on the map, then is consumed. */
public class IceShroom extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        int count = 0;
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (isEnemy(zombie)) {
                zombie.applyTimedEffect("frozen", Zombie.FROZEN_SECONDS, 0);
                count++;
            }
        }
        System.out.println("Ice-shroom froze " + count + " zombie(s)!");
        this.health = 0;
        state.getMap().removePlant(this);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
