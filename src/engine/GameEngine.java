package engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import models.MatchState;
import models.Plant;
import models.SeedPacket;
import models.Sun;
import models.Tile;
import models.Zombie;
import models.Enums.DamageType;
import models.Enums.SunType;
import models.PlantTypes.SunProducer;

// Core match orchestrator. Owns the tick loop and the mechanics that run every
// tick. Built incrementally, section by section, following the design doc.
// Step 1 covers time advancement and the "sun from the sky" mechanic.
public class GameEngine {
    public static final int TICKS_PER_SECOND = 10;
    private static final int SUN_FALL_TRAVEL_TICKS = 5 * TICKS_PER_SECOND; // 5 seconds to reach the ground
    private static final int NORMAL_SUN_AMOUNT = 25;
    private static final int SPECIAL_SUN_AMOUNT = 100;

    private final MatchState state;
    private final Random random;
    private final List<FallingSun> fallingSuns = new ArrayList<>();
    private int ticksUntilNextSunfall;
    private boolean sunfallEnabled = true;

    public GameEngine(MatchState state) {
        this(state, new Random());
    }

    // Deterministic constructor for tests.
    public GameEngine(MatchState state, Random random) {
        this.state = state;
        this.random = random;
        this.ticksUntilNextSunfall = computeSunfallInterval();
    }

    public MatchState getState() {
        return state;
    }

    public void advanceTicks(int count) {
        for (int i = 0; i < count; i++) {
            tick();
        }
    }

    private void tick() {
        state.incrementTick();
        updateSunfall();
        updateSeedPackets();
        updatePlants();
    }

    // Seed-packet recharge: cooldowns are tracked in seconds and tick down by
    // 0.1s (one tick) at a time.
    private void updateSeedPackets() {
        for (SeedPacket packet : state.getSeedPackets()) {
            if (packet.getCurrentCooldown() > 0) {
                packet.setCurrentCooldown(Math.max(0, packet.getCurrentCooldown() - 1.0 / TICKS_PER_SECOND));
            }
        }
    }

    private void updatePlants() {
        for (Plant plant : new ArrayList<>(state.getMap().getAllPlants())) {
            if (!plant.isDead()) {
                plant.act(state);
            }
        }
    }

    // --- Sun from the sky -------------------------------------------------

    private void updateSunfall() {
        // A new sun drops from the sky every max(6 + 0.05t, 12) seconds, unless
        // the environment (e.g. Dark Ages / night) forbids it.
        if (sunfallEnabled && !state.isNightTime()) {
            ticksUntilNextSunfall--;
            if (ticksUntilNextSunfall <= 0) {
                spawnFallingSun();
                ticksUntilNextSunfall = computeSunfallInterval();
            }
        }
        for (FallingSun fs : fallingSuns) {
            if (!fs.landed) {
                fs.ticksUntilLand--;
                if (fs.ticksUntilLand <= 0) {
                    fs.landed = true;
                    fs.sun.fall(); // radioactive suns turn into normal suns on landing
                    System.out.println("Sun reached the ground at position ("
                            + (fs.col + 1) + ", " + (fs.row + 1) + ")");
                }
            }
        }
    }

    private int computeSunfallInterval() {
        double seconds = Math.max(6 + 0.05 * secondsElapsed(), 12);
        return (int) Math.round(seconds * TICKS_PER_SECOND);
    }

    private double secondsElapsed() {
        return state.getCurrentTick() / (double) TICKS_PER_SECOND;
    }

    private void spawnFallingSun() {
        double roll = random.nextDouble();
        SunType type;
        int amount;
        if (roll < 0.80) {
            type = SunType.NORMAL;
            amount = NORMAL_SUN_AMOUNT;
        } else if (roll < 0.95) {
            type = SunType.SPECIAL;
            amount = SPECIAL_SUN_AMOUNT;
        } else {
            type = SunType.RADIOACTIVE;
            amount = 0; // explodes if collected mid-air; becomes a normal sun if it lands
        }
        int col = random.nextInt(state.getMap().getColumns());
        int row = random.nextInt(state.getMap().getRows());
        fallingSuns.add(new FallingSun(new Sun(type, amount, true), col, row, SUN_FALL_TRAVEL_TICKS));
        System.out.println("New " + typeName(type) + " sun is dropping at position ("
                + (col + 1) + ", " + (row + 1) + ")");
    }

    // Collects a sun at the given 1-indexed (x=column, y=row) tile: either a sky
    // sun resting on / falling to the tile, or a sun waiting on a sun-producer
    // plant there. Returns the amount added to the bank, 0 if a radioactive sun
    // exploded, or -1 if there was nothing to collect.
    public int collectSunAt(int x, int y) {
        int col = x - 1;
        int row = y - 1;
        for (Iterator<FallingSun> it = fallingSuns.iterator(); it.hasNext();) {
            FallingSun fs = it.next();
            if (fs.col == col && fs.row == row) {
                if (!fs.landed && fs.sun.getType() == SunType.RADIOACTIVE) {
                    it.remove();
                    explodeRadioactive(col, row);
                    return 0; // exploded rather than banked
                }
                int amount = fs.sun.getAmount();
                state.addSun(amount);
                it.remove();
                return amount;
            }
        }
        Tile tile = state.getMap().getTile(row, col);
        if (tile != null) {
            for (Plant plant : tile.getPlants()) {
                if (plant instanceof SunProducer) {
                    SunProducer producer = (SunProducer) plant;
                    if (producer.hasPendingSun()) {
                        int before = state.getSunAmount();
                        producer.collectSun(state);
                        return state.getSunAmount() - before;
                    }
                }
            }
        }
        return -1;
    }

    private void explodeRadioactive(int col, int row) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0
                    && Math.abs(zombie.getX() - col) <= 2 && Math.abs(zombie.getY() - row) <= 2) {
                zombie.takeDamage(80, DamageType.FIRE);
            }
        }
        for (Plant plant : state.getMap().getAllPlants()) {
            if (!plant.isDead()
                    && Math.abs(plant.getX() - col) <= 1 && Math.abs(plant.getY() - row) <= 1) {
                plant.takeDamage(80);
            }
        }
        System.out.println("A radioactive sun exploded at (" + (col + 1) + ", " + (row + 1) + ")!");
    }

    public List<FallingSun> getFallingSuns() {
        return fallingSuns;
    }

    public void setSunfallEnabled(boolean enabled) {
        this.sunfallEnabled = enabled;
    }

    private static String typeName(SunType type) {
        switch (type) {
            case SPECIAL:
                return "special";
            case RADIOACTIVE:
                return "radioactive";
            default:
                return "normal";
        }
    }

    // A sun descending from (or resting on) a tile. Coordinates are 0-indexed
    // grid indices; the engine converts to/from 1-indexed for user I/O.
    public static class FallingSun {
        private final Sun sun;
        private final int col;
        private final int row;
        private int ticksUntilLand;
        private boolean landed;

        FallingSun(Sun sun, int col, int row, int ticksUntilLand) {
            this.sun = sun;
            this.col = col;
            this.row = row;
            this.ticksUntilLand = ticksUntilLand;
            this.landed = false;
        }

        public Sun getSun() { return sun; }
        public int getCol() { return col; }
        public int getRow() { return row; }
        public boolean isLanded() { return landed; }
    }
}
