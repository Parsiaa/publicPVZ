package controllers;

import models.Enums.Menu;
import models.User;
import utils.Result;
import utils.UserApp;

public class MainMenuController {
    private UserApp userApp;
    private MenuRouter router;

    public MainMenuController(UserApp userApp, MenuRouter router) {
        this.userApp = userApp;
        this.router = router;
    }

    public Result handleMenuEnter(String menuName) {
        if (userApp.getLoggedInUser() == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        switch (menuName.toLowerCase()) {
            case "game":
                router.navigateTo(Menu.GameMenu);
                return new Result("Entered Game Menu.", true);
            case "settings":
                router.navigateTo(Menu.SettingsMenu);
                return new Result("Entered Settings Menu.", true);
            case "network":
                router.navigateTo(Menu.NetworkMenu);
                return new Result("Entered Network Menu.", true);
            case "news":
                router.navigateTo(Menu.NewsMenu);
                return new Result("Entered News Menu.", true);
            case "profile":
                router.navigateTo(Menu.ProfileMenu);
                return new Result("Entered Profile Menu.", true);
            default:
                return new Result("Error: Unknown menu '" + menuName + "'.", false);
        }
    }

    public Result handleLogout() {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: No user is logged in.", false);
        }
        user.setStayLoggedIn(false);
        userApp.logout();
        userApp.saveUsers();
        router.navigateTo(Menu.SignUpMenu);
        return new Result("Logged out successfully. Returned to SignUp Menu.", true);
    }
}
