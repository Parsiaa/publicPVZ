package controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import models.User;
import utils.Result;
import utils.UserApp;

public class ShopMenuController {
    private UserApp userApp;
    private GreenhouseMenuController greenhouseController;
    private CollectionMenuController collectionController;

    // In-memory seed-packet inventory (username -> plant -> packet count).
    private java.util.Map<String, java.util.Map<String, Integer>> seedPacketsByUser;
    // Tracks the last day each user redeemed the daily offer, so it is buyable once per day.
    private java.util.Map<String, LocalDate> dailyPurchasedByUser;
    private LocalDate dailyOfferDate;

    // Item ids used by "shop buy -i <item_id>".
    private static final int ITEM_POT = 1;
    private static final int ITEM_PLANT_FOOD = 2;
    private static final int ITEM_RANDOM_PACKET = 3;
    private static final int ITEM_CHOSEN_PACKET = 4;
    private static final int ITEM_CURRENCY_CONVERSION = 5;
    private static final int ITEM_DAILY_OFFER = 6;

    // Prices and quantities per the doc's shop table.
    private static final int POT_COST_COINS = 2000;
    private static final int PLANT_FOOD_COST_GEMS = 3;
    private static final int MAX_PLANT_FOOD = 3;
    private static final int RANDOM_PACKET_COST_COINS = 1000;
    private static final int RANDOM_PACKET_AMOUNT = 5;
    private static final int CHOSEN_PACKET_COST_GEMS = 10;
    private static final int CHOSEN_PACKET_AMOUNT = 5;
    private static final int CONVERSION_COST_GEMS = 5;
    private static final int CONVERSION_GAIN_COINS = 500;
    private static final int DAILY_OFFER_COST_COINS = 1600;
    private static final int DAILY_OFFER_AMOUNT = 10;

    public ShopMenuController(UserApp userApp, GreenhouseMenuController greenhouseController,
                             CollectionMenuController collectionController) {
        this.userApp = userApp;
        this.greenhouseController = greenhouseController;
        this.collectionController = collectionController;
        this.seedPacketsByUser = new HashMap<>();
        this.dailyPurchasedByUser = new HashMap<>();
        refreshDailyOffer();
    }

    public Result handleShopList() {
        StringBuilder sb = new StringBuilder("Permanent shop items:\n");
        sb.append(ITEM_POT).append(". Pot | ").append(POT_COST_COINS)
                .append(" coins | unlocks one greenhouse pot (max 20)\n");
        sb.append(ITEM_PLANT_FOOD).append(". Plant Food | ").append(PLANT_FOOD_COST_GEMS)
                .append(" gems | starting plant food for a level (max ").append(MAX_PLANT_FOOD).append(" stored)\n");
        sb.append(ITEM_RANDOM_PACKET).append(". Random Seed Packet | ").append(RANDOM_PACKET_COST_COINS)
                .append(" coins | ").append(RANDOM_PACKET_AMOUNT).append(" packets for a random unlocked plant\n");
        sb.append(ITEM_CHOSEN_PACKET).append(". Chosen Seed Packet | ").append(CHOSEN_PACKET_COST_GEMS)
                .append(" gems | ").append(CHOSEN_PACKET_AMOUNT).append(" packets for a chosen unlocked plant (-t required)\n");
        sb.append(ITEM_CURRENCY_CONVERSION).append(". Currency Conversion | ").append(CONVERSION_COST_GEMS)
                .append(" gems -> ").append(CONVERSION_GAIN_COINS).append(" coins\n");
        return new Result(sb.toString().trim(), true);
    }

    public Result handleShopDaily() {
        refreshDailyOffer();
        User user = userApp.getLoggedInUser();
        String plant = dailyOfferPlant(user);
        StringBuilder sb = new StringBuilder("Daily offer (" + dailyOfferDate + "):\n");
        sb.append(ITEM_DAILY_OFFER).append(". Special Seed Packet");
        if (plant != null) {
            sb.append(" for ").append(plant);
        }
        sb.append(" | ").append(DAILY_OFFER_COST_COINS).append(" coins (20% off) | ")
                .append(DAILY_OFFER_AMOUNT).append(" packets");
        if (alreadyBoughtDaily(user)) {
            sb.append("\n(Already purchased today.)");
        }
        return new Result(sb.toString(), true);
    }

    public Result handleBuyItem(int itemId, int count, String plantType) {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        if (count < 1) {
            return new Result("Error: Count must be at least 1.", false);
        }
        switch (itemId) {
            case ITEM_POT:
                return buyPots(user, count);
            case ITEM_PLANT_FOOD:
                return buyPlantFood(user, count);
            case ITEM_RANDOM_PACKET:
                return buyRandomPackets(user, count);
            case ITEM_CHOSEN_PACKET:
                return buyChosenPackets(user, count, plantType);
            case ITEM_CURRENCY_CONVERSION:
                return convertCurrency(user, count);
            case ITEM_DAILY_OFFER:
                return buyDailyOffer(user, count);
            default:
                return new Result("Error: Item id '" + itemId + "' doesn't exist.", false);
        }
    }

    private Result buyPots(User user, int count) {
        int available = greenhouseController.lockedPotCount(user);
        if (available == 0) {
            return new Result("Error: All greenhouse pots are already unlocked.", false);
        }
        if (count > available) {
            return new Result("Error: Only " + available + " locked pot(s) remain.", false);
        }
        int total = POT_COST_COINS * count;
        if (user.getCoins() < total) {
            return new Result("Error: Not enough coins. Total cost: " + total + " coins.", false);
        }
        user.setCoins(user.getCoins() - total);
        for (int i = 0; i < count; i++) {
            greenhouseController.unlockNextPot(user);
        }
        userApp.saveUsers();
        return new Result("Unlocked " + count + " greenhouse pot(s) for " + total + " coins.", true);
    }

    private Result buyPlantFood(User user, int count) {
        if (user.getStoredStartingPlantFoods() + count > MAX_PLANT_FOOD) {
            return new Result("Error: You can store at most " + MAX_PLANT_FOOD
                    + " plant food (you have " + user.getStoredStartingPlantFoods() + ").", false);
        }
        int total = PLANT_FOOD_COST_GEMS * count;
        if (user.getGems() < total) {
            return new Result("Error: Not enough gems. Total cost: " + total + " gems.", false);
        }
        user.setGems(user.getGems() - total);
        user.setStoredStartingPlantFoods(user.getStoredStartingPlantFoods() + count);
        userApp.saveUsers();
        return new Result("Bought " + count + " plant food for " + total + " gems.", true);
    }

    private Result buyRandomPackets(User user, int count) {
        List<String> unlocked = collectionController.getUnlockedPlants(user);
        if (unlocked.isEmpty()) {
            return new Result("Error: You have no unlocked plants to receive packets for.", false);
        }
        int total = RANDOM_PACKET_COST_COINS * count;
        if (user.getCoins() < total) {
            return new Result("Error: Not enough coins. Total cost: " + total + " coins.", false);
        }
        user.setCoins(user.getCoins() - total);
        Random random = new Random();
        StringBuilder detail = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String plant = unlocked.get(random.nextInt(unlocked.size()));
            addPackets(user, plant, RANDOM_PACKET_AMOUNT);
            if (detail.length() > 0) {
                detail.append(", ");
            }
            detail.append(RANDOM_PACKET_AMOUNT).append("x ").append(plant);
        }
        userApp.saveUsers();
        return new Result("Bought random seed packets for " + total + " coins: " + detail + ".", true);
    }

    private Result buyChosenPackets(User user, int count, String plantType) {
        if (plantType == null || plantType.trim().isEmpty()) {
            return new Result("Error: A plant type (-t) is required for chosen seed packets.", false);
        }
        if (!collectionController.isPlantUnlocked(plantType)) {
            return new Result("Error: '" + plantType + "' is not unlocked (or doesn't exist).", false);
        }
        int total = CHOSEN_PACKET_COST_GEMS * count;
        if (user.getGems() < total) {
            return new Result("Error: Not enough gems. Total cost: " + total + " gems.", false);
        }
        user.setGems(user.getGems() - total);
        addPackets(user, plantType, CHOSEN_PACKET_AMOUNT * count);
        userApp.saveUsers();
        return new Result("Bought " + (CHOSEN_PACKET_AMOUNT * count) + " seed packet(s) for "
                + plantType + " for " + total + " gems.", true);
    }

    private Result convertCurrency(User user, int count) {
        int totalGems = CONVERSION_COST_GEMS * count;
        if (user.getGems() < totalGems) {
            return new Result("Error: Not enough gems. Total cost: " + totalGems + " gems.", false);
        }
        int gainedCoins = CONVERSION_GAIN_COINS * count;
        user.setGems(user.getGems() - totalGems);
        user.setCoins(user.getCoins() + gainedCoins);
        userApp.saveUsers();
        return new Result("Converted " + totalGems + " gems into " + gainedCoins + " coins.", true);
    }

    private Result buyDailyOffer(User user, int count) {
        if (count != 1) {
            return new Result("Error: The daily offer can only be bought once per day.", false);
        }
        refreshDailyOffer();
        if (alreadyBoughtDaily(user)) {
            return new Result("Error: You already bought today's daily offer.", false);
        }
        String plant = dailyOfferPlant(user);
        if (plant == null) {
            return new Result("Error: You have no unlocked plants for the daily offer.", false);
        }
        if (user.getCoins() < DAILY_OFFER_COST_COINS) {
            return new Result("Error: Not enough coins. The daily offer costs "
                    + DAILY_OFFER_COST_COINS + " coins.", false);
        }
        user.setCoins(user.getCoins() - DAILY_OFFER_COST_COINS);
        addPackets(user, plant, DAILY_OFFER_AMOUNT);
        dailyPurchasedByUser.put(user.getUsername(), dailyOfferDate);
        userApp.saveUsers();
        return new Result("Bought the daily offer: " + DAILY_OFFER_AMOUNT + " seed packets for "
                + plant + " (" + DAILY_OFFER_COST_COINS + " coins).", true);
    }

    public int getSeedPacketCount(User user, String plantName) {
        java.util.Map<String, Integer> inventory = seedPacketsByUser.get(user.getUsername());
        if (inventory == null) {
            return 0;
        }
        return inventory.getOrDefault(plantName, 0);
    }

    private void addPackets(User user, String plant, int amount) {
        seedPacketsByUser
                .computeIfAbsent(user.getUsername(), k -> new HashMap<>())
                .merge(plant, amount, Integer::sum);
    }

    private void refreshDailyOffer() {
        LocalDate today = LocalDate.now();
        if (dailyOfferDate == null || !dailyOfferDate.equals(today)) {
            dailyOfferDate = today;
        }
    }

    private boolean alreadyBoughtDaily(User user) {
        return dailyOfferDate.equals(dailyPurchasedByUser.get(user.getUsername()));
    }

    // Deterministic per-day pick from the user's unlocked plants.
    private String dailyOfferPlant(User user) {
        List<String> unlocked = collectionController.getUnlockedPlants(user);
        if (unlocked.isEmpty()) {
            return null;
        }
        int index = (int) Math.floorMod(dailyOfferDate.toEpochDay(), unlocked.size());
        return unlocked.get(index);
    }
}
