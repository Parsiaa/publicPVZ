package models.PlantTypes.Plants;

import models.PlantTypes.Modifier;

/** Passively turns any pea passing over it into a fiery pea (double damage, melts ice). */
public class Torchwood extends Modifier {
    @Override
    public boolean ignitesPeas() {
        return true;
    }
}
