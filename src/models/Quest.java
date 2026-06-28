package models;

import models.Enums.QuestCategory;
import models.Enums.QuestPriority;
import models.Reward.Reward;

public class Quest {
    private String id;
    private String title;
    private QuestPriority priority;
    private QuestCategory category;
    private Reward reward;
    private String conditionType;
    private int currentProgress;
    private int targetProgress;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public QuestPriority getPriority() {
        return priority;
    }

    public void setPriority(QuestPriority priority) {
        this.priority = priority;
    }

    public QuestCategory getCategory() {
        return category;
    }

    public void setCategory(QuestCategory category) {
        this.category = category;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(int targetProgress) {
        this.targetProgress = targetProgress;
    }


    public Quest(String id, String title, QuestPriority priority, QuestCategory category,
                 Reward reward, String conditionType, int currentProgress, int targetProgress) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.reward = reward;
        this.conditionType = conditionType;
        this.currentProgress = currentProgress;
        this.targetProgress = targetProgress;
    }



    public void updateProgress(int progress) {
        //TODO
    }

    public boolean checkCompletion() {
        //TODO
        return false;
    }

    public void claimReward(User user) {
        //TODO
    }


}
