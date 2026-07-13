package controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import models.User;
import utils.PlantFactory;
import utils.Result;
import utils.UserApp;

/**
 * The shop, reachable from the greenhouse. Sells permanent items plus a daily
 * offer that refreshes at 00:00 (system clock) and can be bought once per day.
 */
public class ShopMenuController {

    private static final int POT_PRICE_COINS = 2000;
    private static final int PLANT_FOOD_PRICE_GEMS = 3;
    private static final int MAX_STORED_PLANT_FOODS = 3;
    private static final int RANDOM_SEED_PACK_PRICE_COINS = 1000;
    private static final int RANDOM_SEED_PACK_AMOUNT = 5;
    private static final int CHOSEN_SEED_PACK_PRICE_GEMS = 5;
    private static final int CHOSEN_SEED_PACK_AMOUNT = 10;
    private static final int EXCHANGE_PRICE_GEMS = 5;
    private static final int EXCHANGE_COINS_GIVEN = 500;
    private static final int DAILY_OFFER_PRICE_COINS = 1600;
    private static final int DAILY_OFFER_AMOUNT = 10;

    private UserApp userApp;
    private GreenhouseMenuController greenhouseController;
    private CollectionMenuController collectionController;
    private final Random random;
    private LocalDate dailyOfferDate;
    private String dailyOfferPlant;
    private Map<String, LocalDate> dailyPurchaseByUser;

    public ShopMenuController(UserApp userApp, GreenhouseMenuController greenhouseController,
                              CollectionMenuController collectionController) {
        this.userApp = userApp;
        this.greenhouseController = greenhouseController;
        this.collectionController = collectionController;
        this.random = new Random();
        this.dailyPurchaseByUser = new HashMap<>();
    }

    public Result handleShopList() {
        return new Result("Permanent shop items:\n"
                + "1. Pot | " + POT_PRICE_COINS + " coins | unlocks one greenhouse slot (max 20)\n"
                + "2. Plant Food | " + PLANT_FOOD_PRICE_GEMS + " gems | start levels with one extra plant food"
                + " (max " + MAX_STORED_PLANT_FOODS + " stored)\n"
                + "3. Random Seed Pack | " + RANDOM_SEED_PACK_PRICE_COINS + " coins | "
                + RANDOM_SEED_PACK_AMOUNT + " seed packets of a random unlocked plant\n"
                + "4. Chosen Seed Pack | " + CHOSEN_SEED_PACK_PRICE_GEMS + " gems | "
                + CHOSEN_SEED_PACK_AMOUNT + " seed packets of an unlocked plant of your choice (-t required)\n"
                + "5. Currency Exchange | " + EXCHANGE_PRICE_GEMS + " gems | "
                + EXCHANGE_COINS_GIVEN + " coins", true);
    }

    public Result handleShopDaily() {
        refreshDailyOffer();
        User user = userApp.getLoggedInUser();
        String status = hasBoughtToday(user) ? " [already purchased today]" : "";
        return new Result("Daily offer (" + dailyOfferDate + "):\n"
                + "6. Special Seed Pack: " + DAILY_OFFER_AMOUNT + "x " + dailyOfferPlant
                + " | 2000 coins -20% -> " + DAILY_OFFER_PRICE_COINS + " coins" + status, true);
    }

    public Result handleShopBuy(int itemId, int count, String plantType) {
        if (count < 1) {
            return new Result("Error: Count must be at least 1.", false);
        }
        switch (itemId) {
            case 1: return buyPots(count);
            case 2: return buyPlantFood(count);
            case 3: return buyRandomSeedPack(count);
            case 4: return buyChosenSeedPack(count, plantType);
            case 5: return buyCurrencyExchange(count);
            case 6: return buyDailyOffer(count);
            default: return new Result("Error: Item " + itemId + " doesn't exist.", false);
        }
    }

    private Result buyPots(int count) {
        User user = userApp.getLoggedInUser();
        if (greenhouseController.lockedPotCount(user) < count) {
            return new Result("Error: Only " + greenhouseController.lockedPotCount(user)
                    + " locked pot(s) remain.", false);
        }
        Result payment = pay(user, POT_PRICE_COINS * count, 0);
        if (payment != null) {
            return payment;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(greenhouseController.unlockNextPot(user).getMessage()).append("\n");
        }
        userApp.saveUsers();
        return new Result(sb.toString().trim(), true);
    }

    private Result buyPlantFood(int count) {
        User user = userApp.getLoggedInUser();
        int stored = user.getStoredStartingPlantFoods();
        if (stored + count > MAX_STORED_PLANT_FOODS) {
            return new Result("Error: You can store at most " + MAX_STORED_PLANT_FOODS
                    + " plant foods (you have " + stored + ").", false);
        }
        Result payment = pay(user, 0, PLANT_FOOD_PRICE_GEMS * count);
        if (payment != null) {
            return payment;
        }
        user.setStoredStartingPlantFoods(stored + count);
        userApp.saveUsers();
        return new Result("Bought " + count + " plant food(s). Stored: "
                + user.getStoredStartingPlantFoods(), true);
    }

    private Result buyRandomSeedPack(int count) {
        User user = userApp.getLoggedInUser();
        List<String> unlocked = collectionController.getUnlockedPlants(user);
        if (unlocked.isEmpty()) {
            return new Result("Error: You have no unlocked plants.", false);
        }
        Result payment = pay(user, RANDOM_SEED_PACK_PRICE_COINS * count, 0);
        if (payment != null) {
            return payment;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String plant = unlocked.get(random.nextInt(unlocked.size()));
            user.addSeedPackets(plant, RANDOM_SEED_PACK_AMOUNT);
            sb.append("Received ").append(RANDOM_SEED_PACK_AMOUNT).append(" seed packets of ")
                    .append(plant).append(".\n");
        }
        userApp.saveUsers();
        return new Result(sb.toString().trim(), true);
    }

    private Result buyChosenSeedPack(int count, String plantType) {
        User user = userApp.getLoggedInUser();
        if (plantType == null || plantType.isEmpty()) {
            return new Result("Error: Chosen seed packs need a plant type (-t <plant>).", false);
        }
        String properName = PlantFactory.properName(plantType);
        if (properName == null || !collectionController.isPlantUnlocked(properName)) {
            return new Result("Error: You can only buy seed packets of unlocked plants.", false);
        }
        Result payment = pay(user, 0, CHOSEN_SEED_PACK_PRICE_GEMS * count);
        if (payment != null) {
            return payment;
        }
        user.addSeedPackets(properName, CHOSEN_SEED_PACK_AMOUNT * count);
        userApp.saveUsers();
        return new Result("Received " + (CHOSEN_SEED_PACK_AMOUNT * count) + " seed packets of "
                + properName + ".", true);
    }

    private Result buyCurrencyExchange(int count) {
        User user = userApp.getLoggedInUser();
        Result payment = pay(user, 0, EXCHANGE_PRICE_GEMS * count);
        if (payment != null) {
            return payment;
        }
        user.setCoins(user.getCoins() + EXCHANGE_COINS_GIVEN * count);
        userApp.saveUsers();
        return new Result("Exchanged " + (EXCHANGE_PRICE_GEMS * count) + " gems for "
                + (EXCHANGE_COINS_GIVEN * count) + " coins. Coins: " + user.getCoins(), true);
    }

    private Result buyDailyOffer(int count) {
        refreshDailyOffer();
        User user = userApp.getLoggedInUser();
        if (count != 1) {
            return new Result("Error: The daily offer can only be bought once per day.", false);
        }
        if (hasBoughtToday(user)) {
            return new Result("Error: You already bought today's offer.", false);
        }
        Result payment = pay(user, DAILY_OFFER_PRICE_COINS, 0);
        if (payment != null) {
            return payment;
        }
        user.addSeedPackets(dailyOfferPlant, DAILY_OFFER_AMOUNT);
        dailyPurchaseByUser.put(user.getUsername(), dailyOfferDate);
        userApp.saveUsers();
        return new Result("Bought the daily offer: " + DAILY_OFFER_AMOUNT + " seed packets of "
                + dailyOfferPlant + ".", true);
    }

    /** Charges the user; returns an error Result if they cannot afford it, or null on success. */
    private Result pay(User user, int coins, int gems) {
        if (user.getCoins() < coins) {
            return new Result("Error: Not enough coins. This costs " + coins + " coins.", false);
        }
        if (user.getGems() < gems) {
            return new Result("Error: Not enough gems. This costs " + gems + " gems.", false);
        }
        user.setCoins(user.getCoins() - coins);
        user.setGems(user.getGems() - gems);
        return null;
    }

    private void refreshDailyOffer() {
        LocalDate today = LocalDate.now();
        if (today.equals(dailyOfferDate) && dailyOfferPlant != null) {
            return;
        }
        dailyOfferDate = today;
        List<String> unlocked = collectionController.getUnlockedPlants(userApp.getLoggedInUser());
        List<String> pool = unlocked.isEmpty() ? PlantFactory.getAllPlantNames() : unlocked;
        dailyOfferPlant = pool.get(new Random(today.toEpochDay()).nextInt(pool.size()));
    }

    private boolean hasBoughtToday(User user) {
        return dailyOfferDate != null && dailyOfferDate.equals(dailyPurchaseByUser.get(user.getUsername()));
    }
}
