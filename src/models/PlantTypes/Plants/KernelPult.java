package models.PlantTypes.Plants;

import java.util.concurrent.ThreadLocalRandom;
import models.MatchState;
import models.Zombie;
import models.PlantTypes.Lobber;

/** Lobs corn kernels; sometimes lobs butter instead, briefly immobilizing the target. */
public class KernelPult extends Lobber {
    private static final double BUTTER_CHANCE = 0.25;

    @Override
    protected void lob(MatchState state, Zombie target, int damage) {
        strike(target, damage);
        if (ThreadLocalRandom.current().nextDouble() < BUTTER_CHANCE) {
            target.applyTimedEffect("buttered", Zombie.BUTTER_SECONDS, 0);
            System.out.println("A butter stuck " + target.getTypeName() + " at ("
                    + String.format("%.1f", target.getX()) + ", " + ((int) target.getY() + 1) + ")!");
        }
    }
}
