package models;

public abstract class Entity {
    protected double x;



    protected double y;
    protected int health;

    public void takeDamage(int amount){
        health = Math.min(0, health - amount);
    }
    public boolean isDead(){
        return health > 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }






}
