package views;

import java.util.regex.Matcher;
import controllers.LoginMenuController;
import controllers.MenuRouter;
import models.Enums.Menu;
import utils.Command;
import utils.Result;

public class LoginMenuView extends MenuView {

    private final LoginMenuController controller;

    public LoginMenuView(MenuRouter router, LoginMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        Matcher matcher = Command.LOGIN.match(input);
        if (matcher != null) {
            Result result = controller.handleLoginAccount(matcher.group("username"),
                    matcher.group("password"), matcher.group("stay") != null);
            print(result);
            if (result.isSuccess()) {
                router.navigateTo(Menu.MainMenu);
            }
            return true;
        }
        matcher = Command.FORGET_PASSWORD.match(input);
        if (matcher != null) {
            print(controller.handleForgotPassword(matcher.group("username"), matcher.group("email")));
            return true;
        }
        matcher = Command.ANSWER.match(input);
        if (matcher != null) {
            print(controller.handleSecurityAnswer(matcher.group("answer")));
            return true;
        }
        matcher = Command.SET_NEW_PASSWORD.match(input);
        if (matcher != null) {
            print(controller.handleSetNewPassword(matcher.group("password")));
            return true;
        }
        return false;
    }
}
