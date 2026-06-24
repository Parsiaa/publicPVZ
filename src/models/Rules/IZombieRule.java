package models.Rules;

import models.LevelRule;
import models.MatchState;

public class IZombieRule implements LevelRule {
    public boolean checkLossCondition() { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    
    public boolean checkWinCondition() { return false; }
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    
    @Override
    public String getRuleInfo() { return null; }
    @Override
    public void onTick(MatchState state) {}
}
