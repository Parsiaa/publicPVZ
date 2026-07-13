package models;

import java.util.*;
import models.Enums.*;

public abstract class Plant extends Entity {
    protected int id;
    protected String name;
    protected String category;
    protected int cost;
    protected int maxHp;
    protected int baseDamage;
    protected double actionInterval;
    protected double rechargeTime;
    protected int level;
    protected List<PlantTag> tags;
    protected int frostLevel;

    public void act(MatchState state) {
    }

    public void triggerPlantFood(MatchState state) {
    }

    /** Called the moment a zombie bites this plant, before damage is dealt. */
    public void onBittenBy(Zombie zombie, MatchState state) {
    }

    /** Called when this plant's health reaches zero, before it is removed. */
    public void onDeath(MatchState state) {
    }

    /** Whether this plant stops zombies that would otherwise fly/jump over obstacles (Tall-nut). */
    public boolean blocksFlying() {
        return false;
    }

    /** Whether pea shots passing over this plant become fiery (Torchwood). */
    public boolean ignitesPeas() {
        return false;
    }

    public void upgrade() {
        this.level++;
        this.maxHp += maxHp / 4;
        this.baseDamage += baseDamage / 4;
    }

    public void heal(int heal) {
        health = Math.min(maxHp, health + heal);
    }

    public boolean hasTag(PlantTag tag) {
        return tags != null && tags.contains(tag);
    }

    public void applyFrost(int mag) {
        frostLevel = Math.min(3, frostLevel + mag);
    }

    public void meltFrost(int mag) {
        frostLevel = Math.max(0, frostLevel - mag);
    }

    public boolean isFrozen() {
        return frostLevel >= 3;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getBaseDamage() { return baseDamage; }
    public void setBaseDamage(int baseDamage) { this.baseDamage = baseDamage; }
    public double getActionInterval() { return actionInterval; }
    public void setActionInterval(double actionInterval) { this.actionInterval = actionInterval; }
    public double getRechargeTime() { return rechargeTime; }
    public void setRechargeTime(double rechargeTime) { this.rechargeTime = rechargeTime; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public List<PlantTag> getTags() { return tags; }
    public void setTags(List<PlantTag> tags) { this.tags = tags; }
    public int getFrostLevel() { return frostLevel; }
}
