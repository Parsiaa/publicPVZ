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
        if (attackFront(state)) {
            ticksSinceAction = 0;
        }
    }

    /** Hits the first enemy zombie directly in front; returns true if it connected. */
    protected boolean attackFront(MatchState state) {
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            double distance = zombie.getX() - x;
            if (isEnemy(zombie) && distance >= 0 && distance <= 1) {
                hit(zombie);
                return true;
            }
        }
        return false;
    }

    protected boolean isEnemy(Zombie zombie) {
        return zombie.getCurrentHealth() > 0 && !zombie.isHypnotized();
    }

    protected void hit(Zombie zombie) {
        zombie.takeDamage(baseDamage, meleeType());
    }

    protected DamageType meleeType() {
        if (hasTag(PlantTag.FIRE)) {
            return DamageType.FIRE;
        }
        if (hasTag(PlantTag.POISON)) {
            return DamageType.POISON;
        }
        return DamageType.NORMAL;
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            double distance = zombie.getX() - x;
            if (isEnemy(zombie) && distance >= 0 && distance <= 2) {
                zombie.takeDamage(baseDamage * 5, DamageType.NORMAL);
            }
        }
    }
}
