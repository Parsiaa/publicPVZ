package models.Rules;

import models.LevelRule;
import models.MatchState;

public class VasebreakerRule implements LevelRule {
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    @Override
    public String getRuleInfo() { return null; }
    
    
    public void onTick() {} 
    @Override
    public void onTick(MatchState state) {}
    public boolean checkWinCondition() { return false; }
}
