package views;

import java.util.regex.Matcher;
import controllers.MenuRouter;
import controllers.ProfileMenuController;
import utils.Command;

public class ProfileMenuView extends MenuView {

    private final ProfileMenuController controller;

    public ProfileMenuView(MenuRouter router, ProfileMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        Matcher matcher = Command.PROFILE_CHANGE_USERNAME.match(input);
        if (matcher != null) {
            print(controller.changeUsername(matcher.group("username")));
            return true;
        }
        matcher = Command.PROFILE_CHANGE_NICKNAME.match(input);
        if (matcher != null) {
            print(controller.changeNickname(matcher.group("nickname")));
            return true;
        }
        matcher = Command.PROFILE_CHANGE_EMAIL.match(input);
        if (matcher != null) {
            print(controller.changeEmail(matcher.group("email")));
            return true;
        }
        matcher = Command.PROFILE_CHANGE_PASSWORD.match(input);
        if (matcher != null) {
            print(controller.changePassword(matcher.group("newpass"), matcher.group("oldpass")));
            return true;
        }
        if (Command.PROFILE_SHOW_INFO.match(input) != null) {
            print(controller.getUserInfo());
            return true;
        }
        return false;
    }
}
