package models.Zombies;

import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;

public abstract class GargantuarZombie extends Zombie {
    private boolean hasThrowingImp;

    public void takeDamage(int amount, DamageType damageType) {
        //TODO
    }

    public void smash(Plant plant) {
        //TODO
    }

    public void throwImp(MatchState state) {
        //TODO
    }
}
