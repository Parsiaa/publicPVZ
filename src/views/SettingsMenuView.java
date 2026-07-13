package views;

import java.util.regex.Matcher;
import controllers.MenuRouter;
import controllers.SettingsMenuController;
import utils.Command;

public class SettingsMenuView extends MenuView {

    private final SettingsMenuController controller;

    public SettingsMenuView(MenuRouter router, SettingsMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        Matcher matcher = Command.CHANGE_DIFFICULTY.match(input);
        if (matcher != null) {
            print(controller.changeDifficultyLevel(Integer.parseInt(matcher.group("level"))));
            return true;
        }
        return false;
    }
}
