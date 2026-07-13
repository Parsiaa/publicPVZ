package models.Rules;

import java.util.List;
import models.LevelRule;
import models.MatchState;

/**
 * Locked Plants level: some plants are unavailable and fewer seed slots can be used.
 */
public class LockedPlantsRule implements LevelRule {

    private final List<String> bannedPlants;
    private final int maxSlots;

    public LockedPlantsRule(List<String> bannedPlants, int maxSlots) {
        this.bannedPlants = bannedPlants;
        this.maxSlots = maxSlots;
    }

    public List<String> getBannedPlants() {
        return bannedPlants;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return false;
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        return false;
    }

    @Override
    public void onTick(MatchState state) {
    }

    @Override
    public String getRuleInfo() {
        return "Locked Plants level: " + String.join(", ", bannedPlants)
                + " cannot be used and only " + maxSlots + " seed slots are available.";
    }
}
