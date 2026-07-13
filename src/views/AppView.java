package views;

import java.util.Map;
import java.util.Scanner;
import controllers.MenuRouter;
import models.Enums.Menu;

/**
 * The main console loop: reads one line at a time and hands it to the view
 * of whatever menu the router currently points at.
 */
public class AppView {

    private final MenuRouter router;
    private final Map<Menu, MenuView> viewsByMenu;

    public AppView(MenuRouter router, Map<Menu, MenuView> viewsByMenu) {
        this.router = router;
        this.viewsByMenu = viewsByMenu;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Plants vs. Zombies 2!");
        while (!router.isExit() && scanner.hasNextLine()) {
            String line = scanner.nextLine();
            MenuView view = viewsByMenu.get(router.getCurrentMenu());
            if (view != null) {
                view.handle(line);
            } else {
                System.out.println("invalid command");
            }
        }
    }
}
