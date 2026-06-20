package models;

public class Brain {
    private int currentHealth;

    public Brain(int currentHealth) {
        this.currentHealth = currentHealth;
    }
    public void beEaten(int amount){
        //TODO
    }
    public int getCurrentHealth() {return currentHealth;}
    public void setCurrentHealth(int currentHealth) {this.currentHealth = currentHealth;}
}
