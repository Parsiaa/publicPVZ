package views;

import java.util.regex.Matcher;
import controllers.CollectionMenuController;
import controllers.MenuRouter;
import utils.Command;

public class CollectionMenuView extends MenuView {

    private final CollectionMenuController controller;

    public CollectionMenuView(MenuRouter router, CollectionMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        if (Command.COLLECTION_SHOW_PLANTS.match(input) != null) {
            print(controller.showUnlockedPlants());
            return true;
        }
        if (Command.COLLECTION_SHOW_ALL_PLANTS.match(input) != null) {
            print(controller.showAllPlants());
            return true;
        }
        if (Command.COLLECTION_SHOW_ZOMBIES.match(input) != null) {
            print(controller.showUnlockedZombies());
            return true;
        }
        if (Command.COLLECTION_SHOW_ALL_ZOMBIES.match(input) != null) {
            print(controller.showAllZombies());
            return true;
        }
        Matcher matcher = Command.COLLECTION_SHOW_PLANT.match(input);
        if (matcher != null) {
            print(controller.showPlantDetails(matcher.group("plant")));
            return true;
        }
        matcher = Command.COLLECTION_SHOW_ZOMBIE.match(input);
        if (matcher != null) {
            print(controller.showZombieDetails(matcher.group("zombie")));
            return true;
        }
        matcher = Command.COLLECTION_UPGRADE_PLANT.match(input);
        if (matcher != null) {
            print(controller.upgradePlant(matcher.group("plant")));
            return true;
        }
        matcher = Command.COLLECTION_PURCHASE_PLANT.match(input);
        if (matcher != null) {
            print(controller.purchasePlant(matcher.group("plant")));
            return true;
        }
        return false;
    }
}
