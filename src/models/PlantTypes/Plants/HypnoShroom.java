package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.PlantTypes.Modifier;

/** When eaten, hypnotises the attacking zombie so it turns and fights for the player. */
public class HypnoShroom extends Modifier {
    @Override
    public void onBittenBy(Zombie zombie, MatchState state) {
        zombie.setHypnotized(true);
        System.out.println(zombie.getTypeName() + " was hypnotised and now fights for you!");
        this.health = 0;
        state.getMap().removePlant(this);
    }
}
