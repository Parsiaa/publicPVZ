package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.MeleeAttack;

/** Sonic melee attack that hits every zombie in the 3x3 area around it. */
public class PhatBeet extends MeleeAttack {
    @Override
    protected boolean attackFront(MatchState state) {
        boolean hit = false;
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - x) <= 1 && Math.abs(zombie.getY() - y) <= 1) {
                hit(zombie);
                hit = true;
            }
        }
        return hit;
    }
}
