package models;

import java.util.ArrayList;
import java.util.List;
import models.Blueprints.ZombieData;
import models.Enums.DamageType;

public abstract class Zombie extends Entity {
    protected ZombieData data;
    protected int currentHealth;
    protected List<Armor> armors = new ArrayList<>();
    protected List<String> effects = new ArrayList<>();
    protected Plant targetPlant;

    public void move(MatchState state) {
        if (currentHealth <= 0) {
            return;
        }
        Plant front = state.getMap().getFrontPlantForZombie((int) y, x);
        if (front != null) {
            this.targetPlant = front;
            eat();
            if (targetPlant == null || targetPlant.isDead()) {
                this.targetPlant = null;
            }
            return;
        }
        this.targetPlant = null;
        this.x -= getEffectiveSpeed() * 0.1;
        state.getMap().moveZombieToTile(this);
    }

    public void eat() {
        if (targetPlant == null || targetPlant.isDead()) {
            targetPlant = null;
            return;
        }
        int dps = data != null ? data.getEatDps() : 100;
        targetPlant.takeDamage(Math.max(1, dps / 10));
        if (targetPlant.isDead()) {
            System.out.println("Plant " + targetPlant.getName() + " at (" + (int) targetPlant.getX() + ", "
                    + (int) targetPlant.getY() + ") is destroyed.");
            targetPlant = null;
        }
    }

    public void takeDamage(int amount, DamageType type) {
        if (currentHealth <= 0) {
            return;
        }
        int remaining = amount;
        if (type != DamageType.POISON) {
            for (Armor armor : armors) {
                if (!armor.isBroken()) {
                    remaining = armor.takeDamage(remaining);
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        if (remaining > 0) {
            currentHealth = Math.max(0, currentHealth - remaining);
            this.health = currentHealth;
        }
        if (type == DamageType.ICE) {
            applyChill();
        } else if (type == DamageType.FIRE) {
            removeChill();
        }
        if (currentHealth <= 0) {
            System.out.println("Zombie of type " + getTypeName() + " is dead at ("
                    + String.format("%.1f", x) + ", " + (int) y + ")");
        }
    }

    @Override
    public void takeDamage(int amount) {
        takeDamage(amount, DamageType.NORMAL);
    }

    public void applyChill() {
        if (!effects.contains("chilled")) {
            effects.add("chilled");
        }
    }

    public void removeChill() {
        effects.remove("chilled");
    }

    public boolean isChilled() {
        return effects.contains("chilled");
    }

    protected double getEffectiveSpeed() {
        double speed = data != null ? data.getSpeed() : 1.0;
        if (isChilled()) {
            speed *= 0.5;
        }
        return speed;
    }

    public String getTypeName() {
        return data != null ? data.getAlias() : getClass().getSimpleName();
    }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTypeName()).append(":\n");
        sb.append("    position: ").append((int) Math.round(x)).append(", ").append((int) y).append("\n");
        sb.append("    health: ").append(currentHealth).append("\n");
        sb.append("    armor:");
        for (Armor armor : armors) {
            if (!armor.isBroken()) {
                sb.append("\n        ").append(armor.getData() != null ? armor.getData().getAlias() : "armor")
                        .append(": ").append(armor.getCurrentHealth());
            }
        }
        sb.append("\n    effects:");
        for (String effect : effects) {
            sb.append("\n        ").append(effect);
        }
        return sb.toString();
    }

    public ZombieData getData() { return data; }
    public void setData(ZombieData data) {
        this.data = data;
        if (data != null) {
            this.currentHealth = data.getHitpoints();
            this.health = currentHealth;
        }
    }

    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
        this.health = currentHealth;
    }

    public List<Armor> getArmors() { return armors; }
    public void addArmor(Armor armor) { this.armors.add(armor); }

    public List<String> getEffects() { return effects; }
    public void setEffects(List<String> effects) { this.effects = effects; }

    public Plant getTargetPlant() { return targetPlant; }
}
