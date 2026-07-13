package controllers.Chapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
 * Frostbite Caves: icy winds stack frost on plants (3rd level freezes them),
 * slider tiles push zombies to neighbour lanes, frozen zombies wait inside
 * ice tiles, fire plants thaw their neighbourhood, and zombies never chill.
 */
public class FrostbiteChapter extends ChapterMechanics {

    private static final double ICY_WIND_CHANCE = 0.25;
    private static final int FIRE_MELT_INTERVAL_TICKS = 10;

    private final Map<Zombie, Integer> lastColumns = new HashMap<>();

    public FrostbiteChapter(Random random) {
        super(random);
    }

    @Override
    public void onMatchStart(MatchState state) {
        placeSliderTiles(state);
        placeFrozenZombies(state);
    }

    private void placeSliderTiles(MatchState state) {
        for (int i = 0; i < 2; i++) {
            int row = 1 + random.nextInt(state.getMap().getRows() - 2);
            int column = 3 + random.nextInt(state.getMap().getColumns() - 4);
            Tile tile = state.getMap().getTile(row, column);
            if (tile != null && tile.getType() == TileType.NORMAL) {
                tile.setType(random.nextBoolean() ? TileType.SLIDERUP : TileType.SLIDERDOWN);
            }
        }
    }

    private void placeFrozenZombies(MatchState state) {
        for (int i = 0; i < 2; i++) {
            int row = random.nextInt(state.getMap().getRows());
            int column = 4 + random.nextInt(state.getMap().getColumns() - 5);
            Tile tile = state.getMap().getTile(row, column);
            if (tile != null && tile.getType() == TileType.NORMAL) {
                tile.setType(TileType.ICE);
                Zombie zombie = ZombieFactory.createZombie("Normal", state.getDifficultyLevel());
                state.getMap().addZombie(zombie, row, column);
            }
        }
    }

    @Override
    public void onWaveStart(MatchState state, int waveNumber, boolean finalWave) {
        for (int row = 0; row < state.getMap().getRows(); row++) {
            if (random.nextDouble() >= ICY_WIND_CHANCE) {
                continue;
            }
            System.out.println("An icy wind hits row " + (row + 1) + "!");
            for (Plant plant : state.getMap().getPlantsInRow(row)) {
                if (!plant.isDead() && !plant.hasTag(PlantTag.FIRE)) {
                    plant.applyFrost(1);
                    if (plant.isFrozen()) {
                        System.out.println("Plant " + plant.getName() + " at ("
                                + ((int) plant.getX() + 1) + ", " + (row + 1) + ") is frozen solid!");
                    }
                }
            }
        }
    }

    @Override
    public void onTick(MatchState state) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            zombie.removeTimedEffect("chilled");
        }
        handleSliderTiles(state);
        if (state.getCurrentTick() % FIRE_MELT_INTERVAL_TICKS == 0) {
            meltNearFirePlants(state);
        }
    }

    /** Zombies stepping onto a slider tile slip to the neighbouring lane. */
    private void handleSliderTiles(MatchState state) {
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (zombie.getCurrentHealth() <= 0) {
                continue;
            }
            int column = (int) Math.max(0, Math.floor(zombie.getX()));
            Integer previous = lastColumns.put(zombie, column);
            if (previous == null || previous == column) {
                continue;
            }
            Tile tile = state.getMap().getTile((int) zombie.getY(), column);
            if (tile == null) {
                continue;
            }
            int shift = 0;
            if (tile.getType() == TileType.SLIDERUP && zombie.getY() > 0) {
                shift = -1;
            } else if (tile.getType() == TileType.SLIDERDOWN && zombie.getY() < state.getMap().getRows() - 1) {
                shift = 1;
            }
            if (shift != 0) {
                zombie.setY(zombie.getY() + shift);
                state.getMap().moveZombieToTile(zombie);
                System.out.println("Zombie " + zombie.getTypeName() + " slipped to lane "
                        + ((int) zombie.getY() + 1) + "!");
            }
        }
    }

    private void meltNearFirePlants(MatchState state) {
        for (Plant plant : state.getMap().getAllPlants()) {
            if (plant.getFrostLevel() > 0 && hasFireNeighbor(state, plant)) {
                plant.meltFrost(1);
            }
        }
        for (Plant firePlant : state.getMap().getAllPlants()) {
            if (firePlant.hasTag(PlantTag.FIRE)) {
                meltAdjacentIceTiles(state, firePlant);
            }
        }
    }

    private boolean hasFireNeighbor(MatchState state, Plant plant) {
        for (Plant other : state.getMap().getAllPlants()) {
            if (other != plant && other.hasTag(PlantTag.FIRE)
                    && Math.abs(other.getX() - plant.getX()) <= 1
                    && Math.abs(other.getY() - plant.getY()) <= 1) {
                return true;
            }
        }
        return false;
    }

    private void meltAdjacentIceTiles(MatchState state, Plant firePlant) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                Tile tile = state.getMap().getTile((int) firePlant.getY() + dy, (int) firePlant.getX() + dx);
                if (tile != null && tile.getType() == TileType.ICE) {
                    tile.takeTileDamage(6);
                }
            }
        }
    }
}
