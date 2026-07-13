package models.PlantTypes.Plants;

import models.MatchState;
import models.PlantTypes.Shooter;

/** Fires straight shots in its own lane and the two adjacent lanes at once. */
public class Threepeater extends Shooter {
    @Override
    protected boolean fire(MatchState state) {
        boolean fired = false;
        for (int row = (int) y - 1; row <= (int) y + 1; row++) {
            if (row >= 0 && row < state.getMap().getRows()) {
                fired |= fireInRow(state, row, x, baseDamage);
            }
        }
        return fired;
    }
}
