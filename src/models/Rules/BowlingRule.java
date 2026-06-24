package models.Rules;

import models.LevelRule;
import models.MatchState;

public class BowlingRule implements LevelRule {
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    
    public void onTick() {}
    @Override
    public void onTick(MatchState state) {}
    
    @Override
    public String getRuleInfo() { return null; }
}
