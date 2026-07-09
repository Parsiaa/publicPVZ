package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import models.User;
import utils.Result;
import utils.UserApp;

public class CollectionMenuController {
    private UserApp userApp;
    private java.util.Map<String, Set<String>> unlockedPlantsByUser;
    private java.util.Map<String, Integer> plantLevels;
    private static final List<String> ALL_PLANTS = Arrays.asList(
            "Sunflower", "Peashooter", "WallNut", "CherryBomb", "Cabbagepult",
            "Chomper", "IcebergLettuce", "BonkChoy", "Bloomerang", "PeppermInt");
    private static final List<String> ALL_ZOMBIES = Arrays.asList(
            "BasicZombie", "ExplorerZombie", "RaZombie", "TombRaiserZombie",
            "DodoZombie", "GargantuarZombie");
    private static final int PURCHASE_COST = 2000;
    private static final int BASE_UPGRADE_COST = 250;

    public CollectionMenuController(UserApp userApp) {
        this.userApp = userApp;
        this.unlockedPlantsByUser = new HashMap<>();
        this.plantLevels = new HashMap<>();
    }

    public Result showUnlockedPlants() {
        Set<String> unlocked = unlockedFor(userApp.getLoggedInUser());
        return new Result("Unlocked plants:\n- " + String.join("\n- ", unlocked), true);
    }

    public Result showAllPlants() {
        return new Result("All plants:\n- " + String.join("\n- ", ALL_PLANTS), true);
    }

    public Result showUnlockedZombies() {
        User user = userApp.getLoggedInUser();
        int count = Math.min(ALL_ZOMBIES.size(), 1 + user.getHighestLevel());
        List<String> seen = ALL_ZOMBIES.subList(0, count);
        return new Result("Encountered zombies:\n- " + String.join("\n- ", seen), true);
    }

    public Result showAllZombies() {
        return new Result("All zombies:\n- " + String.join("\n- ", ALL_ZOMBIES), true);
    }

    public Result showPlantDetails(String plantName) {
        if (!containsIgnoreCase(ALL_PLANTS, plantName)) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        String name = properName(ALL_PLANTS, plantName);
        int level = plantLevels.getOrDefault(key(name), 1);
        boolean unlocked = unlockedFor(userApp.getLoggedInUser()).contains(name);
        return new Result("Plant: " + name
                + "\nLevel: " + level
                + "\nNext upgrade cost: " + upgradeCostFor(level) + " coins"
                + "\nStatus: " + (unlocked ? "Unlocked" : "Locked"), true);
    }

    public Result showZombieDetails(String zombieName) {
        if (!containsIgnoreCase(ALL_ZOMBIES, zombieName)) {
            return new Result("Error: Zombie '" + zombieName + "' doesn't exist.", false);
        }
        return new Result("Zombie: " + properName(ALL_ZOMBIES, zombieName)
                + "\nThreat: standard lane walker unless stated otherwise.", true);
    }

    public Result upgradePlant(String plantName) {
        User user = userApp.getLoggedInUser();
        if (!containsIgnoreCase(ALL_PLANTS, plantName)) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        String name = properName(ALL_PLANTS, plantName);
        if (!unlockedFor(user).contains(name)) {
            return new Result("Error: You haven't unlocked this plant.", false);
        }
        int level = plantLevels.getOrDefault(key(name), 1);
        if (level >= 5) {
            return new Result("Error: Plant is already at max level.", false);
        }
        int cost = upgradeCostFor(level);
        if (user.getCoins() < cost) {
            return new Result("Error: Not enough coins or seed packets. Upgrading costs " + cost + " coins.", false);
        }
        user.setCoins(user.getCoins() - cost);
        plantLevels.put(key(name), level + 1);
        userApp.saveUsers();
        return new Result(name + " upgraded to level " + (level + 1) + "!", true);
    }

    public Result purchasePlant(String plantName) {
        User user = userApp.getLoggedInUser();
        if (!containsIgnoreCase(ALL_PLANTS, plantName)) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        Set<String> unlocked = unlockedFor(user);
        String name = properName(ALL_PLANTS, plantName);
        if (unlocked.contains(name)) {
            return new Result("Error: Plant is already unlocked.", false);
        }
        if (user.getCoins() < PURCHASE_COST) {
            return new Result("Error: Not enough coins. Buying a new plant costs " + PURCHASE_COST + " coins.", false);
        }
        user.setCoins(user.getCoins() - PURCHASE_COST);
        unlocked.add(name);
        userApp.saveUsers();
        return new Result(name + " purchased and unlocked!", true);
    }

    public boolean isPlantUnlocked(String plantName) {
        return unlockedFor(userApp.getLoggedInUser()).contains(properName(ALL_PLANTS, plantName));
    }

    public List<String> getUnlockedPlants(User user) {
        return new ArrayList<>(unlockedFor(user));
    }

    public boolean plantExists(String plantName) {
        return containsIgnoreCase(ALL_PLANTS, plantName);
    }

    private int upgradeCostFor(int level) {
        return BASE_UPGRADE_COST * level;
    }

    private Set<String> unlockedFor(User user) {
        return unlockedPlantsByUser.computeIfAbsent(user.getUsername(),
                k -> new HashSet<>(Arrays.asList("Sunflower", "Peashooter", "WallNut")));
    }

    private boolean containsIgnoreCase(List<String> list, String name) {
        for (String s : list) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private String properName(List<String> list, String name) {
        for (String s : list) {
            if (s.equalsIgnoreCase(name)) {
                return s;
            }
        }
        return name;
    }

    private String key(String plantName) {
        return userApp.getLoggedInUser().getUsername() + ":" + plantName.toLowerCase();
    }
}
