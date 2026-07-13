package models.PlantTypes.Plants;

import models.MatchState;
import models.Tile;
import models.PlantTypes.Explosive;

/** Detonates instantly across the whole board and leaves an unplantable crater. */
public class DoomShroom extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        int reach = Math.max(state.getMap().getRows(), state.getMap().getColumns());
        damageArea(state, x, y, reach, baseDamage);
        Tile tile = state.getMap().getTile((int) y, (int) x);
        if (tile != null) {
            tile.setHasCrater(true);
        }
        finish(state);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
