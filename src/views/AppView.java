package views;

import utils.UserApp;

import java.util.Scanner;
import models.Enums.Menu;

public class AppView {
    public static  void run() {
        Scanner scanner = new Scanner(System.in);
        do {
            UserApp.getCurrentMenu().checkCommand(scanner);

        } while (UserApp.getCurrentMenu() != Menu.Exit);
    }
}
