package controllers;

import models.MatchState;
import models.Plant;
import models.Rules.BowlingRule;
import models.Rules.ConveyorBeltRule;
import models.Rules.IZombieRule;
import models.Rules.VasebreakerRule;
import models.PlantTypes.BowlingPlant;
import utils.PlantFactory;
import utils.Result;

/**
 * The in-match commands that only exist in special levels and mini-games:
 * planting from a conveyor belt, rolling bowling nuts, breaking vases
 * and placing zombies in I, Zombie.
 */
public class MiniGameActions {

    private final GameEngine engine;

    public MiniGameActions(GameEngine engine) {
        this.engine = engine;
    }

    public Result plantFromConveyor(ConveyorBeltRule conveyor, String type, int x, int y) {
        MatchState state = engine.getState();
        if (!conveyor.takeFromBelt(type)) {
            return new Result("Error: There is no " + type + " on the conveyor belt. Belt: "
                    + conveyor.getBelt(), false);
        }
        Plant plant = PlantFactory.createPlant(type);
        if (plant == null) {
            return new Result("Error: Plant '" + type + "' doesn't exist.", false);
        }
        plant.setCost(0);
        Result placement = engine.placePlant(plant, x, y);
        if (placement != null) {
            conveyor.getBelt().add(plant.getName());
            return placement;
        }
        return new Result(plant.getName() + " planted at (" + x + ", " + y + ") from the belt.", true);
    }

    public Result plantBowlingNut(BowlingRule bowling, String type, int x, int y) {
        if (x > BowlingRule.RED_LINE_COLUMN) {
            return new Result("Error: Bowling nuts can only be planted in columns 1-"
                    + BowlingRule.RED_LINE_COLUMN + ".", false);
        }
        if (!bowling.takeFromBelt(type)) {
            return new Result("Error: There is no " + type + " on the conveyor belt. Belt: "
                    + bowling.getBelt(), false);
        }
        BowlingPlant nut = bowling.createNut(type);
        Result placement = engine.placePlant(nut, x, y);
        if (placement != null) {
            bowling.getBelt().add(nut.getName());
            return placement;
        }
        return new Result(nut.getName() + " starts rolling from (" + x + ", " + y + ")!", true);
    }

    public Result breakVase(int x, int y) {
        VasebreakerRule rule = engine.findRule(VasebreakerRule.class);
        if (rule == null) {
            return new Result("Error: This is not a Vasebreaker level.", false);
        }
        if (engine.tileAt(x, y) == null) {
            return new Result("Error: (" + x + ", " + y + ") is outside the map.", false);
        }
        String message = rule.breakVase(engine.getState(), x - 1, y - 1);
        if (message == null) {
            return new Result("Error: There is no vase at (" + x + ", " + y + ").", false);
        }
        return new Result(message + " Vases left: " + rule.getVases().size(), true);
    }

    public Result placeZombie(String type, int x, int y) {
        IZombieRule rule = engine.findRule(IZombieRule.class);
        if (rule == null) {
            return new Result("Error: This is not an I, Zombie level.", false);
        }
        if (engine.tileAt(x, y) == null) {
            return new Result("Error: (" + x + ", " + y + ") is outside the map.", false);
        }
        String error = rule.placeZombie(engine.getState(), type, x - 1, y - 1);
        if (error != null) {
            return new Result(error, false);
        }
        return new Result(type + " placed at (" + x + ", " + y + "). Sun: "
                + engine.getState().getSunAmount(), true);
    }
}
