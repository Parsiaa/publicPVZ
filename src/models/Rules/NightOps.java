package models.Rules;

import models.LevelRule;
import models.MatchState;

/**
 * Night Ops level: no sun falls from the sky; only sun producers keep you alive.
 */
public class NightOps implements LevelRule {

    @Override
    public void onMatchStart(MatchState state) {
        state.setNightTime(true);
    }

    @Override
    public void onTick(MatchState state) {
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
    public String getRuleInfo() {
        return "Night Ops level: no sun falls from the sky - rely on your sun producers.";
    }
}
