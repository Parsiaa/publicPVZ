package models.Rules;

import models.LevelRule;
import models.MatchState;

public class ZombotanyRule implements LevelRule {
    @Override
    public boolean checkWinCondition(MatchState state) {
        return false;
    }
    @Override
    public boolean checkLossCondition(MatchState state) {
        return false;
    }
    @Override
    public String getRuleInfo() { return null; }
    @Override
    public void onTick(MatchState state) {}
}

