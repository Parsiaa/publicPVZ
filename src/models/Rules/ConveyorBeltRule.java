package models.Rules;

import models.LevelRule;
import models.MatchState;

public class ConveyorBeltRule implements LevelRule {
    private double timeSinceLastPlant;

    public double getTimeSinceLastPlant() {
        return timeSinceLastPlant;
    }

    public void setTimeSinceLastPlant(double timeSinceLastPlant) {
        this.timeSinceLastPlant = timeSinceLastPlant;
    }

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
