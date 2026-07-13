package views;

import java.util.regex.Matcher;
import controllers.MenuRouter;
import controllers.PlantSelectionMenuController;
import controllers.SettingsMenuController;
import utils.Command;

public class PlantSelectionMenuView extends MenuView {

    private final PlantSelectionMenuController controller;
    private final SettingsMenuController settingsController;

    public PlantSelectionMenuView(MenuRouter router, PlantSelectionMenuController controller,
                                  SettingsMenuController settingsController) {
        super(router);
        this.controller = controller;
        this.settingsController = settingsController;
    }

    @Override
    protected boolean handleCommand(String input) {
        if (Command.SHOW_ALL_PLANTS.match(input) != null) {
            print(controller.showAllPlantsInLevel());
            return true;
        }
        if (Command.SHOW_AVAILABLE_PLANTS.match(input) != null) {
            print(controller.showAvailablePlantsInLevel());
            return true;
        }
        Matcher matcher = Command.ADD_PLANT.match(input);
        if (matcher != null) {
            print(controller.addPlantToSlot(matcher.group("type")));
            return true;
        }
        matcher = Command.REMOVE_PLANT.match(input);
        if (matcher != null) {
            print(controller.removePlantFromSlot(matcher.group("type")));
            return true;
        }
        matcher = Command.BOOST_PLANT.match(input);
        if (matcher != null) {
            print(controller.boostPlant(matcher.group("type")));
            return true;
        }
        if (Command.START_GAME.match(input) != null) {
            print(controller.finalizeAndStart(settingsController));
            return true;
        }
        return false;
    }
}
