package controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import models.User;
import utils.UserApp;

public class LeaderboardController {
    private UserApp userApp;
    private Comparator<User> currentSort;

    public LeaderboardController(UserApp userApp) {
        this.userApp = userApp;
        this.currentSort = Comparator.comparingInt(User::getHighestLevel).reversed();
    }

    public String displayLeaderboard() {
        List<User> users = new ArrayList<>(userApp.getUser().values());
        users.sort(currentSort);
        StringBuilder sb = new StringBuilder("Leaderboard:\n");
        int rank = 1;
        for (User user : users) {
            sb.append(rank++).append(". ").append(user.getUsername())
                    .append(" | Level: ").append(user.getHighestLevel())
                    .append(" | Minigames: ").append(user.getMiniGamesCompleted())
                    .append(" | Quests: ").append(user.getDailyQuestsCompleted() + user.getNotDailyQuestsCompleted())
                    .append(" | Meow Points: ").append(user.getHighestMeowPointScore())
                    .append("\n");
            if (rank > 10) {
                break;
            }
        }
        return sb.toString().trim();
    }

    public void sortByHighestLevel(boolean ascending) {
        Comparator<User> c = Comparator.comparingInt(User::getHighestLevel);
        currentSort = ascending ? c : c.reversed();
    }

    public void sortByMiniGames(boolean ascending) {
        Comparator<User> c = Comparator.comparingInt(User::getMiniGamesCompleted);
        currentSort = ascending ? c : c.reversed();
    }

    public void sortByQuests(boolean ascending) {
        Comparator<User> c = Comparator.comparingInt(u -> u.getDailyQuestsCompleted() + u.getNotDailyQuestsCompleted());
        currentSort = ascending ? c : c.reversed();
    }

    public void sortByMeowpoints(boolean ascending) {
        Comparator<User> c = Comparator.comparingInt(User::getHighestMeowPointScore);
        currentSort = ascending ? c : c.reversed();
    }
}
