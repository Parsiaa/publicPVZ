package models;

import java.time.LocalDate;
import java.util.Random;

public class Pot {
    
    private boolean isUnlocked;
    private boolean isEmpty;
    private String plantedPlantName;
    private LocalDate plantTime;
    private boolean isMarigold;
    
    
    public Pot() {
        this.isUnlocked = false;
        this.isEmpty = true;
        this.plantedPlantName = null;
        this.plantTime = null;
        this.isMarigold = false;
    }
    
    
    public void plantRandomSeed() {
        //TODO
    }
    
    public int getRemainingGrowthTime() {
        if (isEmpty || plantTime == null) {
            return 0;
        }

        int daysPassed = (int) java.time.temporal.ChronoUnit.DAYS.between(plantTime, LocalDate.now());
        int growthDuration = 7;
        int remaining = growthDuration - daysPassed;
        return Math.max(0, remaining);
    }
    
    public boolean isReadyToHarvest() {
        return !isEmpty && getRemainingGrowthTime() == 0;
    }
    
    public void accelerateGrowth() {
        if (!isEmpty && plantTime != null) {
            this.plantTime = plantTime.minusDays(1);
        }
    }
    
    public int harvest() {
        if (isReadyToHarvest()) {
            int coins = 0;
            if (isMarigold) {
                coins = 50;
            } else {
                coins = 25;
            }
            

            this.isEmpty = true;
            this.plantedPlantName = null;
            this.plantTime = null;
            this.isMarigold = false;
            
            return coins;
        }
        return 0;
    }
    

    public boolean isUnlocked() {
        return isUnlocked;
    }
    
    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }
    
    public String getPlantedPlantName() {
        return plantedPlantName;
    }
    
    public LocalDate getPlantTime() {
        return plantTime;
    }
    
    public boolean isMarigold() {
        return isMarigold;
    }
}

