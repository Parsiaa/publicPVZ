package models.PlantTypes.Plants;

import models.MatchState;
import models.Zombie;
import models.Enums.DamageType;
import models.PlantTypes.WallNut;

/** A spiked wall that reflects damage back at any zombie that bites it. */
public class Endurian extends WallNut {
    @Override
    public void onBittenBy(Zombie zombie, MatchState state) {
        zombie.takeDamage(baseDamage, DamageType.NORMAL);
    }
}
