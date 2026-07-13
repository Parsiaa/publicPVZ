package models.PlantTypes.Plants;

import java.util.ArrayList;
import models.MatchState;
import models.Zombie;
import models.Enums.DamageType;
import models.PlantTypes.WallNut;

/** A defensive nut that bursts in a 3x3 explosion the moment its health runs out. */
public class ExplodeONut extends WallNut {
    @Override
    public void onDeath(MatchState state) {
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (zombie.getCurrentHealth() > 0 && !zombie.isHypnotized()
                    && Math.abs(zombie.getX() - x) <= 1 && Math.abs(zombie.getY() - y) <= 1) {
                zombie.takeDamage(baseDamage, DamageType.FIRE);
            }
        }
        System.out.println("Plant " + name + " exploded at (" + (int) x + ", " + (int) y + ")!");
    }
}
