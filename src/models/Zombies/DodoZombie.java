package models.Zombies;

import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.PlantTag;

public class DodoZombie extends Zombie {
    private boolean isFlying;

    @Override
    public void move(MatchState state) {
        if (currentHealth <= 0) {
            return;
        }
        Plant front = state.getMap().getFrontPlantForZombie((int) y, x);
        if (front != null && shouldFlyOver(front)) {
            this.isFlying = true;
            this.x -= getEffectiveSpeed() * 0.1;
            state.getMap().moveZombieToTile(this);
            return;
        }
        this.isFlying = false;
        super.move(state);
    }

    private boolean shouldFlyOver(Plant plant) {
        if ("TallNut".equalsIgnoreCase(plant.getName())) {
            return false;
        }
        return plant.getMaxHp() >= 1000
                || plant.hasTag(PlantTag.EXPLOSIVE)
                || plant.hasTag(PlantTag.TRAP)
                || plant.hasTag(PlantTag.MOVE_ZOMBIES);
    }

    public boolean isFlying() { return isFlying; }
    public void setFlying(boolean flying) { isFlying = flying; }
}
