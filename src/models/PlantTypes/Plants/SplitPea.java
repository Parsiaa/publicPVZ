package models.PlantTypes.Plants;

import models.MatchState;
import models.Projectile;
import models.Zombie;
import models.PlantTypes.Shooter;

/** Fires one pea forward and two peas backward. */
public class SplitPea extends Shooter {
    @Override
    protected boolean fire(MatchState state) {
        boolean fired = fireInRow(state, (int) y, x, baseDamage);
        Zombie behind = state.getMap().getFirstZombieBehind((int) y, x);
        if (behind != null) {
            for (int i = 0; i < 2; i++) {
                Projectile pea = new Projectile(baseDamage, 0, 1, shotTags());
                pea.setX(x);
                pea.setY(y);
                pea.hitTarget(behind);
            }
            fired = true;
        }
        return fired;
    }
}
