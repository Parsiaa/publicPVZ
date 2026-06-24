package models.Reward;

public class UnlockableReward implements Reward {
    private String currentType;
    private String targetName;

    public UnlockableReward(String currentType, String targetName) {
        this.currentType = currentType;
        this.targetName = targetName;
    }

    public String getCurrentType() { return currentType; }
    public void setCurrentType(String currentType) { this.currentType = currentType; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    @Override
    public void addReward() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addReward'");
    }
    
}
