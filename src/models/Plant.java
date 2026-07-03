package models;

import java.util.*;
import models.Enums.*;

public abstract class Plant extends Entity {
    protected int id;
    protected String name;
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
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getBaseDamage() { return baseDamage; }
    public void setBaseDamage(int baseDamage) { this.baseDamage = baseDamage; }
    public double getActionInterval() { return actionInterval; }
    public double getRechargeTime() { return rechargeTime; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public List<PlantTag> getTags() { return tags; }
    public void setTags(List<PlantTag> tags) { this.tags = tags; }
    public int getFrostLevel() { return frostLevel; }
}
