package controllers;

import models.MatchState;
import models.ScoreManager;
import models.User;
import models.Enums.ScoreEvent;

/**
 * Watches a score-run match and turns what happens into MewPoints:
 * fast kills, multi kills, sync kills, no-damage waves and plant food combos.
 */
public class ScoreTracker {

    private static final int FAST_KILL_WINDOW_TICKS = 50;
    private static final int PLANT_FOOD_WINDOW_TICKS = 20;

    private final ScoreManager scoreManager = new ScoreManager();
    private boolean active;
    private int lastWaveSeen;
    private int waveStartTick;
    private int waveStartLostPlants;
    private int lastPlantFoodTick;

    public void reset(boolean scoreMode) {
        this.active = scoreMode;
        this.lastWaveSeen = 0;
        this.waveStartTick = 0;
        this.waveStartLostPlants = 0;
        this.lastPlantFoodTick = -PLANT_FOOD_WINDOW_TICKS;
        scoreManager.setCurrentMeowPoints(0);
    }

    public void onWaveChange(MatchState state, int currentWave) {
        if (!active || currentWave == lastWaveSeen) {
            return;
        }
        if (lastWaveSeen > 0 && state.getLostPlantsCount() == waveStartLostPlants) {
            scoreManager.registerEvent(ScoreEvent.NO_DAMAGE_WAVE);
        }
        lastWaveSeen = currentWave;
        waveStartTick = state.getCurrentTick();
        waveStartLostPlants = state.getLostPlantsCount();
    }

    public void onKills(MatchState state, int killsThisTick) {
        if (!active || killsThisTick == 0) {
            return;
        }
        for (int i = 0; i < killsThisTick; i++) {
            scoreManager.registerKill();
        }
        if (state.getCurrentTick() - waveStartTick <= FAST_KILL_WINDOW_TICKS) {
            scoreManager.registerEvent(ScoreEvent.FASTKILL);
        }
        if (killsThisTick >= 2) {
            scoreManager.registerEvent(ScoreEvent.MUTLI_KILL);
        }
        if (killsThisTick >= 3) {
            scoreManager.registerEvent(ScoreEvent.SYNC_KILL);
        }
        if (state.getCurrentTick() - lastPlantFoodTick <= PLANT_FOOD_WINDOW_TICKS) {
            scoreManager.registerEvent(ScoreEvent.PLANT_FOOD_COMBO);
        }
    }

    public void onPlantFood(MatchState state) {
        if (active) {
            lastPlantFoodTick = state.getCurrentTick();
        }
    }

    public void finish(User user, boolean won) {
        if (!active) {
            return;
        }
        int score = scoreManager.getCurrentMeowPoints();
        System.out.println("Final MewPoints: " + score + (won ? " (victory!)" : ""));
        if (score > user.getHighestMeowPointScore()) {
            user.setHighestMeowPointScore(score);
            System.out.println("New personal best!");
        }
        active = false;
    }
}
