package models.Enums;


import views.AppMenu;
import views.ExitMenu;

import java.util.Scanner;

public enum Menu {
    LoginMenu(new LoginMenu()),
    SignUpMenu(new SignUpMenu()),
    Dashboard(new Dashboard()),
    ProfileMenu(new ProfileMenu()),
    Exit(new ExitMenu());
    private final AppMenu menu;
    Menu(AppMenu menu){
        this.menu = menu;
    }

    public void checkCommand(Scanner scanner){
        this.menu.check(scanner);
    }
}
