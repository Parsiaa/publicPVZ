package models.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.LevelRule;
import models.MatchState;
import models.PlantTypes.BowlingPlant;

/**
 * Wallnut Bowling mini-game: nuts arrive on a conveyor and can only be planted
 * left of the red line; once planted they roll towards the zombies.
 */
public class BowlingRule implements LevelRule {

    private static final int DELIVERY_INTERVAL_TICKS = 80;
    private static final int MAX_BELT_SIZE = 4;
    public static final int RED_LINE_COLUMN = 3;

    private final int stage;
    private final Random random;
    private final List<String> belt = new ArrayList<>();

    public BowlingRule(int stage, Random random) {
        this.stage = stage;
        this.random = random;
    }

    @Override
    public void onMatchStart(MatchState state) {
        deliverNut();
    }

    @Override
    public void onTick(MatchState state) {
        if (state.getCurrentTick() % DELIVERY_INTERVAL_TICKS == 0) {
            deliverNut();
        }
    }

    private void deliverNut() {
        if (belt.size() >= MAX_BELT_SIZE) {
            return;
        }
        double roll = random.nextDouble();
        String nut = "Bowling Wall-nut";
        if (roll < 0.2) {
            nut = "Explode-o-nut";
        } else if (roll < 0.3) {
            nut = "Giant Wall-nut";
        }
        belt.add(nut);
        System.out.println("The conveyor belt delivered a " + nut + "! Belt: " + belt);
    }

    public boolean takeFromBelt(String nutName) {
        for (String item : belt) {
            if (item.equalsIgnoreCase(nutName)) {
                belt.remove(item);
                return true;
            }
        }
        return false;
    }

    /** Builds the rolling nut for a belt entry. */
    public BowlingPlant createNut(String nutName) {
        BowlingPlant nut = new BowlingPlant();
        nut.setName(nutName);
        nut.setMaxHp(4000);
        nut.setHealth(4000);
        nut.setCost(0);
        nut.setLevel(1);
        if (nutName.equalsIgnoreCase("Explode-o-nut")) {
            nut.setExplosive(true);
            nut.setBaseDamage(1800);
        } else if (nutName.equalsIgnoreCase("Giant Wall-nut")) {
            nut.setGiant(true);
            nut.setBaseDamage(999999);
        } else {
            nut.setBaseDamage(190);
        }
        return nut;
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
        return "Wallnut Bowling (stage " + stage + "): plant rolling nuts in columns 1-"
                + RED_LINE_COLUMN + " and bowl the zombies over.";
    }
}
