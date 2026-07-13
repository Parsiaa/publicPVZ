package models.PlantTypes.Plants;

import models.MatchState;
import models.Tile;
import models.Enums.TileType;
import models.PlantTypes.Explosive;

/** Melts the ice on the tile it is planted on (thawing a frozen plant), then vanishes. */
public class HotPotato extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        Tile tile = state.getMap().getTile((int) y, (int) x);
        if (tile != null && tile.getType() == TileType.ICE) {
            tile.meltIce();
            System.out.println("Hot Potato melted the ice at (" + ((int) x + 1) + ", " + ((int) y + 1) + ").");
        }
        this.health = 0;
        state.getMap().removePlant(this);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
