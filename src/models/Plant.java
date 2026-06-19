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

    }
    public void heal(int heal) {
        health = Math.max(maxHp, health + heal);
    }
    public boolean hasTag(PlantTag tag) {
        //TODO
        return (tags.size() > 0);
    }

    public void applyFrost(int mag) {
        //TODO
    }

    public void meltFrost(int mag) {
        //TODO
    }
    
    
    
} 