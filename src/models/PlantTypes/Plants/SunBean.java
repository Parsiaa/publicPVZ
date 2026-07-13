package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.WallNut;

/** A defensive bean that yields sun each time a zombie bites it. */
public class SunBean extends WallNut {
    private static final int SUN_PER_BITE = 5;

    @Override
    public void onBittenBy(Zombie zombie, MatchState state) {
        state.addSun(SUN_PER_BITE);
    }
}
