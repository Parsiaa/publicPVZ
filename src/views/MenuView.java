package views;

import java.util.regex.Matcher;
import controllers.MenuRouter;
import utils.Command;
import utils.Result;

/**
 * Base class of every console menu. Each view translates one line of user
 * input into a controller call. The global navigation commands
 * (menu enter / menu exit / menu show current) work in every menu.
 */
public abstract class MenuView {

    protected final MenuRouter router;

    protected MenuView(MenuRouter router) {
        this.router = router;
    }

    public final void handle(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if (handleCommand(trimmed)) {
            return;
        }
        Matcher enter = Command.MENU_ENTER.match(trimmed);
        if (enter != null) {
            handleMenuEnter(enter.group("name"));
            return;
        }
        if (Command.MENU_EXIT.match(trimmed) != null) {
            print(router.menuExit());
            return;
        }
        if (Command.MENU_SHOW_CURRENT.match(trimmed) != null) {
            print(router.showCurrent());
            return;
        }
        System.out.println("invalid command");
    }

    /** Handles the commands specific to this menu; returns false if the input is not one of them. */
    protected abstract boolean handleCommand(String input);

    /** Called for "menu enter <name>"; menus override this with their reachable targets. */
    protected void handleMenuEnter(String menuName) {
        print(new Result("Error: You cannot enter '" + menuName + "' from this menu.", false));
    }

    protected void print(Result result) {
        if (result != null) {
            System.out.println(result.getMessage());
        }
    }
}
