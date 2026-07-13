package controllers.Chapters;

import java.util.ArrayList;
import java.util.Random;
import controllers.ChapterMechanics;
import models.MatchState;
import models.Plant;
import models.Tile;
import models.Zombie;
import models.Enums.PlantTag;
import models.Enums.TileType;
import utils.ZombieFactory;

/**
 * Big Wave Beach: the rightmost columns are sea. The tide moves with every
 * wave (up to a fixed line), drowning plants that cannot live in water,
 * and shallow tiles under water can spawn zombies from below.
 */
public class BeachChapter extends ChapterMechanics {

    private static final int MAX_WATER_COLUMNS = 3;
    private static final double SHALLOW_SPAWN_CHANCE = 0.3;

    private int currentWaterColumns;

    public BeachChapter(Random random) {
        super(random);
    }

    @Override
    public void onMatchStart(MatchState state) {
        currentWaterColumns = 2;
        placeShallowTiles(state);
        applyWaterLevel(state);
    }

    private void placeShallowTiles(MatchState state) {
        for (int i = 0; i < 2; i++) {
            int row = random.nextInt(state.getMap().getRows());
            int column = state.getMap().getColumns() - MAX_WATER_COLUMNS + random.nextInt(MAX_WATER_COLUMNS);
            Tile tile = state.getMap().getTile(row, column);
            if (tile != null) {
                tile.setType(TileType.SHALLOW);
            }
        }
    }

    @Override
    public void onWaveStart(MatchState state, int waveNumber, boolean finalWave) {
        int newWidth = 1 + random.nextInt(MAX_WATER_COLUMNS);
        if (newWidth != currentWaterColumns) {
            currentWaterColumns = newWidth;
            System.out.println("The tide shifted! Water now covers the last "
                    + currentWaterColumns + " column(s).");
            applyWaterLevel(state);
        }
        spawnFromShallows(state);
    }

    private void applyWaterLevel(MatchState state) {
        int columns = state.getMap().getColumns();
        for (int row = 0; row < state.getMap().getRows(); row++) {
            for (int col = columns - MAX_WATER_COLUMNS; col < columns; col++) {
                Tile tile = state.getMap().getTile(row, col);
                if (tile == null || tile.getType() == TileType.SHALLOW) {
                    continue;
                }
                boolean shouldBeWater = col >= columns - currentWaterColumns;
                if (shouldBeWater && tile.getType() == TileType.NORMAL) {
                    tile.setType(TileType.WATER);
                    drownPlants(state, tile);
                } else if (!shouldBeWater && tile.getType() == TileType.WATER) {
                    tile.setType(TileType.NORMAL);
                }
            }
        }
    }

    private void drownPlants(MatchState state, Tile tile) {
        boolean hasLilyPad = !tile.getPlants().isEmpty() && tile.getPlants().get(0).hasTag(PlantTag.WATER);
        if (hasLilyPad) {
            return;
        }
        for (Plant plant : new ArrayList<>(tile.getPlants())) {
            if (!plant.hasTag(PlantTag.WATER)) {
                System.out.println("Plant " + plant.getName() + " at (" + (tile.getColumn() + 1)
                        + ", " + (tile.getRow() + 1) + ") drowned in the rising water!");
                plant.setHealth(0);
                state.getMap().removePlant(plant);
                state.incrementLostPlantsCount();
            }
        }
    }

    private void spawnFromShallows(MatchState state) {
        int columns = state.getMap().getColumns();
        for (int row = 0; row < state.getMap().getRows(); row++) {
            for (int col = columns - MAX_WATER_COLUMNS; col < columns; col++) {
                Tile tile = state.getMap().getTile(row, col);
                boolean underWater = col >= columns - currentWaterColumns;
                if (tile != null && tile.getType() == TileType.SHALLOW && underWater
                        && random.nextDouble() < SHALLOW_SPAWN_CHANCE) {
                    Zombie zombie = ZombieFactory.createZombie("Normal", state.getDifficultyLevel());
                    state.getMap().addZombie(zombie, row, col);
                    System.out.println("A zombie emerged from the shallow water at ("
                            + (col + 1) + ", " + (row + 1) + ")!");
                }
            }
        }
    }
}
