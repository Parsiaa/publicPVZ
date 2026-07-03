package models.PlantTypes;

import models.MatchState;
import models.Plant;
import models.Zombie;
import models.Enums.DamageType;

public class BowlingPlant extends Plant {
    private int moveAngle;
    private boolean isGiant;
    private boolean isExplosive;

    @Override
    public void act(MatchState state) {
        if (isDead()) {
            return;
        }
        this.x += 0.5;
        if (moveAngle > 0) {
            this.y = Math.min(state.getMap().getRows() - 1, y + 0.5);
        } else if (moveAngle < 0) {
            this.y = Math.max(0, y - 0.5);
        }
        for (Zombie zombie : state.getMap().getZombiesInRow((int) y)) {
            if (zombie.getCurrentHealth() > 0 && Math.abs(zombie.getX() - x) <= 0.5) {
                handleCollision(zombie);
                if (isExplosive) {
                    explodeAround(state, zombie);
                    return;
                }
            }
        }
        if (x >= state.getMap().getColumns()) {
            this.health = 0;
            state.getMap().removePlant(this);
        }
    }

    public void handleCollision(Zombie zombie) {
        int damage = isGiant ? baseDamage * 2 : baseDamage;
        zombie.takeDamage(damage, DamageType.NORMAL);
        if (!isGiant && !isExplosive) {
            this.moveAngle = moveAngle >= 0 ? -1 : 1;
        }
    }

    private void explodeAround(MatchState state, Zombie center) {
        for (Zombie zombie : state.getMap().getAllZombies()) {
            if (zombie.getCurrentHealth() > 0
                    && Math.abs(zombie.getX() - center.getX()) <= 1
                    && Math.abs(zombie.getY() - center.getY()) <= 1) {
                zombie.takeDamage(baseDamage, DamageType.FIRE);
            }
        }
        this.health = 0;
        state.getMap().removePlant(this);
    }

    public int getMoveAngle() { return moveAngle; }
    public void setMoveAngle(int moveAngle) { this.moveAngle = moveAngle; }
    public boolean isGiant() { return isGiant; }
    public void setGiant(boolean giant) { isGiant = giant; }
    public boolean isExplosive() { return isExplosive; }
    public void setExplosive(boolean explosive) { isExplosive = explosive; }
}
