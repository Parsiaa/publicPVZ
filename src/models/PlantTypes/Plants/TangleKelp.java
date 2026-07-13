package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.Enums.DamageType;
import models.PlantTypes.Explosive;

/** Drags the first adjacent water zombie under and destroys it, ignoring armor. */
public class TangleKelp extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        Zombie target = null;
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - x) <= 1
                    && (target == null || Math.abs(zombie.getX() - x) < Math.abs(target.getX() - x))) {
                target = zombie;
            }
        }
        if (target == null) {
            return;
        }
        target.takeDamage(Integer.MAX_VALUE, DamageType.POISON);
        this.health = 0;
        state.getMap().removePlant(this);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
