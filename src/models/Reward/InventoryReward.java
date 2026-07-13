package models.Reward;

import models.User;

public class InventoryReward implements Reward {
    private String itemType;
    private int amount;

    public InventoryReward(String itemType, int amount) {
        this.itemType = itemType;
        this.amount = amount;
    }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    @Override
    public void addReward(User user) {
        if ("plantfood".equalsIgnoreCase(itemType)) {
            user.setStoredStartingPlantFoods(user.getStoredStartingPlantFoods() + amount);
        } else if ("coins".equalsIgnoreCase(itemType)) {
            user.setCoins(user.getCoins() + amount);
        } else if ("gems".equalsIgnoreCase(itemType) || "diamond".equalsIgnoreCase(itemType)) {
            user.setGems(user.getGems() + amount);
        } else {
            user.addSeedPackets(itemType, amount);
        }
    }
}
