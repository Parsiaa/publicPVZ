package models.PlantTypes.Plants;

import models.PlantTypes.WallNut;

/** A tall wall that also blocks zombies which would jump or fly over shorter obstacles. */
public class TallNut extends WallNut {
    @Override
    public boolean blocksFlying() {
        return true;
    }
}
