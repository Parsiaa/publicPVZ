package models.Rules;

import models.LevelRule;
import models.MatchState;

public class LoveYourPlantsRule implements LevelRule {
    private int maxPlantLoss;

    public int getMaxPlantLoss() {
        return maxPlantLoss;
    }
    public void setMaxPlantLoss(int maxPlantLoss) {
        this.maxPlantLoss = maxPlantLoss;
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