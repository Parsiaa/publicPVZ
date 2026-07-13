package views;

import java.util.regex.Matcher;
import controllers.LeaderboardController;
import controllers.MenuRouter;
import utils.Command;

public class LeaderboardMenuView extends MenuView {

    private final LeaderboardController controller;

    public LeaderboardMenuView(MenuRouter router, LeaderboardController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        if (Command.SHOW_LEADERBOARD.match(input) != null) {
            System.out.println(controller.displayLeaderboard());
            return true;
        }
        Matcher matcher = Command.LEADERBOARD_SORT.match(input);
        if (matcher != null) {
            boolean ascending = matcher.group("order").equalsIgnoreCase("asc");
            switch (matcher.group("column").toLowerCase()) {
                case "level": controller.sortByHighestLevel(ascending); break;
                case "minigames": controller.sortByMiniGames(ascending); break;
                case "quests": controller.sortByQuests(ascending); break;
                default: controller.sortByMeowpoints(ascending); break;
            }
            System.out.println(controller.displayLeaderboard());
            return true;
        }
        return false;
    }
}
