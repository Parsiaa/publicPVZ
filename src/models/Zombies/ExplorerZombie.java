package models.Zombies;

import java.util.ArrayList;
import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;

public class ExplorerZombie extends Zombie {
    private boolean isTorchLit = true;

    public void act(MatchState state) {
        if (currentHealth <= 0 || !isTorchLit) {
            return;
        }
        for (Plant plant : new ArrayList<>(state.getMap().getPlantsInRow((int) y))) {
            double distance = x - plant.getX();
            if (distance >= 0 && distance < 1 && !plant.isDead()) {
                plant.setHealth(0);
                state.getMap().removePlant(plant);
                state.incrementLostPlantsCount();
                System.out.println("Plant " + plant.getName() + " at (" + (int) plant.getX() + ", "
                        + (int) plant.getY() + ") is destroyed.");
            }
        }
    }

    @Override
    public void takeDamage(int amount, DamageType type) {
        if (type == DamageType.ICE) {
            this.isTorchLit = false;
        } else if (type == DamageType.FIRE) {
            this.isTorchLit = true;
        }
        super.takeDamage(amount, type);
    }

    public boolean isTorchLit() { return isTorchLit; }
    public void setTorchLit(boolean torchLit) { isTorchLit = torchLit; }
}
