package models.PlantTypes.Plants;

import models.MatchState;
import models.Plant;
import models.Tile;
import models.PlantTypes.Shooter;

/** Fires one pea per stacked head on its tile (up to five heads). */
public class PeaPod extends Shooter {
    @Override
    protected boolean fire(MatchState state) {
        Tile tile = state.getMap().getTile((int) y, (int) x);
        int heads = 0;
        if (tile != null) {
            for (Plant plant : tile.getPlants()) {
                if (plant instanceof PeaPod) {
                    heads++;
                }
            }
        }
        heads = Math.max(1, Math.min(5, heads));
        return fireInRow(state, (int) y, x, baseDamage * heads);
    }
}
