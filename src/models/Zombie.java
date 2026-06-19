package models;

import java.util.List;

import models.Blueprints.ZombieData;

public abstract class Zombie extends Entity {
    ZombieData data;
    protected int currentHealth;
    //protected List<Armor> armors = new List<>(); TODO
    protected List<String> effects;

    public void move(MatchState state) {
        //TODO
    }

    public void eat() {
        //TODO
    }

    public String getInfo() {
        return null;
    } 
}
