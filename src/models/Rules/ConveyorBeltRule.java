package models.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.LevelRule;
import models.MatchState;

/**
 * Conveyor Belt level: no plant selection and no sun cost; the belt delivers
 * a random plant every 12 seconds and the player plants straight from it.
 */
public class ConveyorBeltRule implements LevelRule {

    private static final int DELIVERY_INTERVAL_TICKS = 120;
    private static final int MAX_BELT_SIZE = 5;

    private final List<String> plantPool;
    private final List<String> belt = new ArrayList<>();
    private final Random random;

    public ConveyorBeltRule(List<String> plantPool, Random random) {
        this.plantPool = plantPool;
        this.random = random;
    }

    @Override
    public void onMatchStart(MatchState state) {
        deliverPlant();
    }

    @Override
    public void onTick(MatchState state) {
        if (state.getCurrentTick() % DELIVERY_INTERVAL_TICKS == 0) {
            deliverPlant();
        }
    }

    private void deliverPlant() {
        if (plantPool.isEmpty() || belt.size() >= MAX_BELT_SIZE) {
            return;
        }
        String plant = plantPool.get(random.nextInt(plantPool.size()));
        belt.add(plant);
        System.out.println("The conveyor belt delivered a " + plant + "! Belt: " + belt);
    }

    /** Removes one plant of this type from the belt; returns false if it is not there. */
    public boolean takeFromBelt(String plantName) {
        for (String item : belt) {
            if (item.equalsIgnoreCase(plantName)) {
                belt.remove(item);
                return true;
            }
        }
        return false;
    }

    public List<String> getBelt() {
        return belt;
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
    public String getRuleInfo() {
        return "Conveyor Belt level: plants arrive on the belt every 12 seconds and cost no sun.";
    }
}
