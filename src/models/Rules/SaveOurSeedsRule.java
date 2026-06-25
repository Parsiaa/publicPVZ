package models.Rules;

import java.util.List;

import models.LevelRule;
import models.MatchState;
import models.Plant;

public class SaveOurSeedsRule implements LevelRule {
    private List<Plant> protectedPlants;

    public List<Plant> getProtectedPlants() {
        return protectedPlants;
    }
    public void setProtectedPlants(List<Plant> protectedPlants) {
        this.protectedPlants = protectedPlants;
    }
    public boolean checkLossCondition() { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    
    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public String getRuleInfo() { return null; }
    @Override
    public void onTick(MatchState state) {}
}