package models;

import java.util.ArrayList;
import java.util.List;
import models.Blueprints.ZombieData;
import models.Enums.DamageType;

public abstract class Zombie extends Entity {

    /** Base durations (in-game seconds) for the timed status effects. */
    public static final double CHILL_SECONDS = 5.0;
    public static final double FROZEN_SECONDS = 5.0;
    public static final double BUTTER_SECONDS = 3.0;
    public static final double POISON_SECONDS = 3.0;

    protected ZombieData data;
    protected int currentHealth;
    protected List<Armor> armors = new ArrayList<>();
    protected List<String> effects = new ArrayList<>();
    protected List<Effect> timedEffects = new ArrayList<>();
    protected Plant targetPlant;
    protected boolean hypnotized;

    public void move(MatchState state) {
        if (currentHealth <= 0 || isImmobilized()) {
            return;
        }
        if (hypnotized) {
            moveHypnotized(state);
            return;
        }
        Plant front = state.getMap().getFrontPlantForZombie((int) y, x);
        if (front != null) {
            this.targetPlant = front;
            eat(state);
            if (targetPlant == null || targetPlant.isDead()) {
                this.targetPlant = null;
            }
            return;
        }
        this.targetPlant = null;
        this.x -= getEffectiveSpeed() * 0.1;
        state.getMap().moveZombieToTile(this);
    }

    /** Hypnotised zombies walk to the right and attack the first enemy zombie ahead. */
    protected void moveHypnotized(MatchState state) {
        Zombie enemy = null;
        for (Zombie other : state.getMap().getZombiesInRow((int) y)) {
            if (other != this && !other.isHypnotized() && other.getCurrentHealth() > 0
                    && other.getX() > x && (enemy == null || other.getX() < enemy.getX())) {
                enemy = other;
            }
        }
        if (enemy != null && enemy.getX() - x <= 0.5) {
            int dps = data != null ? data.getEatDps() : 100;
            enemy.takeDamage(Math.max(1, dps / 10), DamageType.NORMAL);
            return;
        }
        this.x += getEffectiveSpeed() * 0.1;
        if (x > state.getMap().getColumns() - 1) {
            x = state.getMap().getColumns() - 1;
        }
        state.getMap().moveZombieToTile(this);
    }

    public void eat(MatchState state) {
        if (targetPlant == null || targetPlant.isDead() || isImmobilized()) {
            if (targetPlant != null && targetPlant.isDead()) {
                targetPlant = null;
            }
            return;
        }
        int dps = data != null ? data.getEatDps() : 100;
        targetPlant.onBittenBy(this, state);
        if (targetPlant == null || targetPlant.isDead()) {
            targetPlant = null;
            return;
        }
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
        applyDamageTypeEffect(type, amount);
        if (currentHealth <= 0) {
            System.out.println("Zombie of type " + getTypeName() + " is dead at ("
                    + String.format("%.1f", x) + ", " + (int) y + ")");
        }
    }

    private void applyDamageTypeEffect(DamageType type, int amount) {
        if (type == DamageType.ICE) {
            applyChill();
        } else if (type == DamageType.FIRE) {
            removeChill();
        } else if (type == DamageType.POISON) {
            applyTimedEffect("poisoned", POISON_SECONDS, Math.max(1, amount));
        }
    }

    @Override
    public void takeDamage(int amount) {
        takeDamage(amount, DamageType.NORMAL);
    }

    /** Per-tick special behaviour hook for zombies with abilities (default: none). */
    public void onTick(MatchState state) {
    }

    /** Advances every timed effect by one tick and applies damage-over-time effects. */
    public void tickEffects() {
        for (Effect effect : new ArrayList<>(timedEffects)) {
            effect.tick();
            if (effect.getName().equals("poisoned") && currentHealth > 0) {
                int dot = (int) Math.max(1, Math.round(effect.getMagnitude() * 0.1));
                currentHealth = Math.max(0, currentHealth - dot);
                this.health = currentHealth;
                if (currentHealth <= 0) {
                    System.out.println("Zombie of type " + getTypeName() + " is dead at ("
                            + String.format("%.1f", x) + ", " + (int) y + ")");
                }
            }
            if (effect.isExpired()) {
                timedEffects.remove(effect);
            }
        }
    }

    public void applyTimedEffect(String name, double durationSeconds, double magnitude) {
        for (Effect effect : timedEffects) {
            if (effect.getName().equals(name)) {
                effect.refresh(durationSeconds);
                return;
            }
        }
        timedEffects.add(new Effect(name, durationSeconds, magnitude));
    }

    public boolean hasTimedEffect(String name) {
        for (Effect effect : timedEffects) {
            if (effect.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void removeTimedEffect(String name) {
        timedEffects.removeIf(effect -> effect.getName().equals(name));
    }

    public void applyChill() {
        applyTimedEffect("chilled", CHILL_SECONDS, 0);
    }

    public void removeChill() {
        removeTimedEffect("chilled");
        removeTimedEffect("frozen");
    }

    public boolean isChilled() {
        return hasTimedEffect("chilled");
    }

    /** Frozen (Iceberg/Ice-shroom) or buttered (Kernel-pult) zombies cannot move or eat. */
    public boolean isImmobilized() {
        return hasTimedEffect("frozen") || hasTimedEffect("buttered");
    }

    protected double getEffectiveSpeed() {
        double speed = data != null ? data.getSpeed() : 1.0;
        if (isImmobilized()) {
            return 0;
        }
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
        for (Effect effect : timedEffects) {
            sb.append("\n        ").append(effect.getName()).append(": ")
                    .append(String.format("%.1f", Math.max(0, effect.getRemainingSeconds()))).append("s");
        }
        if (hypnotized) {
            sb.append("\n        hypnotized");
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
    public boolean hasArmor() {
        for (Armor armor : armors) {
            if (!armor.isBroken()) {
                return true;
            }
        }
        return false;
    }

    public List<Effect> getTimedEffects() { return timedEffects; }

    public List<String> getEffects() { return effects; }
    public void setEffects(List<String> effects) { this.effects = effects; }

    public boolean isHypnotized() { return hypnotized; }
    public void setHypnotized(boolean hypnotized) { this.hypnotized = hypnotized; }

    public Plant getTargetPlant() { return targetPlant; }
}
