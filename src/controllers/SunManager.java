package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import models.MatchState;
import models.Plant;
import models.Sun;
import models.Zombie;
import models.Enums.DamageType;
import models.Enums.SunType;

/**
 * Handles the suns that fall from the sky: drop timing, falling, landing and collection.
 * A sun appears every max(6 + 0.05t, 12) seconds (scaled by difficulty) and lands after 5 seconds.
 */
public class SunManager {

    private static final int FALL_DURATION_TICKS = 50;
    private static final int RADIOACTIVE_ZOMBIE_DAMAGE = 150;
    private static final int RADIOACTIVE_PLANT_DAMAGE = 80;

    private static class SkySun {
        private final Sun sun;
        private final int row;
        private final int column;
        private int ticksUntilLand;

        SkySun(Sun sun, int row, int column) {
            this.sun = sun;
            this.row = row;
            this.column = column;
            this.ticksUntilLand = FALL_DURATION_TICKS;
        }
    }

    private final Random random;
    private final List<SkySun> skySuns;
    private double secondsUntilNextDrop;

    public SunManager(Random random) {
        this.random = random;
        this.skySuns = new ArrayList<>();
        this.secondsUntilNextDrop = -1;
    }

    public void update(MatchState state) {
        tickFallingSuns();
        if (state.isNightTime()) {
            return;
        }
        if (secondsUntilNextDrop < 0) {
            secondsUntilNextDrop = nextDropInterval(state);
        }
        secondsUntilNextDrop -= 0.1;
        if (secondsUntilNextDrop <= 0) {
            dropNewSun(state);
            secondsUntilNextDrop = nextDropInterval(state);
        }
    }

    private double nextDropInterval(MatchState state) {
        double secondsSinceStart = state.getCurrentTick() / 10.0;
        double base = Math.max(6 + 0.05 * secondsSinceStart, 12);
        return base * state.getDifficultyLevel() / 3.0;
    }

    private void tickFallingSuns() {
        for (SkySun skySun : skySuns) {
            if (!skySun.sun.isFalling()) {
                continue;
            }
            skySun.ticksUntilLand--;
            if (skySun.ticksUntilLand <= 0) {
                skySun.sun.fall();
                System.out.println("Sun reached the ground at position ("
                        + (skySun.column + 1) + ", " + (skySun.row + 1) + ")");
            }
        }
    }

    private void dropNewSun(MatchState state) {
        SunType type = rollSunType();
        int amount = (type == SunType.SPECIAL) ? 100 : 25;
        int row = random.nextInt(state.getMap().getRows());
        int column = random.nextInt(state.getMap().getColumns());
        skySuns.add(new SkySun(new Sun(type, amount, true), row, column));
        System.out.println("New " + type.name().toLowerCase() + " sun is dropping at position ("
                + (column + 1) + ", " + (row + 1) + ")");
    }

    private SunType rollSunType() {
        double roll = random.nextDouble();
        if (roll < 0.80) {
            return SunType.NORMAL;
        }
        if (roll < 0.95) {
            return SunType.SPECIAL;
        }
        return SunType.RADIOACTIVE;
    }

    /**
     * Collects the sky sun on the given tile. Returns the collected amount,
     * 0 if a falling radioactive sun exploded, or -1 if there is no sky sun there.
     */
    public int collectAt(MatchState state, int row, int column) {
        Iterator<SkySun> iterator = skySuns.iterator();
        while (iterator.hasNext()) {
            SkySun skySun = iterator.next();
            if (skySun.row != row || skySun.column != column) {
                continue;
            }
            iterator.remove();
            if (skySun.sun.isFalling() && skySun.sun.getType() == SunType.RADIOACTIVE) {
                explodeRadioactiveSun(state, row, column);
                return 0;
            }
            state.addSun(skySun.sun.getAmount());
            return skySun.sun.getAmount();
        }
        return -1;
    }

    private void explodeRadioactiveSun(MatchState state, int row, int column) {
        System.out.println("The radioactive sun exploded at (" + (column + 1) + ", " + (row + 1) + ")!");
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0 && Math.abs(zombie.getX() - column) <= 2
                    && Math.abs(zombie.getY() - row) <= 2) {
                zombie.takeDamage(RADIOACTIVE_ZOMBIE_DAMAGE, DamageType.FIRE);
            }
        }
        for (Plant plant : new ArrayList<>(state.getMap().getAllPlants())) {
            if (!plant.isDead() && Math.abs(plant.getX() - column) <= 1 && Math.abs(plant.getY() - row) <= 1) {
                plant.takeDamage(RADIOACTIVE_PLANT_DAMAGE);
            }
        }
    }

    public boolean hasSunAt(int row, int column) {
        for (SkySun skySun : skySuns) {
            if (skySun.row == row && skySun.column == column) {
                return true;
            }
        }
        return false;
    }
}
