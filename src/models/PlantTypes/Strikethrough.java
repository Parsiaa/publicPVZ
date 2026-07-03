package models.PlantTypes;

import models.MatchState;
import models.Plant;
import models.Projectile;
import models.Zombie;

public class Strikethrough extends Plant {
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
        boolean anyTarget = false;
        Projectile projectile = new Projectile(baseDamage, 0, Integer.MAX_VALUE, tags);
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (zombie.getCurrentHealth() > 0 && zombie.getX() >= x) {
                projectile.hitTarget(zombie);
                anyTarget = true;
            }
        }
        if (anyTarget) {
            ticksSinceAction = 0;
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        Projectile projectile = new Projectile(baseDamage * 5, 0, Integer.MAX_VALUE, tags);
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (zombie.getCurrentHealth() > 0) {
                projectile.hitTarget(zombie);
            }
        }
    }
}
