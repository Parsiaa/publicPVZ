package views;

import java.util.regex.Matcher;
import controllers.MenuRouter;
import controllers.ShopMenuController;
import utils.Command;

public class ShopMenuView extends MenuView {

    private final ShopMenuController controller;

    public ShopMenuView(MenuRouter router, ShopMenuController controller) {
        super(router);
        this.controller = controller;
    }

    @Override
    protected boolean handleCommand(String input) {
        if (Command.SHOP_LIST.match(input) != null) {
            print(controller.handleShopList());
            return true;
        }
        if (Command.SHOP_DAILY.match(input) != null) {
            print(controller.handleShopDaily());
            return true;
        }
        Matcher matcher = Command.SHOP_BUY.match(input);
        if (matcher != null) {
            print(controller.handleShopBuy(Integer.parseInt(matcher.group("item")),
                    Integer.parseInt(matcher.group("count")), matcher.group("type")));
            return true;
        }
        return false;
    }
}
