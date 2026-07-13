package models.PlantTypes.Plants;

import models.PlantTypes.SunProducer;

/** Produces a big 75-sun from the start. */
public class PrimalSunflower extends SunProducer {
    public PrimalSunflower() {
        this.sunPerCycle = 75;
        this.plantFoodSun = 225;
    }
}
