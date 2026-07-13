package views;

import java.util.regex.Matcher;
import controllers.MenuRouter;
import controllers.TravelLogController;
import utils.Command;

public class TravelLogMenuView extends MenuView {

    private final TravelLogController controller;

    public TravelLogMenuView(MenuRouter router, TravelLogController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        Matcher matcher = Command.TRAVEL_LOG_PAGE.match(input);
        if (matcher != null) {
            String page = matcher.group("page");
            if (page.toLowerCase().startsWith("minigame")) {
                System.out.println("Mini-games: vasebreaker, bowling, izombie (stages 1-3)."
                        + " Start one with: play minigame -m <name> -s <stage>");
                print(controller.handleTravelLog("MINIGAME"));
            } else {
                print(controller.handleTravelLog(page.equalsIgnoreCase("all") ? null : page));
            }
            return true;
        }
        matcher = Command.PLAY_MINIGAME.match(input);
        if (matcher != null) {
            print(controller.handlePlayMiniGame(matcher.group("name"),
                    Integer.parseInt(matcher.group("stage"))));
            return true;
        }
        if (Command.TRAVEL_LOG_CLAIM.match(input) != null) {
            print(controller.claimCompletedQuests());
            return true;
        }
        return false;
    }
}
