package models.PlantTypes;

import java.util.ArrayList;
import models.MatchState;
import models.Plant;
import models.Projectile;
import models.Zombie;

/**
 * Lobbed-shot plants (Cabbage-pult, Melon-pult, ...). Lobs arc over obstacles,
 * so unlike straight shooters they ignore graves and blocking tiles.
 */
public class Lobber extends Plant {
    protected int ticksSinceAction;
    /** Splash radius around the struck zombie; 0 means single-target. */
    protected int splashRadius;

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
        Zombie target = state.getMap().getFirstZombieAhead((int) y, x);
        if (target == null) {
            return;
        }
        ticksSinceAction = 0;
        lob(state, target, baseDamage);
    }

    /** Applies the lob to the target and, for AoE lobs, its close neighbours. */
    protected void lob(MatchState state, Zombie target, int damage) {
        strike(target, damage);
        if (splashRadius > 0) {
            for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
                if (zombie != target && zombie.getCurrentHealth() > 0 && !zombie.isHypnotized()
                        && Math.abs(zombie.getX() - target.getX()) <= splashRadius
                        && Math.abs(zombie.getY() - target.getY()) <= splashRadius) {
                    strike(zombie, damage);
                }
            }
        }
    }

    protected void strike(Zombie zombie, int damage) {
        Projectile projectile = new Projectile(damage, 1, 1, tags);
        projectile.setX(x);
        projectile.setY(y);
        projectile.hitTarget(zombie);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0 && !zombie.isHypnotized()) {
                Projectile projectile = new Projectile(baseDamage * 3, 1, 1, tags);
                projectile.hitTarget(zombie);
            }
        }
    }
}
