package models.Zombies;

import models.MatchState;
import models.Zombie;

public abstract class TombRaiserZombie extends Zombie {
    private double timeSinceLastCast;

    @Override
    public void move(MatchState state) {
        // TODO Auto-generated method stub
        super.move(state);
    }

    public void act(MatchState state) {
        //TODO
    }
}
