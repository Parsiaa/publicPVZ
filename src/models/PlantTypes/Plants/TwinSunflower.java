package models.PlantTypes.Plants;

import models.PlantTypes.SunProducer;

/** Produces 100 sun per cycle. */
public class TwinSunflower extends SunProducer {
    public TwinSunflower() {
        this.sunPerCycle = 100;
        this.plantFoodSun = 250;
    }
}
