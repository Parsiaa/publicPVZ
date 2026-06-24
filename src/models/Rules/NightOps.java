package models.Rules;

import models.LevelRule;
import models.MatchState;

public class NightOps implements LevelRule {
    public void onTick() {}
    @Override
    public void onTick(MatchState state) {}
    
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    @Override
    public String getRuleInfo() { return null; }
}