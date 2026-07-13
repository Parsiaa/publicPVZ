package views;

import java.util.regex.Matcher;
import controllers.GreenhouseMenuController;
import controllers.MenuRouter;
import utils.Command;

public class GreenhouseMenuView extends MenuView {

    private final GreenhouseMenuController controller;

    public GreenhouseMenuView(MenuRouter router, GreenhouseMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        if (Command.SHOW_GREENHOUSE.match(input) != null) {
            print(controller.handleShowGreenhouse());
            return true;
        }
        Matcher matcher = Command.PLANT_POT.match(input);
        if (matcher != null) {
            print(controller.handlePlantPot(Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y"))));
            return true;
        }
        matcher = Command.COLLECT_POT.match(input);
        if (matcher != null) {
            print(controller.handleCollectPot(Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y"))));
            return true;
        }
        matcher = Command.GROW_POT.match(input);
        if (matcher != null) {
            print(controller.handleGrow(Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y"))));
            return true;
        }
        if (Command.ENTER_SHOP.match(input) != null) {
            print(controller.handleEnterShop());
            return true;
        }
        return false;
    }
}
