package models;

import models.Blueprints.ArmorData;

public class Armor {
    private ArmorData data;
    private int currentHealth;

    public Armor(ArmorData data) {
        this.data = data;
        this.currentHealth = data != null ? data.getBaseHealth() : 0;
    }

    public boolean isMagnetic() {
        return data != null && data.hasFlag("magnetic");
    }

    /** Metal armour (bucket, crown, ...) that a Magnet-shroom can pull off. */
    public boolean isMetallic() {
        return data != null && data.hasFlag("metallic");
    }

    public boolean isBroken() {
        return currentHealth <= 0;
    }

    public int takeDamage(int amount) {
        int absorbed = Math.min(currentHealth, amount);
        currentHealth -= absorbed;
        return amount - absorbed;
    }

    public ArmorData getData() { return data; }
    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }
}
