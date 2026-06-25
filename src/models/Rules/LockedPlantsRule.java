package models.Rules;

import java.util.List;

import models.LevelRule;
import models.MatchState;

public class LockedPlantsRule implements LevelRule {
    private List<String> LockedPlants;

    public List<String> getLockedPlants() {
        return LockedPlants;
    }

    public void setLockedPlants(List<String> lockedPlants) {
        LockedPlants = lockedPlants;
    }

    @Override
    public String getRuleInfo() { return null; }
    
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    @Override
    public void onTick(MatchState state) {}
}
