package models.PlantTypes;

import java.util.ArrayList;
import java.util.List;
import models.MatchState;
import models.Plant;
import models.Projectile;
import models.Tile;
import models.Zombie;
import models.Enums.ObstacleType;
import models.Enums.PlantTag;

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
        if (fire(state)) {
            ticksSinceAction = 0;
        }
    }

    /** Fires the plant's shot(s); returns true if it actually acted (had a target). */
    protected boolean fire(MatchState state) {
        return fireInRow(state, (int) y, x, baseDamage);
    }

    /** Fires a straight shot down a row from a given x; returns true if a target was hit. */
    protected boolean fireInRow(MatchState state, int row, double fromX, int damage) {
        Zombie target = state.getMap().getFirstZombieAhead(row, fromX);
        if (target == null) {
            return false;
        }
        Tile blocking = state.getMap().getBlockingTileAhead(row, fromX, target.getX());
        if (blocking != null) {
            hitBlockingTile(state, blocking, damage);
            return true;
        }
        shoot(state, row, fromX, target, damage);
        return true;
    }

    /** Builds and resolves a straight projectile, applying Torchwood ignition on pea shots. */
    protected void shoot(MatchState state, int row, double fromX, Zombie target, int damage) {
        List<PlantTag> shotTags = shotTags();
        if (hasTag(PlantTag.PEA) && state.getMap().hasIgniterBetween(row, fromX, target.getX())
                && !shotTags.contains(PlantTag.FIRE)) {
            shotTags.add(PlantTag.FIRE);
        }
        Projectile projectile = new Projectile(damage, 0, 1, shotTags);
        projectile.setX(fromX);
        projectile.setY(row);
        projectile.hitTarget(target);
    }

    protected List<PlantTag> shotTags() {
        return tags == null ? new ArrayList<>() : new ArrayList<>(tags);
    }

    /** Straight shots that hit a grave damage the grave instead of the zombie behind it. */
    protected void hitBlockingTile(MatchState state, Tile blocking, int damage) {
        if (!blocking.takeTileDamage(damage)) {
            return;
        }
        System.out.println("The grave at (" + (blocking.getColumn() + 1) + ", "
                + (blocking.getRow() + 1) + ") is destroyed.");
        if (blocking.getGraveLoot() == ObstacleType.GRAVE_SUN) {
            state.addSun(50);
            System.out.println("The grave dropped 50 sun!");
        } else if (blocking.getGraveLoot() == ObstacleType.GRAVE_FOOD) {
            state.addPlantFood();
            System.out.println("The grave dropped a plant food; you have "
                    + state.getPlantFoods() + " plant foods now.");
        }
        blocking.setGraveLoot(null);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (zombie.getCurrentHealth() > 0 && zombie.getX() >= x) {
                Projectile projectile = new Projectile(baseDamage * 5, 0, 1, shotTags());
                projectile.hitTarget(zombie);
            }
        }
    }
}
