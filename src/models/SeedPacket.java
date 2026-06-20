package models;

public class SeedPacket {

    private String plantName;
    private int sunCost;
    private double rechargeTime;
    private double currentCooldown;
    private boolean isBoosted;
    private boolean hasStoredBoost;
    private double expirationTimer;


    public SeedPacket(String plantName, int sunCost, double rechargeTime) {
        this.plantName = plantName;
        this.sunCost = sunCost;
        this.rechargeTime = rechargeTime;
        this.currentCooldown = 0;
        this.isBoosted = false;
        this.hasStoredBoost = false;
        this.expirationTimer = 0;
    }



    public boolean isReadyToPlant() {

        return currentCooldown <= 0;
    }

    public void startCooldown() {

        this.currentCooldown = this.rechargeTime;
    }

    public void applyBoost() {

        this.isBoosted = true;
    }

    public double getExpirationTimer() {
        return expirationTimer;
    }

    public void setExpirationTimer(double expirationTimer) {
        this.expirationTimer = expirationTimer;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public int getSunCost() {
        return sunCost;
    }

    public void setSunCost(int sunCost) {
        this.sunCost = sunCost;
    }

    public double getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(double rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public double getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(double currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    public boolean isBoosted() {
        return isBoosted;
    }

    public void setBoosted(boolean boosted) {
        isBoosted = boosted;
    }

    public boolean isHasStoredBoost() {
        return hasStoredBoost;
    }

    public void setHasStoredBoost(boolean hasStoredBoost) {
        this.hasStoredBoost = hasStoredBoost;
    }
}
