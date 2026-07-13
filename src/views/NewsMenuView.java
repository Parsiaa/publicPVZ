package views;

import controllers.MenuRouter;
import controllers.NewsMenuController;
import utils.Command;

public class NewsMenuView extends MenuView {

    private final NewsMenuController controller;

    public NewsMenuView(MenuRouter router, NewsMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        if (Command.NEWS_SHOW_UNREAD.match(input) != null) {
            print(controller.showUnreadNews());
            return true;
        }
        if (Command.NEWS_SHOW_ALL.match(input) != null) {
            print(controller.showAllNews());
            return true;
        }
        return false;
    }
}
