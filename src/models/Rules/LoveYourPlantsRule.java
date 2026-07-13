package models.Rules;

import models.LevelRule;
import models.MatchState;

/**
 * Love Your Plants level: losing more than the allowed number of plants loses the level.
 */
public class LoveYourPlantsRule implements LevelRule {

    private final int maxPlantLoss;

    public LoveYourPlantsRule(int maxPlantLoss) {
        this.maxPlantLoss = maxPlantLoss;
    }

    public int getMaxPlantLoss() {
        return maxPlantLoss;
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        if (state.getLostPlantsCount() >= maxPlantLoss) {
            System.out.println("You lost " + state.getLostPlantsCount() + " plants!");
            return true;
        }
        return false;
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return false;
    }

    @Override
    public void onTick(MatchState state) {
    }

    @Override
    public String getRuleInfo() {
        return "Love Your Plants level: losing " + maxPlantLoss + " plants means losing the level.";
    }
}
