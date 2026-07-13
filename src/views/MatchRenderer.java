package views;

import models.MatchState;
import models.Plant;
import models.SeedPacket;
import models.Tile;
import models.Zombie;
import models.Enums.TileType;

/**
 * Builds the textual representations of the match used by the in-game "show" commands.
 */
public final class MatchRenderer {

    private MatchRenderer() {
    }

    public static String renderMap(MatchState state, int currentWaveNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("Wave: ").append(currentWaveNumber).append("/").append(state.getWaves().size())
                .append(" | Sun: ").append(state.getSunAmount())
                .append(" | Plant foods: ").append(state.getPlantFoods())
                .append(" | Tick: ").append(state.getCurrentTick()).append("\n");
        for (int r = 0; r < state.getMap().getRows(); r++) {
            sb.append("Row ").append(r + 1)
                    .append(state.getMap().hasLawnMower(r) ? " [M]: " : " [ ]: ");
            for (int c = 0; c < state.getMap().getColumns(); c++) {
                sb.append(cellString(state.getMap().getTile(r, c)));
            }
            sb.append("\n");
        }
        sb.append("Legend: [terrain plants zombies] | terrain: .=normal ~=water I=ice G=grave ^=slide-up ")
                .append("v=slide-down S=shallow N=necromancy X=crater | [M]=lawn mower available");
        return sb.toString();
    }

    private static String cellString(Tile tile) {
        String plantPart = tile.getPlants().isEmpty() ? "--" : "P" + tile.getPlants().size();
        String zombiePart = tile.getZombies().isEmpty() ? "--" : "Z" + tile.getZombies().size();
        return "[" + terrainChar(tile) + " " + plantPart + " " + zombiePart + "]";
    }

    private static char terrainChar(Tile tile) {
        if (tile.hasCrater()) {
            return 'X';
        }
        switch (tile.getType()) {
            case WATER: return '~';
            case ICE: return 'I';
            case GRAVE: return 'G';
            case SLIDERUP: return '^';
            case SLIDERDOWN: return 'v';
            case SHALLOW: return 'S';
            case NECROMANCY: return 'N';
            default: return '.';
        }
    }

    public static String renderPlantsStatus(MatchState state) {
        if (state.getSeedPackets().isEmpty()) {
            return "You have no plants selected.";
        }
        StringBuilder sb = new StringBuilder("Plants status:");
        for (SeedPacket packet : state.getSeedPackets()) {
            sb.append("\n- ").append(packet.getPlantName())
                    .append(" (cost: ").append(packet.getSunCost()).append("): ");
            if (packet.isReadyToPlant()) {
                sb.append("ready");
            } else {
                sb.append("ready in ").append(String.format("%.1f", packet.getCurrentCooldown())).append(" seconds");
            }
        }
        return sb.toString();
    }

    public static String renderTileStatus(Tile tile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tile (").append(tile.getColumn() + 1).append(", ").append(tile.getRow() + 1).append("):");
        sb.append("\n  terrain: ").append(tile.getType().name().toLowerCase());
        if (tile.getType() == TileType.GRAVE || tile.getType() == TileType.ICE) {
            sb.append(" (health: ").append(tile.getTileHealth()).append(")");
        }
        if (tile.hasCrater()) {
            sb.append(" (crater)");
        }
        if (tile.getPlants().isEmpty() && tile.getZombies().isEmpty()) {
            sb.append("\n  empty");
            return sb.toString();
        }
        for (Plant plant : tile.getPlants()) {
            sb.append("\n  plant: ").append(plant.getName())
                    .append(" | health: ").append(plant.getHealth()).append("/").append(plant.getMaxHp())
                    .append(" | level: ").append(plant.getLevel());
            if (plant.isFrozen()) {
                sb.append(" | frozen");
            }
        }
        for (Zombie zombie : tile.getZombies()) {
            sb.append("\n").append(indent(zombie.getInfo()));
        }
        return sb.toString();
    }

    public static String renderZombiesInfo(MatchState state) {
        StringBuilder sb = new StringBuilder();
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(zombie.getInfo());
            }
        }
        if (sb.length() == 0) {
            return "There are no zombies on the map.";
        }
        return sb.toString();
    }

    private static String indent(String text) {
        return "  " + text.replace("\n", "\n  ");
    }
}
