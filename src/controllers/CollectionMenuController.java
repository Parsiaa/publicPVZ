package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import models.User;
import utils.PlantFactory;
import utils.Result;
import utils.UserApp;
import utils.ZombieFactory;

public class CollectionMenuController {
    private UserApp userApp;
    private static final int PURCHASE_COST = 2000;
    private static final int BASE_UPGRADE_COST = 250;

    public CollectionMenuController(UserApp userApp) {
        this.userApp = userApp;
    }

    public Result showUnlockedPlants() {
        Set<String> unlocked = userApp.getLoggedInUser().getUnlockedPlants();
        return new Result("Unlocked plants:\n- " + String.join("\n- ", unlocked), true);
    }

    public Result showAllPlants() {
        return new Result("All plants:\n- " + String.join("\n- ", PlantFactory.getAllPlantNames()), true);
    }

    public Result showUnlockedZombies() {
        User user = userApp.getLoggedInUser();
        List<String> allZombies = ZombieFactory.getAllZombieNames();
        int count = Math.min(allZombies.size(), 1 + user.getHighestLevel());
        List<String> seen = allZombies.subList(0, count);
        return new Result("Encountered zombies:\n- " + String.join("\n- ", seen), true);
    }

    public Result showAllZombies() {
        return new Result("All zombies:\n- " + String.join("\n- ", ZombieFactory.getAllZombieNames()), true);
    }

    public Result showPlantDetails(String plantName) {
        String name = PlantFactory.properName(plantName);
        if (name == null) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        User user = userApp.getLoggedInUser();
        int level = user.getPlantLevel(name);
        boolean unlocked = user.getUnlockedPlants().contains(name);
        return new Result("Plant: " + name
                + "\nCost: " + PlantFactory.getCost(name) + " sun"
                + "\nLevel: " + level
                + "\nNext upgrade cost: " + upgradeCostFor(level) + " coins"
                + "\nStatus: " + (unlocked ? "Unlocked" : "Locked"), true);
    }

    public Result showZombieDetails(String zombieName) {
        if (!ZombieFactory.isKnownZombie(zombieName)) {
            return new Result("Error: Zombie '" + zombieName + "' doesn't exist.", false);
        }
        return new Result("Zombie: " + zombieName
                + "\nWave cost: " + ZombieFactory.getWaveCost(zombieName)
                + "\nSpawn weight: " + ZombieFactory.getWeight(zombieName), true);
    }

    public Result upgradePlant(String plantName) {
        User user = userApp.getLoggedInUser();
        String name = PlantFactory.properName(plantName);
        if (name == null) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        if (!user.getUnlockedPlants().contains(name)) {
            return new Result("Error: You haven't unlocked this plant.", false);
        }
        int level = user.getPlantLevel(name);
        if (level >= 5) {
            return new Result("Error: Plant is already at max level.", false);
        }
        int cost = upgradeCostFor(level);
        int packetsNeeded = level;
        if (user.getCoins() < cost || user.getSeedPacketCount(name) < packetsNeeded) {
            return new Result("Error: Not enough coins or seed packets. Upgrading costs " + cost
                    + " coins and " + packetsNeeded + " seed packet(s) of " + name + ".", false);
        }
        user.setCoins(user.getCoins() - cost);
        user.consumeSeedPackets(name, packetsNeeded);
        user.setPlantLevel(name, level + 1);
        userApp.saveUsers();
        return new Result(name + " upgraded to level " + (level + 1) + "!", true);
    }

    public Result purchasePlant(String plantName) {
        User user = userApp.getLoggedInUser();
        String name = PlantFactory.properName(plantName);
        if (name == null) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        if (user.getUnlockedPlants().contains(name)) {
            return new Result("Error: Plant is already unlocked.", false);
        }
        if (user.getCoins() < PURCHASE_COST) {
            return new Result("Error: Not enough coins. Buying a new plant costs " + PURCHASE_COST + " coins.", false);
        }
        user.setCoins(user.getCoins() - PURCHASE_COST);
        user.unlockPlant(name);
        userApp.saveUsers();
        return new Result(name + " purchased and unlocked!", true);
    }

    public boolean isPlantUnlocked(String plantName) {
        String name = PlantFactory.properName(plantName);
        return name != null && userApp.getLoggedInUser().getUnlockedPlants().contains(name);
    }

    public List<String> getUnlockedPlants(User user) {
        return new ArrayList<>(user.getUnlockedPlants());
    }

    public boolean plantExists(String plantName) {
        return PlantFactory.isKnownPlant(plantName);
    }

    private int upgradeCostFor(int level) {
        return BASE_UPGRADE_COST * level;
    }
}
