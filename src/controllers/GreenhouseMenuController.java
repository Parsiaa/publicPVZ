package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.Pot;
import models.User;
import models.Enums.Menu;
import utils.Result;
import utils.UserApp;

public class GreenhouseMenuController {
    private UserApp userApp;
    private MenuRouter router;
    private java.util.Map<String, List<Pot>> potsByUser;
    private static final int MAX_POTS = 8;

    public GreenhouseMenuController(UserApp userApp, MenuRouter router) {
        this.userApp = userApp;
        this.router = router;
        this.potsByUser = new HashMap<>();
    }

    public Result handleShowGreenhouse() {
        List<Pot> pots = potsFor(userApp.getLoggedInUser());
        StringBuilder sb = new StringBuilder("Greenhouse:\n");
        for (int i = 0; i < pots.size(); i++) {
            Pot pot = pots.get(i);
            sb.append("Pot ").append(i + 1).append(": ");
            if (!pot.isUnlocked()) {
                sb.append("Locked");
            } else if (pot.isEmpty()) {
                sb.append("Empty");
            } else if (pot.isReadyToHarvest()) {
                sb.append(pot.getPlantedPlantName()).append(" (ready to harvest!)");
            } else {
                sb.append(pot.getPlantedPlantName()).append(" (").append(pot.getRemainingGrowthTime()).append(" days left)");
            }
            sb.append("\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result handlePlantPot(int potNumber) {
        Result check = validatePot(potNumber);
        if (check != null) {
            return check;
        }
        Pot pot = potsFor(userApp.getLoggedInUser()).get(potNumber - 1);
        if (!pot.isEmpty()) {
            return new Result("Error: Pot " + potNumber + " already has a plant.", false);
        }
        pot.plantRandomSeed();
        return new Result("Planted " + pot.getPlantedPlantName() + " in pot " + potNumber + ".", true);
    }

    public Result handleCollectPot(int potNumber) {
        Result check = validatePot(potNumber);
        if (check != null) {
            return check;
        }
        User user = userApp.getLoggedInUser();
        Pot pot = potsFor(user).get(potNumber - 1);
        if (pot.isEmpty()) {
            return new Result("Error: Pot " + potNumber + " is empty.", false);
        }
        if (!pot.isReadyToHarvest()) {
            return new Result("Error: Plant needs " + pot.getRemainingGrowthTime() + " more days.", false);
        }
        int coins = pot.harvest();
        user.setCoins(user.getCoins() + coins);
        userApp.saveUsers();
        return new Result("Harvested! You earned " + coins + " coins.", true);
    }

    public Result handleGrow(int potNumber) {
        Result check = validatePot(potNumber);
        if (check != null) {
            return check;
        }
        User user = userApp.getLoggedInUser();
        Pot pot = potsFor(user).get(potNumber - 1);
        if (pot.isEmpty()) {
            return new Result("Error: Pot " + potNumber + " is empty.", false);
        }
        if (pot.isReadyToHarvest()) {
            return new Result("Error: Plant is already fully grown.", false);
        }
        if (user.getGems() < 1) {
            return new Result("Error: Accelerating growth costs 1 gem.", false);
        }
        user.setGems(user.getGems() - 1);
        pot.accelerateGrowth();
        userApp.saveUsers();
        return new Result("Growth accelerated by one day. " + pot.getRemainingGrowthTime() + " days left.", true);
    }

    public Result handleAddPot() {
        List<Pot> pots = potsFor(userApp.getLoggedInUser());
        for (Pot pot : pots) {
            if (!pot.isUnlocked()) {
                pot.setUnlocked(true);
                return new Result("A new pot has been unlocked!", true);
            }
        }
        return new Result("Error: All pots are already unlocked.", false);
    }

    public Result handleEnterShop() {
        router.navigateTo(Menu.ShopMenu);
        return new Result("Entered the Shop.", true);
    }

    private Result validatePot(int potNumber) {
        List<Pot> pots = potsFor(userApp.getLoggedInUser());
        if (potNumber < 1 || potNumber > pots.size()) {
            return new Result("Error: Pot number must be between 1 and " + pots.size() + ".", false);
        }
        if (!pots.get(potNumber - 1).isUnlocked()) {
            return new Result("Error: Pot " + potNumber + " is locked.", false);
        }
        return null;
    }

    private List<Pot> potsFor(User user) {
        return potsByUser.computeIfAbsent(user.getUsername(), k -> {
            List<Pot> pots = new ArrayList<>();
            for (int i = 0; i < MAX_POTS; i++) {
                Pot pot = new Pot();
                if (i < 2) {
                    pot.setUnlocked(true);
                }
                pots.add(pot);
            }
            return pots;
        });
    }
}
