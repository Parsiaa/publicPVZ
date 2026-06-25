package models;

import models.Enums.SunType;

public class Sun {
    private SunType type;
    private int amount;
    private boolean isFalling;
    

    public Sun(SunType type, int amount, boolean isFalling) {
        this.type = type;
        this.amount = amount;
        this.isFalling = isFalling;
    }

    public void fall() {
        //TODO
    }

    public void explode() {
        //TODO
    }

    public SunType getType() { return type; }
    public void setType(SunType type) { this.type = type; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    public boolean isFalling() { return isFalling; }
    public void setFalling(boolean isFalling) { this.isFalling = isFalling; }
}
