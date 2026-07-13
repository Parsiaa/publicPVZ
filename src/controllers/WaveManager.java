package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.MatchState;
import models.Wave;
import models.Zombie;
import utils.ZombieFactory;

/**
 * Builds the zombie waves of a level and decides when each wave should start.
 * A wave starts once 75% of the previous wave's total health is gone.
 */
public class WaveManager {

    private static final int DEFAULT_WAVE_COUNT = 4;
    private static final double BASE_WAVE_BUDGET = 1000;
    private static final int FIRST_WAVE_DELAY_TICKS = 50;
    private static final double GLOWING_CHANCE = 0.05;

    private final Random random;
    private final ChapterMechanics mechanics;
    private boolean released;
    private int currentWaveNumber;
    private int currentWaveInitialHealth;

    public WaveManager(Random random, ChapterMechanics mechanics) {
        this.random = random;
        this.mechanics = mechanics;
        this.released = true;
        this.currentWaveNumber = 0;
        this.currentWaveInitialHealth = 0;
    }

    public void generateWaves(MatchState state) {
        state.getWaves().clear();
        double budget = BASE_WAVE_BUDGET * state.getDifficultyLevel() / 3.0;
        double previousBudget = budget;
        for (int i = 1; i <= DEFAULT_WAVE_COUNT; i++) {
            double waveBudget = (i == DEFAULT_WAVE_COUNT) ? previousBudget * 2 : budget;
            state.getWaves().add(buildWave(waveBudget, state.getDifficultyLevel()));
            previousBudget = waveBudget;
            budget *= 1.25;
        }
    }

    private Wave buildWave(double budget, int difficultyLevel) {
        Wave wave = new Wave();
        double remaining = budget;
        String type = pickWeightedAffordableType(remaining);
        while (type != null) {
            wave.addZombie(ZombieFactory.createZombie(type, difficultyLevel));
            remaining -= ZombieFactory.getWaveCost(type);
            type = pickWeightedAffordableType(remaining);
        }
        return wave;
    }

    /** Weighted random pick among spawnable zombies whose wave cost fits in the remaining budget. */
    private String pickWeightedAffordableType(double remainingBudget) {
        List<String> affordable = new ArrayList<>();
        int totalWeight = 0;
        for (String candidate : ZombieFactory.getSpawnableTypes()) {
            int cost = ZombieFactory.getWaveCost(candidate);
            if (cost > 0 && cost <= remainingBudget) {
                affordable.add(candidate);
                totalWeight += ZombieFactory.getWeight(candidate);
            }
        }
        if (affordable.isEmpty() || totalWeight <= 0) {
            return null;
        }
        int roll = random.nextInt(totalWeight);
        for (String candidate : affordable) {
            roll -= ZombieFactory.getWeight(candidate);
            if (roll < 0) {
                return candidate;
            }
        }
        return affordable.get(affordable.size() - 1);
    }

    public void update(MatchState state) {
        if (!released || state.getWaves().isEmpty()) {
            return;
        }
        if (currentWaveNumber == 0) {
            if (state.getCurrentTick() >= FIRST_WAVE_DELAY_TICKS) {
                spawnNextWave(state);
            }
            return;
        }
        if (currentWaveNumber < state.getWaves().size() && isCurrentWaveWeakEnough(state)) {
            spawnNextWave(state);
        }
    }

    private boolean isCurrentWaveWeakEnough(MatchState state) {
        if (currentWaveInitialHealth <= 0) {
            return true;
        }
        int remaining = 0;
        for (Zombie zombie : state.getCurrentWave().getWaveZombies()) {
            remaining += Math.max(0, zombie.getCurrentHealth());
        }
        return remaining <= currentWaveInitialHealth * 0.25;
    }

    private void spawnNextWave(MatchState state) {
        Wave wave = state.getNextWave();
        if (wave == null) {
            return;
        }
        currentWaveNumber++;
        wave.startWave();
        if (currentWaveNumber == state.getWaves().size()) {
            System.out.println("The final wave has come.");
        } else {
            System.out.println("Wave " + currentWaveNumber + " started.");
        }
        currentWaveInitialHealth = 0;
        for (Zombie zombie : wave.getWaveZombies()) {
            spawnZombie(state, zombie);
            currentWaveInitialHealth += zombie.getCurrentHealth();
        }
        if (mechanics != null) {
            mechanics.onWaveStart(state, currentWaveNumber, currentWaveNumber == state.getWaves().size());
        }
    }

    private void spawnZombie(MatchState state, Zombie zombie) {
        int lane = random.nextInt(state.getMap().getRows());
        if (random.nextDouble() < GLOWING_CHANCE) {
            zombie.getEffects().add("glowing");
        }
        state.getMap().addZombie(zombie, lane, state.getMap().getColumns() - 1);
        System.out.println("Zombie " + zombie.getTypeName() + " spawned at wave " + currentWaveNumber
                + " in lane " + (lane + 1) + " which costed " + ZombieFactory.getWaveCost(zombie.getTypeName()) + ".");
    }

    public boolean allWavesDefeated(MatchState state) {
        if (state.getWaves().isEmpty() || currentWaveNumber < state.getWaves().size()) {
            return false;
        }
        for (Wave wave : state.getWaves()) {
            if (!wave.isCompletelyDefeated()) {
                return false;
            }
        }
        return true;
    }

    public void release() {
        this.released = true;
    }

    public void hold() {
        this.released = false;
    }

    public boolean isReleased() {
        return released;
    }

    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }
}
