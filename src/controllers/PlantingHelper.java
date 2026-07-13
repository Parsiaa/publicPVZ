package controllers;

import models.MatchState;
import models.Plant;
import models.SeedPacket;
import models.Tile;
import models.Enums.PlantTag;
import models.Enums.TileType;
import utils.Result;

/**
 * Placement rules shared by every way of planting: tile validation
 * (including water and stacking), collection levels and stored boosts.
 */
public final class PlantingHelper {

    private PlantingHelper() {
    }

    /** Validates the tile and puts the plant on the map; returns an error Result or null on success. */
    public static Result placePlant(MatchState state, Plant plant, int x, int y) {
        Tile tile = state.getMap().getTile(y - 1, x - 1);
        if (tile == null) {
            return new Result("Error: (" + x + ", " + y + ") is outside the map.", false);
        }
        if (!tile.isPlantable() && !isWaterPlantable(tile, plant)) {
            return new Result("Error: You cannot plant on this tile.", false);
        }
        if (!tile.getPlants().isEmpty() && !canStack(tile, plant)) {
            return new Result("Error: There is already a plant at (" + x + ", " + y + ").", false);
        }
        applyCollectionLevel(state, plant);
        state.getMap().addPlant(plant, y - 1, x - 1);
        return null;
    }

    private static boolean isWaterPlantable(Tile tile, Plant plant) {
        if (tile.getType() != TileType.WATER || tile.hasCrater()) {
            return false;
        }
        return plant.hasTag(PlantTag.WATER) || !tile.getPlants().isEmpty();
    }

    private static boolean canStack(Tile tile, Plant newPlant) {
        Plant top = tile.getTopPlant();
        return (top != null && top.hasTag(PlantTag.STACK)) || newPlant.hasTag(PlantTag.STACK);
    }

    private static void applyCollectionLevel(MatchState state, Plant plant) {
        if (state.getUser() == null) {
            return;
        }
        int level = state.getUser().getPlantLevel(plant.getName());
        for (int i = 1; i < level; i++) {
            plant.upgrade();
        }
    }

    /** Boosted packets trigger the plant food effect the moment the plant is placed. */
    public static void applyBoostIfAny(MatchState state, SeedPacket packet, Plant plant) {
        if (!packet.isBoosted() && !packet.isHasStoredBoost()) {
            return;
        }
        plant.triggerPlantFood(state);
        if (packet.isHasStoredBoost()) {
            packet.setHasStoredBoost(false);
            if (state.getUser() != null) {
                state.getUser().consumeBoostFor(packet.getPlantName());
            }
        }
    }
}
