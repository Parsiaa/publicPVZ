package models.Zombies;

import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;

public class GargantuarZombie extends Zombie {
    private boolean hasThrowingImp = true;
    private boolean pendingImpThrow;

    @Override
    public void move(MatchState state) {
        if (currentHealth <= 0 || isImmobilized()) {
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

    @Override
    public void takeDamage(int amount, DamageType type) {
        super.takeDamage(amount, type);
        int maxHitpoints = data != null ? data.getHitpoints() : 3600;
        if (hasThrowingImp && currentHealth > 0 && currentHealth <= maxHitpoints / 2) {
            pendingImpThrow = true;
        }
    }

    @Override
    public void onTick(MatchState state) {
        if (pendingImpThrow) {
            pendingImpThrow = false;
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
        Zombie imp = utils.ZombieFactory.createZombie("Imp", state.getDifficultyLevel());
        if (imp == null) {
            imp = new BasicZombie();
            imp.setCurrentHealth(270);
        }
        int column = Math.min(2, state.getMap().getColumns() - 1);
        state.getMap().addZombie(imp, (int) y, column);
        System.out.println("The Gargantuar threw its Imp to column " + (column + 1) + " of row " + ((int) y + 1) + "!");
    }

    public boolean hasThrowingImp() { return hasThrowingImp; }
    public void setHasThrowingImp(boolean hasThrowingImp) { this.hasThrowingImp = hasThrowingImp; }
}
