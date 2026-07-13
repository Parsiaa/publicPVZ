package models.PlantTypes.Plants;

import models.MatchState;
import models.Tile;
import models.Enums.ObstacleType;
import models.Enums.TileType;
import models.PlantTypes.Explosive;

/** Destroys the grave it is planted on (and grants any buried loot), then vanishes. */
public class GraveBuster extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        Tile tile = state.getMap().getTile((int) y, (int) x);
        if (tile != null && tile.getType() == TileType.GRAVE) {
            grantLoot(state, tile);
            tile.setTileHealth(0);
            tile.setType(TileType.NORMAL);
            tile.setGraveLoot(null);
            System.out.println("The grave at (" + ((int) x + 1) + ", " + ((int) y + 1) + ") was busted.");
        }
        this.health = 0;
        state.getMap().removePlant(this);
    }

    private void grantLoot(MatchState state, Tile tile) {
        if (tile.getGraveLoot() == ObstacleType.GRAVE_SUN) {
            state.addSun(50);
            System.out.println("The grave dropped 50 sun!");
        } else if (tile.getGraveLoot() == ObstacleType.GRAVE_FOOD) {
            state.addPlantFood();
            System.out.println("The grave dropped a plant food; you have "
                    + state.getPlantFoods() + " plant foods now.");
        }
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
