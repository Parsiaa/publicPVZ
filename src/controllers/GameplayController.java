package controllers;

import engine.GameEngine;
import models.MatchState;
import models.Tile;
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
        int collected = engine.collectSkySunAt(x, y);
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

        sb.append("Legend: .=normal ~=water #=grave *=ice /=slide-up \\=slide-down _=shallow n=necromancy "
                + "o=falling sun O=sun on ground [M]=mower ready\n");
        sb.append(sunSummary());
        return new Result(sb.toString().trim(), true);
    }

    private String cell(Tile tile, int col, int row) {
        for (GameEngine.FallingSun fs : engine.getFallingSuns()) {
            if (fs.getCol() == col && fs.getRow() == row) {
                return fs.isLanded() ? "O" : "o";
            }
        }
        return tile == null ? "?" : tileChar(tile.getType());
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
