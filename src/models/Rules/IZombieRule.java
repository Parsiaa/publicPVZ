package models.Rules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import models.LevelRule;
import models.MatchState;
import models.Plant;
import models.Zombie;
import utils.PlantFactory;
import utils.ZombieFactory;

/**
 * I, Zombie mini-game: the player is the zombies. Preset plants defend the
 * left side; the player spends sun to place zombies on the right of the red
 * line and wins by eating the brain at the end of every row.
 */
public class IZombieRule implements LevelRule {

    public static final int RED_LINE_COLUMN = 6;
    private static final int SUN_PER_PRODUCTION = 25;
    private static final int PRODUCTION_INTERVAL_TICKS = 100;

    private final int stage;
    private final Random random;
    private final Map<String, Integer> zombieRoster = new LinkedHashMap<>();
    private final List<Zombie> sunProducers = new ArrayList<>();
    private int brainsRemaining;

    public IZombieRule(int stage, Random random) {
        this.stage = stage;
        this.random = random;
        fillRoster();
    }

    private void fillRoster() {
        if (stage == 1) {
            zombieRoster.put("Normal", 50);
            zombieRoster.put("ConeHead", 75);
            zombieRoster.put("BucketHead", 125);
            zombieRoster.put("Imp", 25);
            zombieRoster.put("Gargantuar", 300);
        } else if (stage == 2) {
            zombieRoster.put("Normal", 50);
            zombieRoster.put("ConeHead", 75);
            zombieRoster.put("Newspaper", 100);
            zombieRoster.put("Prospector", 75);
            zombieRoster.put("AllStar", 150);
        } else {
            zombieRoster.put("Imp", 25);
            zombieRoster.put("BucketHead", 125);
            zombieRoster.put("Knight", 150);
            zombieRoster.put("Pianist", 125);
            zombieRoster.put("Turquoise", 100);
        }
    }

    @Override
    public void onMatchStart(MatchState state) {
        state.setPlayingAsZombie(true);
        state.getMap().disableLawnMowers();
        brainsRemaining = state.getMap().getRows();
        placePresetPlants(state);
        placeSunProducers(state);
        System.out.println("Zombie roster: " + zombieRoster);
    }

    private void placePresetPlants(MatchState state) {
        String[] pool = {"Peashooter", "Wall-nut", "Chomper", "Sunflower"};
        int count = 6 + stage * 2;
        for (int i = 0; i < count; i++) {
            int row = random.nextInt(state.getMap().getRows());
            int column = random.nextInt(3);
            if (!state.getMap().getTile(row, column).getPlants().isEmpty()) {
                continue;
            }
            Plant plant = PlantFactory.createPlant(pool[random.nextInt(pool.length)]);
            if (plant != null) {
                state.getMap().addPlant(plant, row, column);
            }
        }
    }

    private void placeSunProducers(MatchState state) {
        for (int row = 0; row < state.getMap().getRows(); row++) {
            Zombie producer = ZombieFactory.createZombie("BucketHead", state.getDifficultyLevel());
            producer.getEffects().add("sunproducer");
            state.getMap().addZombie(producer, row, state.getMap().getColumns() - 1);
        }
        System.out.println("A sun-producing zombie waits in every lane. Protect them!");
    }

    /** Places a zombie for the player; returns an error text or null on success. */
    public String placeZombie(MatchState state, String type, int column, int row) {
        Integer cost = rosterCost(type);
        if (cost == null) {
            return "Error: Zombie '" + type + "' is not in this level's roster: " + zombieRoster;
        }
        if (column < RED_LINE_COLUMN) {
            return "Error: Zombies can only be placed right of the red line (column "
                    + (RED_LINE_COLUMN + 1) + " or further).";
        }
        if (state.getSunAmount() < cost) {
            return "Error: Not enough sun. " + type + " costs " + cost + ".";
        }
        state.setSunAmount(state.getSunAmount() - cost);
        Zombie zombie = ZombieFactory.createZombie(type, state.getDifficultyLevel());
        state.getMap().addZombie(zombie, row, column);
        return null;
    }

    private Integer rosterCost(String type) {
        for (Map.Entry<String, Integer> entry : zombieRoster.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(type)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void onTick(MatchState state) {
        produceSun(state);
        eatBrains(state);
    }

    private void produceSun(MatchState state) {
        if (state.getCurrentTick() % PRODUCTION_INTERVAL_TICKS != 0) {
            return;
        }
        int bonus = state.getCurrentTick() / 600 * 5;
        for (Zombie producer : sunProducersAlive(state)) {
            state.addSun(SUN_PER_PRODUCTION + bonus);
        }
        if (!sunProducersAlive(state).isEmpty()) {
            System.out.println("Your zombies produced sun. Sun: " + state.getSunAmount());
        }
    }

    private List<Zombie> sunProducersAlive(MatchState state) {
        List<Zombie> alive = new ArrayList<>();
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0 && zombie.getEffects().contains("sunproducer")) {
                alive.add(zombie);
            }
        }
        return alive;
    }

    private void eatBrains(MatchState state) {
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (zombie.getCurrentHealth() > 0 && zombie.getX() < 0) {
                state.getMap().removeZombie(zombie);
                brainsRemaining--;
                System.out.println("A zombie ate the brain in row " + ((int) zombie.getY() + 1)
                        + "! Brains left: " + brainsRemaining);
            }
        }
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return brainsRemaining <= 0;
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        int cheapest = Integer.MAX_VALUE;
        for (int cost : zombieRoster.values()) {
            cheapest = Math.min(cheapest, cost);
        }
        if (state.getSunAmount() >= cheapest) {
            return false;
        }
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0 && !zombie.getEffects().contains("sunproducer")) {
                return false;
            }
        }
        return brainsRemaining > 0 && sunProducersAlive(state).isEmpty();
    }

    @Override
    public String getRuleInfo() {
        return "I, Zombie (stage " + stage + "): place zombies with 'place zombie -t <type> -l (x, y)'"
                + " and eat the brain in every row.";
    }
}
