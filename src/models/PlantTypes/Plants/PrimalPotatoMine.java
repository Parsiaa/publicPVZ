package models.PlantTypes.Plants;

/** Faster-arming potato mine with a 3x3 blast. */
public class PrimalPotatoMine extends PotatoMine {
    public PrimalPotatoMine() {
        this.armTimeTicks = 50;
        this.explosionRadius = 1;
    }
}
