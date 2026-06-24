package models.Reward;

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
    public void addReward() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addReward'");
    }
    
}
