package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Quest;
import models.User;
import utils.Result;
import utils.UserApp;

public class TravelLogController {
    private UserApp userApp;

    public TravelLogController(UserApp userApp) {
        this.userApp = userApp;
    }

    public Result handleTravelLog(String filter) {
        User user = userApp.getLoggedInUser();
        List<Quest> quests = questsOf(user);
        if (quests.isEmpty()) {
            return new Result("Your travel log is empty.", true);
        }
        StringBuilder sb = new StringBuilder("Travel log:\n");
        boolean any = false;
        for (Quest quest : quests) {
            if (filter != null && !filter.isEmpty()
                    && !quest.getCategory().name().equalsIgnoreCase(filter)) {
                continue;
            }
            sb.append("- [").append(quest.getCategory()).append("/").append(quest.getPriority()).append("] ")
                    .append(quest.getTitle()).append(" (")
                    .append(quest.getCurrentProgress()).append("/").append(quest.getTargetProgress()).append(")")
                    .append(quest.checkCompletion() ? " COMPLETED" : "")
                    .append("\n");
            any = true;
        }
        if (!any) {
            return new Result("No quests match the filter '" + filter + "'.", true);
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result updateQuestProgress(String questId, int progress) {
        User user = userApp.getLoggedInUser();
        for (Quest quest : questsOf(user)) {
            if (quest.getId().equals(questId)) {
                quest.updateProgress(progress);
                if (quest.checkCompletion()) {
                    return new Result("Quest '" + quest.getTitle() + "' completed! Claim your reward.", true);
                }
                return new Result("Progress updated: " + quest.getCurrentProgress() + "/" + quest.getTargetProgress() + ".", true);
            }
        }
        return new Result("Error: Quest '" + questId + "' not found.", false);
    }

    public Result claimCompletedQuests() {
        User user = userApp.getLoggedInUser();
        List<Quest> quests = questsOf(user);
        List<Quest> claimed = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.checkCompletion()) {
                quest.claimReward(user);
                claimed.add(quest);
            }
        }
        if (claimed.isEmpty()) {
            return new Result("Error: No completed quests to claim.", false);
        }
        quests.removeAll(claimed);
        userApp.saveUsers();
        return new Result("Claimed " + claimed.size() + " quest reward(s)!", true);
    }

    private List<Quest> questsOf(User user) {
        if (user.getActiveQuests() == null) {
            user.setActiveQuests(new ArrayList<>());
        }
        return user.getActiveQuests();
    }
}
