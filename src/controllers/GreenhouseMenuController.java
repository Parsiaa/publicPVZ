package controllers;

import java.util.*;
import models.Pot;
import models.User;
import models.Enums.Menu;
import utils.Result;
import utils.UserApp;

public class GreenhouseMenuController {
    private UserApp userApp;
    private MenuRouter router;
    private CollectionMenuController collectionController;

    private static final int ROWS = 4;
    private static final int COLUMNS = 5;
    private static final int MARIGOLD_REWARD_COINS = 500;

    public GreenhouseMenuController(UserApp userApp, MenuRouter router,
                                    CollectionMenuController collectionController) {
        this.userApp = userApp;
        this.router = router;
        this.collectionController = collectionController;
    }

    public Result handleShowGreenhouse() {
        Pot[][] pots = potsFor(userApp.getLoggedInUser());
        StringBuilder sb = new StringBuilder("Greenhouse:\n");
        for (int y = 1; y <= ROWS; y++) {
            for (int x = 1; x <= COLUMNS; x++) {
                Pot pot = pots[y - 1][x - 1];
                sb.append("(").append(x).append(",").append(y).append("): ");
                if (!pot.isUnlocked()) {
                    sb.append("Locked");
                } else if (pot.isEmpty()) {
                    sb.append("Empty");
                } else if (pot.isReadyToHarvest()) {
                    sb.append(pot.getPlantedPlantName()).append(" [ready]");
                } else {
                    sb.append(pot.getPlantedPlantName())
                            .append(" (").append(formatHours(pot.getRemainingGrowthHours())).append("h left)");
                }
                sb.append("\n");
            }
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result handlePlantPot(int x, int y) {
        Result check = validatePot(x, y);
        if (check != null) {
            return check;
        }
        Pot pot = potAt(userApp.getLoggedInUser(), x, y);
        if (!pot.isEmpty()) {
            return new Result("Error: Pot (" + x + "," + y + ") already has a plant.", false);
        }
        List<String> unlocked = collectionController.getUnlockedPlants(userApp.getLoggedInUser());
        pot.plantRandomSeed(unlocked);
        return new Result("Planted " + pot.getPlantedPlantName() + " in pot (" + x + "," + y + ").", true);
    }

    public Result handleCollectPot(int x, int y) {
        Result check = validatePot(x, y);
        if (check != null) {
            return check;
        }
        User user = userApp.getLoggedInUser();
        Pot pot = potAt(user, x, y);
        if (pot.isEmpty()) {
            return new Result("Error: Pot (" + x + "," + y + ") is empty.", false);
        }
        if (!pot.isReadyToHarvest()) {
            return new Result("Error: Plant is not fully grown yet ("
                    + formatHours(pot.getRemainingGrowthHours()) + "h left).", false);
        }
        Result result;
        if (pot.isMarigold()) {
            user.setCoins(user.getCoins() + MARIGOLD_REWARD_COINS);
            result = new Result("Harvested a Marigold! You earned " + MARIGOLD_REWARD_COINS + " coins.", true);
        } else {
            String plantName = pot.getPlantedPlantName();
            if (user.hasBoostFor(plantName)) {
                result = new Result("Harvested " + plantName
                        + ". A stored boost for it already exists, so no extra boost was added.", true);
            } else {
                user.addBoostFor(plantName);
                result = new Result("Harvested " + plantName
                        + "! A boost is stored for the next time you plant it in a level.", true);
            }
        }
        pot.clearPot();
        userApp.saveUsers();
        return result;
    }

    public Result handleGrow(int x, int y) {
        Result check = validatePot(x, y);
        if (check != null) {
            return check;
        }
        User user = userApp.getLoggedInUser();
        Pot pot = potAt(user, x, y);
        if (pot.isEmpty()) {
            return new Result("Error: Pot (" + x + "," + y + ") is empty.", false);
        }
        if (pot.isReadyToHarvest()) {
            return new Result("Error: Plant is already ready to harvest.", false);
        }
        int cost = (int) Math.ceil(pot.getRemainingGrowthHours());
        if (cost < 1) {
            cost = 1;
        }
        if (user.getGems() < cost) {
            return new Result("Error: Accelerating growth costs " + cost + " gems (you have "
                    + user.getGems() + ").", false);
        }
        user.setGems(user.getGems() - cost);
        pot.finishGrowth();
        userApp.saveUsers();
        return new Result("Growth accelerated for " + cost + " gems. The plant is ready to harvest.", true);
    }

    public Result unlockNextPot(User user) {
        Pot[][] pots = potsFor(user);
        for (int y = 1; y <= ROWS; y++) {
            for (int x = 1; x <= COLUMNS; x++) {
                Pot pot = pots[y - 1][x - 1];
                if (!pot.isUnlocked()) {
                    pot.setUnlocked(true);
                    return new Result("Pot (" + x + "," + y + ") has been unlocked!", true);
                }
            }
        }
        return new Result("Error: All greenhouse pots are already unlocked.", false);
    }

    public int lockedPotCount(User user) {
        int count = 0;
        for (Pot[] row : potsFor(user)) {
            for (Pot pot : row) {
                if (!pot.isUnlocked()) {
                    count++;
                }
            }
        }
        return count;
    }

    public Result handleEnterShop() {
        router.navigateTo(Menu.ShopMenu);
        return new Result("Entered the Shop.", true);
    }

    private Result validatePot(int x, int y) {
        if (x < 1 || x > COLUMNS || y < 1 || y > ROWS) {
            return new Result("Error: Coordinates must be x in 1.." + COLUMNS + " and y in 1.." + ROWS + ".", false);
        }
        if (!potAt(userApp.getLoggedInUser(), x, y).isUnlocked()) {
            return new Result("Error: Pot (" + x + "," + y + ") is locked.", false);
        }
        return null;
    }

    private Pot potAt(User user, int x, int y) {
        return potsFor(user)[y - 1][x - 1];
    }

    private Pot[][] potsFor(User user) {
        return user.getGreenhousePots();
    }

    private String formatHours(double hours) {
        return String.format("%.1f", Math.max(0, hours));
    }
}

