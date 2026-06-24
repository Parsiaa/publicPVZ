package models.Reward;

public class CurrencyReward implements Reward {
    private String currentType;
    private int amount;

    public CurrencyReward(String currentType, int amount) {
        this.currentType = currentType;
        this.amount = amount;
    }

    public String getCurrentType() { return currentType;}
    public void setCurrentType(String currentType) { this.currentType = currentType; }
    
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }



    @Override
    public void addReward() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addReward'");
    }
}
