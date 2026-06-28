package views;

import java.util.Scanner;

public class ExitMenu implements AppMenu {
    @Override
    public void check(Scanner scanner) {
        throw new RuntimeException("Goodbye!");
    }
}