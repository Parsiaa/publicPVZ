package models.PlantTypes.Plants;

import models.MatchState;
import models.Projectile;
import models.Zombie;
import models.PlantTypes.Shooter;

/** Fires in five star directions: forward and back in its lane, plus up and down. */
public class Starfruit extends Shooter {
    @Override
    protected boolean fire(MatchState state) {
        boolean fired = fireInRow(state, (int) y, x, baseDamage);
        fired |= hit(state, state.getMap().getFirstZombieBehind((int) y, x));
        fired |= hit(state, nearestInRow(state, (int) y - 1));
        fired |= hit(state, nearestInRow(state, (int) y + 1));
        fired |= hit(state, nearestInRow(state, (int) y - 2));
        return fired;
    }

    private Zombie nearestInRow(MatchState state, int row) {
        if (row < 0 || row >= state.getMap().getRows()) {
            return null;
        }
        Zombie nearest = null;
        for (Zombie zombie : state.getMap().getZombiesInRow(row)) {
            if (zombie.getCurrentHealth() > 0 && !zombie.isHypnotized()
                    && (nearest == null || Math.abs(zombie.getX() - x) < Math.abs(nearest.getX() - x))) {
                nearest = zombie;
            }
        }
        return nearest;
    }

    private boolean hit(MatchState state, Zombie target) {
        if (target == null) {
            return false;
        }
        Projectile projectile = new Projectile(baseDamage, 0, 1, shotTags());
        projectile.setX(x);
        projectile.setY(y);
        projectile.hitTarget(target);
        return true;
    }
}
