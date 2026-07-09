package controllers;

import engine.GameEngine;
import models.MatchState;
import models.Plant;
import models.PlantFactory;
import models.SeedPacket;
import models.Tile;
import models.Zombie;
import models.Enums.PlantTag;
import models.Enums.TileType;
import utils.Result;

// In-match command handler. Parses gameplay commands, delegates simulation to
// the GameEngine, and renders textual output. Grown one doc section at a time.
public class GameplayController {
    private final GameEngine engine;

    public GameplayController(GameEngine engine) {
        this.engine = engine;
    }

    public GameEngine getEngine() {
        return engine;
    }

    // advance time -t <count> ticks
    public Result handleAdvanceTime(int ticks) {
        if (ticks < 1) {
            return new Result("Error: Tick count must be at least 1.", false);
        }
        engine.advanceTicks(ticks);
        return new Result("Advanced " + ticks + " tick(s). Current tick: "
                + engine.getState().getCurrentTick() + ".", true);
    }

    // show sun amount
    public Result handleShowSunAmount() {
        return new Result("Sun: " + engine.getState().getSunAmount(), true);
    }

    // collect sun -l (<x>, <y>)
    public Result handleCollectSun(int x, int y) {
        MatchState state = engine.getState();
        if (x < 1 || x > state.getMap().getColumns() || y < 1 || y > state.getMap().getRows()) {
            return new Result("Error: Coordinates are outside the field.", false);
        }
        int collected = engine.collectSunAt(x, y);
        if (collected < 0) {
            return new Result("Error: There is no sun to collect at (" + x + ", " + y + ").", false);
        }
        if (collected == 0) {
            return new Result("The radioactive sun exploded on collection.", true);
        }
        return new Result("Collected " + collected + " sun. Total: " + state.getSunAmount() + ".", true);
    }

    // cheat add -n <count> suns
    public Result handleCheatAddSuns(int count) {
        if (count < 1) {
            return new Result("Error: Count must be at least 1.", false);
        }
        engine.getState().addSun(count);
        return new Result("Cheat: +" + count + " sun. Total: " + engine.getState().getSunAmount() + ".", true);
    }

    // plant plant -t <type> -l (<x>, <y>)
    public Result handlePlantPlant(String type, int x, int y) {
        MatchState state = engine.getState();
        if (!inBounds(x, y)) {
            return new Result("Error: Coordinates are outside the field.", false);
        }
        SeedPacket packet = findPacket(type);
        if (packet == null) {
            return new Result("Error: '" + type + "' is not one of your selected plants.", false);
        }
        if (!packet.isReadyToPlant()) {
            return new Result("Error: " + packet.getPlantName() + " is recharging ("
                    + formatSeconds(packet.getCurrentCooldown()) + "s left).", false);
        }
        Tile tile = state.getMap().getTile(y - 1, x - 1);
        if (!tile.isPlantable()) {
            return new Result("Error: You can't plant on this tile.", false);
        }
        if (!tile.getPlants().isEmpty()) {
            return new Result("Error: Tile (" + x + ", " + y + ") is already occupied.", false);
        }
        if (state.getSunAmount() < packet.getSunCost()) {
            return new Result("Error: Not enough sun. " + packet.getPlantName() + " costs "
                    + packet.getSunCost() + " (you have " + state.getSunAmount() + ").", false);
        }
        Plant plant = PlantFactory.create(packet.getPlantName());
        if (plant == null) {
            return new Result("Error: '" + packet.getPlantName() + "' cannot be planted.", false);
        }
        state.setSunAmount(state.getSunAmount() - packet.getSunCost());
        state.getMap().addPlant(plant, y - 1, x - 1);
        packet.startCooldown();
        // A boosted seed packet fires its plant-food effect the moment it is planted.
        if (packet.isBoosted()) {
            plant.triggerPlantFood(state);
        }
        return new Result("Planted " + plant.getName() + " at (" + x + ", " + y + ").", true);
    }

    // pluck plant -l (<x>, <y>)
    public Result handlePluckPlant(int x, int y) {
        MatchState state = engine.getState();
        if (!inBounds(x, y)) {
            return new Result("Error: Coordinates are outside the field.", false);
        }
        Tile tile = state.getMap().getTile(y - 1, x - 1);
        if (tile.getPlants().isEmpty()) {
            return new Result("Error: There is no plant at (" + x + ", " + y + ").", false);
        }
        Plant plant = tile.getTopPlant();
        state.getMap().removePlant(plant);
        return new Result("Plucked " + plant.getName() + " from (" + x + ", " + y + ").", true);
    }

    // feed plant -l (<x>, <y>)
    public Result handleFeedPlant(int x, int y) {
        MatchState state = engine.getState();
        if (!inBounds(x, y)) {
            return new Result("Error: Coordinates are outside the field.", false);
        }
        Tile tile = state.getMap().getTile(y - 1, x - 1);
        if (tile.getPlants().isEmpty()) {
            return new Result("Error: There is no plant at (" + x + ", " + y + ").", false);
        }
        if (state.getPlantFoods() <= 0) {
            return new Result("Error: You have no plant food.", false);
        }
        Plant plant = tile.getTopPlant();
        state.consumePlantFood();
        plant.triggerPlantFood(state);
        return new Result("Fed plant food to " + plant.getName() + " at (" + x + ", " + y + ").", true);
    }

    // show plants status
    public Result handleShowPlantsStatus() {
        MatchState state = engine.getState();
        if (state.getSeedPackets().isEmpty()) {
            return new Result("No plants selected for this level.", true);
        }
        StringBuilder sb = new StringBuilder("Plant status:\n");
        for (SeedPacket packet : state.getSeedPackets()) {
            sb.append("- ").append(packet.getPlantName())
                    .append(" | cost ").append(packet.getSunCost()).append(" sun | ");
            if (!packet.isReadyToPlant()) {
                sb.append("recharging (").append(formatSeconds(packet.getCurrentCooldown())).append("s)");
            } else if (state.getSunAmount() < packet.getSunCost()) {
                sb.append("ready, but not enough sun");
            } else {
                sb.append("ready");
            }
            if (packet.isBoosted()) {
                sb.append(" [boosted]");
            }
            sb.append("\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    // show tile status -l (<x>, <y>)
    public Result handleShowTileStatus(int x, int y) {
        MatchState state = engine.getState();
        if (!inBounds(x, y)) {
            return new Result("Error: Coordinates are outside the field.", false);
        }
        Tile tile = state.getMap().getTile(y - 1, x - 1);
        StringBuilder sb = new StringBuilder("Tile (" + x + ", " + y + ") - " + tile.getType() + ":\n");
        if (tile.getPlants().isEmpty() && tile.getZombies().isEmpty()) {
            sb.append("  empty");
            return new Result(sb.toString(), true);
        }
        for (Plant plant : tile.getPlants()) {
            sb.append("  Plant ").append(plant.getName())
                    .append(" | hp ").append(plant.getHealth()).append("/").append(plant.getMaxHp())
                    .append(" | lvl ").append(plant.getLevel())
                    .append(" | tags ").append(tagList(plant)).append("\n");
        }
        for (Zombie zombie : tile.getZombies()) {
            sb.append("  Zombie ").append(zombie.getTypeName())
                    .append(" | hp ").append(zombie.getCurrentHealth()).append("\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    // cheat remove-cooldown
    public Result handleCheatRemoveCooldown() {
        for (SeedPacket packet : engine.getState().getSeedPackets()) {
            packet.setCurrentCooldown(0);
        }
        return new Result("Cheat: all plant cooldowns cleared.", true);
    }

    private SeedPacket findPacket(String plantName) {
        for (SeedPacket packet : engine.getState().getSeedPackets()) {
            if (packet.getPlantName().equalsIgnoreCase(plantName)) {
                return packet;
            }
        }
        return null;
    }

    private boolean inBounds(int x, int y) {
        return x >= 1 && x <= engine.getState().getMap().getColumns()
                && y >= 1 && y <= engine.getState().getMap().getRows();
    }

    private String tagList(Plant plant) {
        if (plant.getTags() == null || plant.getTags().isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (PlantTag tag : plant.getTags()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(tag.name().toLowerCase());
        }
        return sb.toString();
    }

    private String formatSeconds(double seconds) {
        return String.format("%.1f", Math.max(0, seconds));
    }

    // show map
    public Result handleShowMap() {
        MatchState state = engine.getState();
        int rows = state.getMap().getRows();
        int cols = state.getMap().getColumns();

        StringBuilder sb = new StringBuilder();
        sb.append("Wave: ").append(state.getCurrentWave() == null ? "-" : state.getWaves().indexOf(state.getCurrentWave()) + 1)
                .append(" | Tick: ").append(state.getCurrentTick())
                .append(" | Sun: ").append(state.getSunAmount())
                .append(" | Plant food: ").append(state.getPlantFoods())
                .append("\n");

        for (int row = 0; row < rows; row++) {
            sb.append(state.getMap().hasLawnMower(row) ? "[M] " : "[ ] ");
            for (int col = 0; col < cols; col++) {
                sb.append(cell(state.getMap().getTile(row, col), col, row)).append(" ");
            }
            sb.append("\n");
        }

        sb.append("Legend: P=plant Z=zombie o=falling sun O=sun on ground .=normal ~=water #=grave *=ice "
                + "/=slide-up \\=slide-down _=shallow n=necromancy [M]=mower ready\n");
        sb.append(sunSummary());
        return new Result(sb.toString().trim(), true);
    }

    private String cell(Tile tile, int col, int row) {
        if (tile == null) {
            return "?";
        }
        if (!tile.getZombies().isEmpty()) {
            return "Z";
        }
        if (!tile.getPlants().isEmpty()) {
            return "P";
        }
        for (GameEngine.FallingSun fs : engine.getFallingSuns()) {
            if (fs.getCol() == col && fs.getRow() == row) {
                return fs.isLanded() ? "O" : "o";
            }
        }
        return tileChar(tile.getType());
    }

    private String tileChar(TileType type) {
        switch (type) {
            case WATER: return "~";
            case GRAVE: return "#";
            case ICE: return "*";
            case SLIDERUP: return "/";
            case SLIDERDOWN: return "\\";
            case SHALLOW: return "_";
            case NECROMANCY: return "n";
            default: return ".";
        }
    }

    private String sunSummary() {
        if (engine.getFallingSuns().isEmpty()) {
            return "Suns on field: none";
        }
        StringBuilder sb = new StringBuilder("Suns on field: ");
        boolean first = true;
        for (GameEngine.FallingSun fs : engine.getFallingSuns()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(fs.getSun().getType().name().toLowerCase())
                    .append(fs.isLanded() ? "(ground)" : "(falling)")
                    .append("@(").append(fs.getCol() + 1).append(",").append(fs.getRow() + 1).append(")");
            first = false;
        }
        return sb.toString();
    }
}
