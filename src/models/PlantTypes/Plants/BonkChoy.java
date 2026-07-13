package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.MeleeAttack;

/** Punches the adjacent tiles both in front of and behind itself. */
public class BonkChoy extends MeleeAttack {
    @Override
    protected boolean attackFront(MatchState state) {
        boolean hit = false;
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - x) <= 1) {
                hit(zombie);
                hit = true;
            }
        }
        return hit;
    }
}
