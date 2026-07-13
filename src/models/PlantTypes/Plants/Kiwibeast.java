package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.MeleeAttack;

/** Sonic melee that grows through three stages, hitting a wider area for more damage. */
public class Kiwibeast extends MeleeAttack {
    private static final int STAGE2_TICKS = 240;
    private static final int STAGE3_TICKS = 720;
    private int ageTicks;

    @Override
    public void act(MatchState state) {
        if (isDead() || isFrozen()) {
            return;
        }
        ageTicks++;
        super.act(state);
    }

    @Override
    protected boolean attackFront(MatchState state) {
        int stage = ageTicks >= STAGE3_TICKS ? 3 : (ageTicks >= STAGE2_TICKS ? 2 : 1);
        int damage = baseDamage * stage;
        int radius = stage >= 2 ? 1 : 0;
        boolean hit = false;
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - x) <= radius && Math.abs(zombie.getY() - y) <= radius) {
                zombie.takeDamage(damage, meleeType());
                hit = true;
            }
        }
        return hit;
    }
}
