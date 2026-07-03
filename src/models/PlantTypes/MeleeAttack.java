package models.PlantTypes;

import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;
import models.Enums.PlantTag;

public class MeleeAttack extends Plant {
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
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            double distance = zombie.getX() - x;
            if (zombie.getCurrentHealth() > 0 && distance >= 0 && distance <= 1) {
                ticksSinceAction = 0;
                DamageType type = hasTag(PlantTag.POISON) ? DamageType.POISON : DamageType.NORMAL;
                zombie.takeDamage(baseDamage, type);
                return;
            }
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            double distance = zombie.getX() - x;
            if (zombie.getCurrentHealth() > 0 && distance >= 0 && distance <= 2) {
                zombie.takeDamage(baseDamage * 5, DamageType.NORMAL);
            }
        }
    }
}
