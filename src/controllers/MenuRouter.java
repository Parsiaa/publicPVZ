package controllers;

import models.Enums.Menu;
import utils.Result;

public class MenuRouter {
    private Menu currentMenu;

    public MenuRouter() {
        this.currentMenu = Menu.SignUpMenu;
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public void navigateTo(Menu menu) {
        this.currentMenu = menu;
    }

    public Result showCurrent() {
        return new Result("Current menu: " + currentMenu.name(), true);
    }

    public Result menuExit() {
        switch (currentMenu) {
            case SignUpMenu:
                currentMenu = Menu.Exit;
                return new Result("Goodbye!", true);
            case LoginMenu:
                currentMenu = Menu.SignUpMenu;
                return new Result("Returned to SignUp Menu.", true);
            case MainMenu:
                return new Result("Error: Use 'menu logout' to exit the Main Menu.", false);
            case GameMenu:
            case SettingsMenu:
            case NetworkMenu:
            case NewsMenu:
            case ProfileMenu:
                currentMenu = Menu.MainMenu;
                return new Result("Returned to Main Menu.", true);
            case CollectionMenu:
            case GreenhouseMenu:
            case TravelLogMenu:
            case LeaderboardMenu:
            case PlantSelectionMenu:
                currentMenu = Menu.GameMenu;
                return new Result("Returned to Game Menu.", true);
            case ShopMenu:
                currentMenu = Menu.GreenhouseMenu;
                return new Result("Returned to Greenhouse Menu.", true);
            default:
                return new Result("Error: Cannot exit from this menu.", false);
        }
    }

    public boolean isExit() {
        return currentMenu == Menu.Exit;
    }
}
