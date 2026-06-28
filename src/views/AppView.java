package views;

import utils.UserApp;

import java.awt.*;
import java.util.Scanner;

public class AppView {
    public static  void run() {
        Scanner scanner = new Scanner(System.in);
        do {
            UserApp.getCurrentMenu().checkCommand(scanner);

        } while (App.getCurrentMenu() != Menu.Exit);
    }
}
