package models.Rules;

import models.LevelRule;
import models.MatchState;

/**
 * Plant What You Get level: a fixed amount of starting sun, no sky sun and no
 * sunflowers. Planting is free of cooldowns until the player releases the
 * zombie waves with "start zombie waves".
 */
public class PlantWhatYouGetRule implements LevelRule {

    private final int startingSun;

    public PlantWhatYouGetRule(int startingSun) {
        this.startingSun = startingSun;
    }

    public int getStartingSun() {
        return startingSun;
    }

    @Override
    public void onMatchStart(MatchState state) {
        state.setSunAmount(startingSun);
        state.setNightTime(true);
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return false;
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        return false;
    }

    @Override
    public void onTick(MatchState state) {
    }

    @Override
    public String getRuleInfo() {
        return "Plant What You Get level: " + startingSun + " starting sun and nothing more."
                + " Build your defense, then type 'start zombie waves'.";
    }
}
