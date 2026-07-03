package models.Zombies;

import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;

public class GargantuarZombie extends Zombie {
    private boolean hasThrowingImp = true;

    @Override
    public void move(MatchState state) {
        if (currentHealth <= 0) {
            return;
        }
        Plant front = state.getMap().getFrontPlantForZombie((int) y, x);
        if (front != null) {
            smash(front);
            state.getMap().removePlant(front);
            state.incrementLostPlantsCount();
            return;
        }
        this.x -= getEffectiveSpeed() * 0.1;
        state.getMap().moveZombieToTile(this);
    }

    public void takeDamage(int amount, DamageType damageType, MatchState state) {
        super.takeDamage(amount, damageType);
        int maxHitpoints = data != null ? data.getHitpoints() : 3000;
        if (hasThrowingImp && currentHealth > 0 && currentHealth <= maxHitpoints / 2) {
            throwImp(state);
        }
    }

    public void smash(Plant plant) {
        plant.setHealth(0);
        System.out.println("Plant " + plant.getName() + " at (" + (int) plant.getX() + ", "
                + (int) plant.getY() + ") is destroyed.");
    }

    public void throwImp(MatchState state) {
        if (!hasThrowingImp) {
            return;
        }
        hasThrowingImp = false;
        BasicZombie imp = new BasicZombie();
        imp.setCurrentHealth(270);
        state.getMap().addZombie(imp, (int) y, 2);
        System.out.println("The Gargantuar threw its Imp to column 3 of row " + (int) y + "!");
    }

    public boolean hasThrowingImp() { return hasThrowingImp; }
    public void setHasThrowingImp(boolean hasThrowingImp) { this.hasThrowingImp = hasThrowingImp; }
}
