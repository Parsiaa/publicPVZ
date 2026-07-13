package models.PlantTypes.Plants;

import models.Armor;
import models.MatchState;
import models.Zombie;
import models.PlantTypes.Modifier;

/** Periodically pulls a metal item (bucket, crown, ...) off the nearest armoured zombie. */
public class MagnetShroom extends Modifier {
    private int ticksSinceAction;

    @Override
    public void act(MatchState state) {
        if (isDead() || isFrozen()) {
            return;
        }
        ticksSinceAction++;
        int intervalTicks = (int) Math.max(1, actionInterval * 10);
        if (ticksSinceAction < intervalTicks) {
            return;
        }
        Zombie target = nearestMetalZombie(state);
        if (target == null) {
            return;
        }
        ticksSinceAction = 0;
        for (Armor armor : target.getArmors()) {
            if (!armor.isBroken() && armor.isMetallic()) {
                armor.setCurrentHealth(0);
                System.out.println("Magnet-shroom pulled the " + armor.getData().getArmorType()
                        + " off a " + target.getTypeName() + "!");
                return;
            }
        }
    }

    private Zombie nearestMetalZombie(MatchState state) {
        Zombie best = null;
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() <= 0 || zombie.isHypnotized() || !hasMetal(zombie)) {
                continue;
            }
            if (best == null || Math.abs(zombie.getX() - x) < Math.abs(best.getX() - x)) {
                best = zombie;
            }
        }
        return best;
    }

    private boolean hasMetal(Zombie zombie) {
        for (Armor armor : zombie.getArmors()) {
            if (!armor.isBroken() && armor.isMetallic()) {
                return true;
            }
        }
        return false;
    }
}
