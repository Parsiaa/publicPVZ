package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.Enums.DamageType;
import models.PlantTypes.Explosive;

/** Waits for a zombie to come adjacent, then crushes the first one. */
public class Squash extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        Zombie target = nearestAdjacent(state);
        if (target == null) {
            return;
        }
        target.takeDamage(baseDamage, DamageType.NORMAL);
        finish(state);
    }

    protected Zombie nearestAdjacent(MatchState state) {
        Zombie target = null;
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - x) <= 1
                    && (target == null || Math.abs(zombie.getX() - x) < Math.abs(target.getX() - x))) {
                target = zombie;
            }
        }
        return target;
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
