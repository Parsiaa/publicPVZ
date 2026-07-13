package models.PlantTypes;

import models.MatchState;
import models.Plant;
import models.Projectile;
import models.Zombie;

/** Targets the frontmost zombie anywhere on the board and locks on automatically. */
public class Homing extends Plant {
    protected int ticksSinceAction;

    @Override
    public void act(MatchState state) {
        if (isDead() || isFrozen()) {
            return;
        }
        ticksSinceAction++;
        int intervalTicks = (int) Math.max(1, actionInterval * 10);
        if (ticksSinceAction < intervalTicks) {
            return;
        }
        Zombie target = findTarget(state);
        if (target == null) {
            return;
        }
        ticksSinceAction = 0;
        onHit(state, target);
    }

    /** What happens to a locked-on target; by default a homing projectile. */
    protected void onHit(MatchState state, Zombie target) {
        Projectile projectile = new Projectile(baseDamage, 1, 1, tags);
        projectile.hitTarget(target);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (int i = 0; i < 5; i++) {
            Zombie target = findTarget(state);
            if (target == null) {
                return;
            }
            Projectile projectile = new Projectile(baseDamage * 2, 1, 1, tags);
            projectile.hitTarget(target);
        }
    }

    protected Zombie findTarget(MatchState state) {
        Zombie best = null;
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0 && !zombie.isHypnotized()) {
                if (best == null || zombie.getX() < best.getX()) {
                    best = zombie;
                }
            }
        }
        return best;
    }
}
