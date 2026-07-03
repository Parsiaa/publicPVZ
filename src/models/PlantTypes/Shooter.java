package models.PlantTypes;

import models.MatchState;
import models.Plant;
import models.Projectile;
import models.Zombie;

public class Shooter extends Plant {
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
        Zombie target = state.getMap().getFirstZombieAhead((int) y, x);
        if (target == null) {
            return;
        }
        ticksSinceAction = 0;
        Projectile projectile = new Projectile(baseDamage, 0, 1, tags);
        projectile.setX(x);
        projectile.setY(y);
        projectile.hitTarget(target);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (zombie.getCurrentHealth() > 0 && zombie.getX() >= x) {
                Projectile projectile = new Projectile(baseDamage * 5, 0, 1, tags);
                projectile.hitTarget(zombie);
            }
        }
    }
}

