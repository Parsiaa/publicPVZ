package views;

import java.util.regex.Matcher;
import controllers.GameEngine;
import controllers.GameMenuController;
import controllers.MatchCheatController;
import controllers.MenuRouter;
import utils.Command;

public class GameMenuView extends MenuView {

    private final GameMenuController controller;
    private final GameEngine engine;
    private final MatchCheatController cheats;

    public GameMenuView(MenuRouter router, GameMenuController controller, GameEngine engine,
                        MatchCheatController cheats) {
        super(router);
        this.controller = controller;
        this.engine = engine;
        this.cheats = cheats;
    }

    @Override
    protected boolean handleCommand(String input) {
        return handleMenuCommand(input) || handleMatchCommand(input);
    }

    private boolean handleMenuCommand(String input) {
        Matcher matcher = Command.MENU_ENTER_CHAPTER.match(input);
        if (matcher != null) {
            print(controller.handleEnterChapter(matcher.group("chapter")));
            return true;
        }
        if (Command.MENU_GREENHOUSE.match(input) != null) {
            print(controller.handleEnterGreenhouse());
            return true;
        }
        if (Command.MENU_TRAVEL_LOG.match(input) != null) {
            print(controller.handleShowTravelLog());
            return true;
        }
        if (Command.MENU_LEADERBOARD.match(input) != null) {
            print(controller.handleShowLeaderBoard());
            return true;
        }
        if (Command.MENU_COIN_WALLET.match(input) != null) {
            print(controller.handleShowCoinWallet());
            return true;
        }
        if (Command.MENU_GEM_WALLET.match(input) != null) {
            print(controller.handleShowGemWallet());
            return true;
        }
        matcher = Command.MENU_CHEAT_ADD.match(input);
        if (matcher != null) {
            print(controller.handleCheat(Integer.parseInt(matcher.group("amount")), matcher.group("currency")));
            return true;
        }
        return false;
    }

    private boolean handleMatchCommand(String input) {
        Matcher matcher = Command.ADVANCE_TIME.match(input);
        if (matcher != null) {
            print(engine.handleAdvanceTime(Integer.parseInt(matcher.group("count"))));
            return true;
        }
        matcher = Command.PLANT_PLANT.match(input);
        if (matcher != null) {
            print(engine.handlePlant(matcher.group("type"), x(matcher), y(matcher)));
            return true;
        }
        matcher = Command.PLUCK_PLANT.match(input);
        if (matcher != null) {
            print(engine.handlePluck(x(matcher), y(matcher)));
            return true;
        }
        matcher = Command.FEED_PLANT.match(input);
        if (matcher != null) {
            print(engine.handleFeedPlant(x(matcher), y(matcher)));
            return true;
        }
        matcher = Command.COLLECT_SUN.match(input);
        if (matcher != null) {
            print(engine.handleCollectSun(x(matcher), y(matcher)));
            return true;
        }
        matcher = Command.SHOW_TILE_STATUS.match(input);
        if (matcher != null) {
            print(engine.handleShowTileStatus(x(matcher), y(matcher)));
            return true;
        }
        matcher = Command.BREAK_VASE.match(input);
        if (matcher != null) {
            print(engine.handleBreakVase(x(matcher), y(matcher)));
            return true;
        }
        matcher = Command.PLACE_ZOMBIE.match(input);
        if (matcher != null) {
            print(engine.handlePlaceZombie(matcher.group("type"), x(matcher), y(matcher)));
            return true;
        }
        return handleSimpleMatchCommand(input);
    }

    private boolean handleSimpleMatchCommand(String input) {
        if (Command.SHOW_SUN_AMOUNT.match(input) != null) {
            print(engine.handleShowSunAmount());
            return true;
        }
        if (Command.SHOW_MAP.match(input) != null) {
            print(engine.handleShowMap());
            return true;
        }
        if (Command.SHOW_PLANTS_STATUS.match(input) != null) {
            print(engine.handleShowPlantsStatus());
            return true;
        }
        if (Command.ZOMBIES_INFO.match(input) != null) {
            print(engine.handleZombiesInfo());
            return true;
        }
        if (Command.START_ZOMBIE_WAVES.match(input) != null) {
            print(engine.handleStartZombieWaves());
            return true;
        }
        return handleMatchCheat(input);
    }

    private boolean handleMatchCheat(String input) {
        Matcher matcher = Command.CHEAT_ADD_SUNS.match(input);
        if (matcher != null) {
            print(cheats.handleAddSuns(Integer.parseInt(matcher.group("count"))));
            return true;
        }
        matcher = Command.CHEAT_SPAWN_ZOMBIE.match(input);
        if (matcher != null) {
            print(cheats.handleSpawnZombie(matcher.group("type"), x(matcher), y(matcher)));
            return true;
        }
        if (Command.CHEAT_NUKE.match(input) != null) {
            print(cheats.handleNuke());
            return true;
        }
        if (Command.CHEAT_REMOVE_COOLDOWN.match(input) != null) {
            print(cheats.handleRemoveCooldowns());
            return true;
        }
        if (Command.CHEAT_ADD_PLANT_FOOD.match(input) != null) {
            print(cheats.handleAddPlantFood());
            return true;
        }
        return false;
    }

    @Override
    protected void handleMenuEnter(String menuName) {
        if (menuName.equalsIgnoreCase("collection")) {
            print(controller.handleEnterCollection());
        } else {
            super.handleMenuEnter(menuName);
        }
    }

    private int x(Matcher matcher) {
        return Integer.parseInt(matcher.group("x"));
    }

    private int y(Matcher matcher) {
        return Integer.parseInt(matcher.group("y"));
    }
}
