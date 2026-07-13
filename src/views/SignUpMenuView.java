package views;

import java.util.regex.Matcher;
import controllers.MenuRouter;
import controllers.SignUpMenuController;
import models.Enums.Menu;
import utils.Command;
import utils.Result;

public class SignUpMenuView extends MenuView {

    private final SignUpMenuController controller;

    public SignUpMenuView(MenuRouter router, SignUpMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        Matcher matcher = Command.REGISTER.match(input);
        if (matcher != null) {
            print(controller.handleCreateAccount(matcher.group("username"), matcher.group("password"),
                    matcher.group("confirm"), matcher.group("nickname"),
                    matcher.group("email"), matcher.group("gender")));
            return true;
        }
        matcher = Command.PICK_QUESTION.match(input);
        if (matcher != null) {
            Result result = controller.handlePickQuestion(Integer.parseInt(matcher.group("number")),
                    matcher.group("answer"), matcher.group("confirm"));
            print(result);
            if (result.isSuccess()) {
                router.navigateTo(Menu.LoginMenu);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void handleMenuEnter(String menuName) {
        if (menuName.equalsIgnoreCase("login")) {
            router.navigateTo(Menu.LoginMenu);
            print(new Result("Entered Login Menu.", true));
        } else {
            super.handleMenuEnter(menuName);
        }
    }
}
