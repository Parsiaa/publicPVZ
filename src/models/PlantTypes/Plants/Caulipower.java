package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.Homing;

/** Fires a magic shot that hypnotises its target instead of damaging it. */
public class Caulipower extends Homing {
    @Override
    protected void onHit(MatchState state, Zombie target) {
        target.setHypnotized(true);
        System.out.println(target.getTypeName() + " was hypnotised by Caulipower!");
    }
}
