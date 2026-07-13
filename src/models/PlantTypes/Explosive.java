package models.PlantTypes;

import java.util.ArrayList;
import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;

/**
 * Base for explosive plants. By default it behaves like a proximity mine:
 * it detonates in a 3x3 area when a zombie comes within one tile. Instant
 * bombs, lane bombs and single-target crushers override the trigger.
 */
public class Explosive extends Plant {
    protected int explosionRadius = 1;

    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - x) <= 1 && Math.abs(zombie.getY() - y) <= 1) {
                explode(state, explosionRadius);
                return;
            }
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        explode(state, explosionRadius + 1);
    }

    protected boolean isEnemy(Zombie zombie) {
        return zombie.getCurrentHealth() > 0 && !zombie.isHypnotized();
    }

    /** Deals this plant's damage to every enemy zombie within the radius, then removes the plant. */
    public void explode(MatchState state, int radius) {
        damageArea(state, x, y, radius, baseDamage);
        finish(state);
    }

    /** Damages every enemy zombie within a square radius around a point. */
    protected void damageArea(MatchState state, double centerX, double centerY, int radius, int damage) {
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (isEnemy(zombie) && Math.abs(zombie.getX() - centerX) <= radius
                    && Math.abs(zombie.getY() - centerY) <= radius) {
                zombie.takeDamage(damage, DamageType.FIRE);
            }
        }
    }

    /** Removes the plant from the board after it has done its job. */
    protected void finish(MatchState state) {
        this.health = 0;
        state.getMap().removePlant(this);
        System.out.println("Plant " + name + " exploded at (" + (int) x + ", " + (int) y + ")!");
    }
}
