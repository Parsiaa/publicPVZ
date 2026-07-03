package models.PlantTypes;

import models.MatchState;
import models.Plant;
import models.Projectile;
import models.Zombie;

public class Lobber extends Plant {
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
        Projectile projectile = new Projectile(baseDamage, 1, 1, tags);
        projectile.setX(x);
        projectile.setY(y);
        projectile.hitTarget(target);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0) {
                Projectile projectile = new Projectile(baseDamage * 3, 1, 1, tags);
                projectile.hitTarget(zombie);
            }
        }
    }
}
