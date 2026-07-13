package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class Pot {
    private boolean isUnlocked;
    private boolean isEmpty;
    private String plantedPlantName;
    private LocalDateTime plantTime;
    private boolean isMarigold;
    private static final int MARIGOLD_GROWTH_HOURS = 2;
    private static final int PLANT_GROWTH_HOURS = 8;

    public Pot() {
        this.isUnlocked = false;
        this.isEmpty = true;
        this.plantedPlantName = null;
        this.plantTime = null;
        this.isMarigold = false;
    }

    public void plantRandomSeed(List<String> unlockedPlants) {
        Random random = new Random();
        if (unlockedPlants == null || unlockedPlants.isEmpty() || random.nextBoolean()) {
            this.isMarigold = true;
            this.plantedPlantName = "Marigold";
        } else {
            this.isMarigold = false;
            this.plantedPlantName = unlockedPlants.get(random.nextInt(unlockedPlants.size()));
        }
        this.plantTime = LocalDateTime.now();
        this.isEmpty = false;
    }

    public double getRemainingGrowthHours() {
        if (isEmpty || plantTime == null) {
            return 0;
        }
        int growthHours = isMarigold ? MARIGOLD_GROWTH_HOURS : PLANT_GROWTH_HOURS;
        double hoursPassed = Duration.between(plantTime, LocalDateTime.now()).toMinutes() / 60.0;
        return Math.max(0, growthHours - hoursPassed);
    }

    public boolean isReadyToHarvest() {
        return !isEmpty && getRemainingGrowthHours() <= 0;
    }

    public void finishGrowth() {
        if (!isEmpty && plantTime != null) {
            int growthHours = isMarigold ? MARIGOLD_GROWTH_HOURS : PLANT_GROWTH_HOURS;
            this.plantTime = LocalDateTime.now().minusHours(growthHours);
        }
    }

    /** Restores a pot from saved data. */
    public void loadState(boolean unlocked, boolean empty, String plantName,
                          LocalDateTime time, boolean marigold) {
        this.isUnlocked = unlocked;
        this.isEmpty = empty;
        this.plantedPlantName = plantName;
        this.plantTime = time;
        this.isMarigold = marigold;
    }

    public void clearPot() {
        this.isEmpty = true;
        this.plantedPlantName = null;
        this.plantTime = null;
        this.isMarigold = false;
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

    public LocalDateTime getPlantTime() {
        return plantTime;
    }

    public boolean isMarigold() {
        return isMarigold;
    }
}
