package models.PlantTypes.Plants;

import models.MatchState;
import models.Projectile;
import models.Zombie;
import models.PlantTypes.Shooter;

/** Fires in the four diagonal directions (the lanes above and below, front and back). */
public class Rotobaga extends Shooter {
    @Override
    protected boolean fire(MatchState state) {
        boolean fired = false;
        for (int row = (int) y - 1; row <= (int) y + 1; row += 2) {
            fired |= hit(state.getMap().getFirstZombieAhead(row, x));
            fired |= hit(state.getMap().getFirstZombieBehind(row, x));
        }
        return fired;
    }

    private boolean hit(Zombie target) {
        if (target == null) {
            return false;
        }
        Projectile projectile = new Projectile(baseDamage, 0, 1, shotTags());
        projectile.setX(x);
        projectile.setY(target.getY());
        projectile.hitTarget(target);
        return true;
    }
}
