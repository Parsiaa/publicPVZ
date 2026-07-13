package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import models.User;
import utils.Result;
import utils.UserApp;

public class NewsMenuController {
    private UserApp userApp;
    private java.util.Map<String, Set<Integer>> readNewsByUser;
    private static final List<String> NEWS = Arrays.asList(
            "New chapter 'Frostbite Caves' is now available!",
            "Daily shop offers refresh every day at midnight.",
            "Boost your plants before a match for extra power.",
            "Leaderboards now track Meow Points!");

    public NewsMenuController(UserApp userApp) {
        this.userApp = userApp;
        this.readNewsByUser = new HashMap<>();
    }

    public Result showAllNews() {
        StringBuilder sb = new StringBuilder("All news:\n");
        Set<Integer> read = readFor(userApp.getLoggedInUser());
        for (int i = 0; i < NEWS.size(); i++) {
            sb.append(i + 1).append(". ").append(read.contains(i) ? "[read] " : "[new] ")
                    .append(NEWS.get(i)).append("\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result showUnreadNews() {
        StringBuilder sb = new StringBuilder("Unread news:\n");
        Set<Integer> read = readFor(userApp.getLoggedInUser());
        boolean any = false;
        for (int i = 0; i < NEWS.size(); i++) {
            if (!read.contains(i)) {
                sb.append(i + 1).append(". ").append(NEWS.get(i)).append("\n");
                read.add(i);
                any = true;
            }
        }
        if (!any) {
            return new Result("You're all caught up!", true);
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result markNewsAsRead(int newsNumber) {
        if (newsNumber < 1 || newsNumber > NEWS.size()) {
            return new Result("Error: News item " + newsNumber + " doesn't exist.", false);
        }
        Set<Integer> read = readFor(userApp.getLoggedInUser());
        if (!read.add(newsNumber - 1)) {
            return new Result("Error: News item is already marked as read.", false);
        }
        return new Result("News item " + newsNumber + " marked as read.", true);
    }

    private Set<Integer> readFor(User user) {
        return readNewsByUser.computeIfAbsent(user.getUsername(), k -> new HashSet<>());
    }
}
