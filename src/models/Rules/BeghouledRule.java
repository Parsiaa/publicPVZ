package models.Rules;

import java.util.Map;

import models.LevelRule;
import models.MatchState;
import models.Tile;

public class BeghouledRule implements LevelRule {
    private Map<String, Integer> upgradeCosts;

    public Map<String, Integer> getUpgradeCosts() {
        return upgradeCosts;
    }
    public void setUpgradeCosts(Map<String, Integer> upgradeCosts) {
        this.upgradeCosts = upgradeCosts;
    }
    public int checkMatches(MatchState state) { return 0; }
    public void handleCraterCreation(Tile tile) {}
    public void swapPlants(int x1, int y1, int x2, int y2) {}

    @Override
    public boolean checkWinCondition(MatchState state) { return false; }
    @Override
    public boolean checkLossCondition(MatchState state) { return false; }
    @Override
    public String getRuleInfo() { return null; }
    @Override
    public void onTick(MatchState state) {}
}