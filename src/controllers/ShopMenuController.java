package controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import models.User;
import utils.Result;
import utils.UserApp;

public class ShopMenuController {
    private UserApp userApp;
    private LocalDate dailyOfferDate;
    private String dailyOfferItem;
    private static final List<String> ITEMS = Arrays.asList("PlantFood", "PotUnlock", "GemPack", "CoinPack", "PlantBoost");
    private static final List<Integer> COIN_PRICES = Arrays.asList(150, 400, -1, -1, 100);
    private static final List<Integer> GEM_PRICES = Arrays.asList(2, 5, -1, 10, 1);

    public ShopMenuController(UserApp userApp) {
        this.userApp = userApp;
        refreshDailyOffer();
    }

    public Result handleShopList() {
        StringBuilder sb = new StringBuilder("Shop items:\n");
        for (int i = 0; i < ITEMS.size(); i++) {
            sb.append("- ").append(ITEMS.get(i));
            if (COIN_PRICES.get(i) > 0) {
                sb.append(" | ").append(COIN_PRICES.get(i)).append(" coins");
            }
            if (GEM_PRICES.get(i) > 0) {
                sb.append(" | ").append(GEM_PRICES.get(i)).append(" gems");
            }
            sb.append("\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result handleShopDaily() {
        refreshDailyOffer();
        int index = ITEMS.indexOf(dailyOfferItem);
        int coinPrice = COIN_PRICES.get(index) > 0 ? COIN_PRICES.get(index) / 2 : -1;
        int gemPrice = GEM_PRICES.get(index) > 0 ? Math.max(1, GEM_PRICES.get(index) / 2) : -1;
        StringBuilder sb = new StringBuilder("Daily offer (50% off): " + dailyOfferItem);
        if (coinPrice > 0) {
            sb.append(" | ").append(coinPrice).append(" coins");
        }
        if (gemPrice > 0) {
            sb.append(" | ").append(gemPrice).append(" gems");
        }
        return new Result(sb.toString(), true);
    }

    public Result handleBuyItem(String itemName, int amount, String currency) {
        User user = userApp.getLoggedInUser();
        if (amount < 1) {
            return new Result("Error: Amount must be at least 1.", false);
        }
        int index = indexOf(itemName);
        if (index == -1) {
            return new Result("Error: Item '" + itemName + "' doesn't exist.", false);
        }
        refreshDailyOffer();
        boolean isDaily = ITEMS.get(index).equals(dailyOfferItem);
        int unitPrice;
        boolean useCoins;
        if (currency.equalsIgnoreCase("coins")) {
            unitPrice = COIN_PRICES.get(index);
            useCoins = true;
        } else if (currency.equalsIgnoreCase("gems")) {
            unitPrice = GEM_PRICES.get(index);
            useCoins = false;
        } else {
            return new Result("Error: Currency must be 'coins' or 'gems'.", false);
        }
        if (unitPrice < 0) {
            return new Result("Error: This item can't be bought with " + currency.toLowerCase() + ".", false);
        }
        if (isDaily) {
            unitPrice = Math.max(1, unitPrice / 2);
        }
        int total = unitPrice * amount;
        if (useCoins) {
            if (user.getCoins() < total) {
                return new Result("Error: Not enough coins. Total cost: " + total + ".", false);
            }
            user.setCoins(user.getCoins() - total);
        } else {
            if (user.getGems() < total) {
                return new Result("Error: Not enough gems. Total cost: " + total + ".", false);
            }
            user.setGems(user.getGems() - total);
        }
        applyItem(user, ITEMS.get(index), amount);
        userApp.saveUsers();
        return new Result("Bought " + amount + "x " + ITEMS.get(index) + " for " + total + " " + currency.toLowerCase() + ".", true);
    }

    private void applyItem(User user, String item, int amount) {
        switch (item) {
            case "PlantFood":
                user.setStoredStartingPlantFoods(user.getStoredStartingPlantFoods() + amount);
                break;
            case "GemPack":
                user.setGems(user.getGems() + 10 * amount);
                break;
            case "CoinPack":
                user.setCoins(user.getCoins() + 500 * amount);
                break;
            default:
                break;
        }
    }

    private void refreshDailyOffer() {
        LocalDate today = LocalDate.now();
        if (dailyOfferDate == null || !dailyOfferDate.equals(today)) {
            dailyOfferDate = today;
            dailyOfferItem = ITEMS.get((int) (today.toEpochDay() % ITEMS.size()));
        }
    }

    private int indexOf(String itemName) {
        for (int i = 0; i < ITEMS.size(); i++) {
            if (ITEMS.get(i).equalsIgnoreCase(itemName)) {
                return i;
            }
        }
        return -1;
    }
}
