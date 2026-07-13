package controllers;

import java.util.Random;
import models.MatchState;

/**
 * Chapter-specific terrain and events. The engine calls these hooks
 * at match start, at every wave start and on every tick.
 */
public abstract class ChapterMechanics {

    protected final Random random;

    protected ChapterMechanics(Random random) {
        this.random = random;
    }

    public void onMatchStart(MatchState state) {
    }

    public void onWaveStart(MatchState state, int waveNumber, boolean finalWave) {
    }

    public void onTick(MatchState state) {
    }
}
