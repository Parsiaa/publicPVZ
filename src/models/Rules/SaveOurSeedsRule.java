package models.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.LevelRule;
import models.MatchState;
import models.Plant;
import utils.PlantFactory;

/**
 * Save Our Seeds level: protected plants are pre-placed on the map;
 * if any of them dies the level is immediately lost.
 */
public class SaveOurSeedsRule implements LevelRule {

    private final List<Plant> protectedPlants = new ArrayList<>();
    private final Random random;

    public SaveOurSeedsRule(Random random) {
        this.random = random;
    }

    @Override
    public void onMatchStart(MatchState state) {
        List<Integer> usedRows = new ArrayList<>();
        while (protectedPlants.size() < 3 && usedRows.size() < state.getMap().getRows()) {
            int row = random.nextInt(state.getMap().getRows());
            if (usedRows.contains(row)) {
                continue;
            }
            usedRows.add(row);
            Plant plant = PlantFactory.createPlant("Wall-nut");
            if (plant == null) {
                return;
            }
            state.getMap().addPlant(plant, row, 4);
            protectedPlants.add(plant);
            System.out.println("Protect the " + plant.getName() + " at (5, " + (row + 1)
                    + ") - if it dies, you lose!");
        }
    }

    public List<Plant> getProtectedPlants() {
        return protectedPlants;
    }

    @Override
    public boolean checkWinCondition(MatchState state) {
        return false;
    }

    @Override
    public boolean checkLossCondition(MatchState state) {
        for (Plant plant : protectedPlants) {
            if (plant.isDead()) {
                System.out.println("A protected plant was lost!");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTick(MatchState state) {
    }

    @Override
    public String getRuleInfo() {
        return "Save Our Seeds level: the pre-placed plants in column 5 must survive the whole level.";
    }
}
