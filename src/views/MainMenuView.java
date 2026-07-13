package views;

import controllers.GameMenuController;
import controllers.MainMenuController;
import controllers.MenuRouter;
import utils.Command;

public class MainMenuView extends MenuView {

    private final MainMenuController controller;
    private final GameMenuController gameMenuController;

    public MainMenuView(MenuRouter router, MainMenuController controller,
                        GameMenuController gameMenuController) {
        super(router);
        this.controller = controller;
        this.gameMenuController = gameMenuController;
    }

    @Override
    protected boolean handleCommand(String input) {
        if (Command.MENU_LOGOUT.match(input) != null) {
            print(controller.handleLogout());
            return true;
        }
        if (Command.SCORE_GAME.match(input) != null) {
            print(gameMenuController.handleStartScoreGame());
            return true;
        }
        return false;
    }

    @Override
    protected void handleMenuEnter(String menuName) {
        print(controller.handleMenuEnter(menuName));
    }
}
