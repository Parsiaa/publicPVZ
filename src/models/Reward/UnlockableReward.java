package models.Reward;

import models.User;

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
    public void addReward(User user) {
        if ("plant".equalsIgnoreCase(currentType)) {
            user.unlockPlant(targetName);
        } else if ("boost".equalsIgnoreCase(currentType)) {
            user.addBoostFor(targetName);
        } else if ("level".equalsIgnoreCase(currentType)) {
            try {
                int level = Integer.parseInt(targetName);
                user.setHighestLevel(Math.max(user.getHighestLevel(), level));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
