package controllers;

import models.MatchState;
import models.SeedPacket;
import models.Zombie;
import utils.Result;
import utils.ZombieFactory;

/**
 * The in-match cheat commands. Kept apart from the engine so the
 * engine stays focused on the real game flow.
 */
public class MatchCheatController {

    private final GameEngine engine;

    public MatchCheatController(GameEngine engine) {
        this.engine = engine;
    }

    public Result handleAddSuns(int count) {
        Result guard = engine.requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        if (count < 1) {
            return new Result("Error: Amount must be at least 1.", false);
        }
        engine.getState().addSun(count);
        return new Result("Cheat activated: +" + count + " sun. Sun: "
                + engine.getState().getSunAmount(), true);
    }

    public Result handleSpawnZombie(String type, int x, int y) {
        Result guard = engine.requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        if (engine.tileAt(x, y) == null) {
            return new Result("Error: (" + x + ", " + y + ") is outside the map.", false);
        }
        MatchState state = engine.getState();
        Zombie zombie = ZombieFactory.createZombie(type, state.getDifficultyLevel());
        if (zombie == null) {
            return new Result("Error: Zombie type '" + type + "' doesn't exist.", false);
        }
        state.getMap().addZombie(zombie, y - 1, x - 1);
        return new Result("Cheat activated: " + zombie.getTypeName() + " spawned at (" + x + ", " + y + ").", true);
    }

    public Result handleNuke() {
        Result guard = engine.requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        for (Zombie zombie : engine.getState().getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0) {
                zombie.takeDamage(1000000);
            }
        }
        return new Result("Cheat activated: the nuke killed every zombie on the map.", true);
    }

    public Result handleRemoveCooldowns() {
        Result guard = engine.requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        for (SeedPacket packet : engine.getState().getSeedPackets()) {
            packet.setRechargeTime(0);
            packet.setCurrentCooldown(0);
        }
        return new Result("Cheat activated: all cooldowns removed.", true);
    }

    public Result handleAddPlantFood() {
        Result guard = engine.requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        engine.getState().addPlantFood();
        return new Result("Cheat activated: you have " + engine.getState().getPlantFoods()
                + " plant foods now.", true);
    }
}
