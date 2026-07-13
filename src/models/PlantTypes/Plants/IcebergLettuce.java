package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.Explosive;

/** Freezes the first zombie that steps onto it, then is consumed. */
public class IcebergLettuce extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - x) <= 0.6) {
                zombie.applyTimedEffect("frozen", Zombie.FROZEN_SECONDS, 0);
                System.out.println(zombie.getTypeName() + " is frozen at ("
                        + String.format("%.1f", zombie.getX()) + ", " + ((int) y + 1) + ")!");
                this.health = 0;
                state.getMap().removePlant(this);
                return;
            }
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (isEnemy(zombie)) {
                zombie.applyTimedEffect("frozen", Zombie.FROZEN_SECONDS, 0);
            }
        }
        this.health = 0;
        state.getMap().removePlant(this);
    }
}
