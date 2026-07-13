package models.PlantTypes.Plants;

import java.util.ArrayList;
import models.MatchState;
import models.Zombie;
import models.PlantTypes.WallNut;

/** Attracts zombies from the adjacent lanes into its own lane. */
public class SweetPotato extends WallNut {
    @Override
    public void act(MatchState state) {
        for (int row = (int) y - 1; row <= (int) y + 1; row += 2) {
            if (row < 0 || row >= state.getMap().getRows()) {
                continue;
            }
            for (Zombie zombie : new ArrayList<>(state.getMap().getZombiesInRow(row))) {
                if (zombie.getCurrentHealth() > 0 && !zombie.isHypnotized() && Math.abs(zombie.getX() - x) <= 1.5) {
                    zombie.setY((int) y);
                    state.getMap().moveZombieToTile(zombie);
                }
            }
        }
    }
}
