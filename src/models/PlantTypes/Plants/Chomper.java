package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.Enums.DamageType;
import models.PlantTypes.MeleeAttack;

/** Swallows the first adjacent zombie whole (ignoring armor), then digests for 40 seconds. */
public class Chomper extends MeleeAttack {
    private static final int DIGEST_TICKS = 400;
    private int digestTicks;

    @Override
    public void act(MatchState state) {
        if (isDead() || isFrozen()) {
            return;
        }
        if (digestTicks > 0) {
            digestTicks--;
            return;
        }
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            double distance = zombie.getX() - x;
            if (isEnemy(zombie) && distance >= 0 && distance <= 1) {
                zombie.takeDamage(Integer.MAX_VALUE, DamageType.POISON);
                System.out.println(name + " swallowed a " + zombie.getTypeName() + " whole!");
                digestTicks = DIGEST_TICKS;
                return;
            }
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        int eaten = 0;
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (isEnemy(zombie) && eaten < 3) {
                zombie.takeDamage(Integer.MAX_VALUE, DamageType.POISON);
                eaten++;
            }
        }
        digestTicks = 0;
    }
}
