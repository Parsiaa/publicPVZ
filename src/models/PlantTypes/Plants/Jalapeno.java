package models.PlantTypes.Plants;

import java.util.ArrayList;
import models.MatchState;
import models.Tile;
import models.Zombie;
import models.Enums.DamageType;
import models.PlantTypes.Explosive;

/** Instantly burns every zombie in its lane and melts any ice in that lane. */
public class Jalapeno extends Explosive {
    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        int row = (int) y;
        for (Zombie zombie : new ArrayList<>(state.getMap().getZombiesInRow(row))) {
            if (isEnemy(zombie)) {
                zombie.takeDamage(baseDamage, DamageType.FIRE);
            }
        }
        for (int c = 0; c < state.getMap().getColumns(); c++) {
            Tile tile = state.getMap().getTile(row, c);
            if (tile != null) {
                tile.meltIce();
            }
        }
        finish(state);
    }

    @Override
    public void triggerPlantFood(MatchState state) {
        act(state);
    }
}
