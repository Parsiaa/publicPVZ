package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.MatchState;
import models.SeedPacket;
import models.Enums.ChapterType;
import models.Enums.Menu;
import models.User;
import utils.PlantFactory;
import utils.Result;
import utils.UserApp;

public class GameMenuController {
    private UserApp userApp;
    private MenuRouter router;
    private GameEngine gameEngine;
    private SettingsMenuController settingsController;

    public GameMenuController(UserApp userApp, MenuRouter router) {
        this.userApp = userApp;
        this.router = router;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void setSettingsController(SettingsMenuController settingsController) {
        this.settingsController = settingsController;
    }

    public Result handleEnterChapter(String chapterName) {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        ChapterType chapter = LevelFactory.chapterTypeOf(chapterName);
        if (chapter == null) {
            return new Result("Error: Chapter '" + chapterName + "' doesn't exist.", false);
        }
        if (LevelFactory.chapterIndex(chapter) > user.getHighestChapter() + 1) {
            return new Result("Error: Chapter '" + chapterName + "' is not unlocked yet.", false);
        }
        int completed = user.getCompletedLevels(chapter.name());
        int level = Math.min(completed + 1, LevelFactory.PLAYABLE_LEVELS_PER_CHAPTER);
        String note = completed >= LevelFactory.PLAYABLE_LEVELS_PER_CHAPTER
                ? " (chapter finished - replaying level 3; the boss level arrives in the next phase)" : "";
        LevelConfig config = LevelFactory.createAdventureLevel(chapter, level,
                new ArrayList<>(user.getUnlockedPlants()), new Random());
        if (gameEngine == null) {
            router.navigateTo(Menu.PlantSelectionMenu);
            return new Result("Entering chapter " + chapterName + " level " + level + ".", true);
        }
        gameEngine.setPendingLevel(config);
        if (config.isConveyor()) {
            gameEngine.setScoreMode(false);
            MatchState state = new MatchState(user, config.getInitialSun(), difficulty());
            state.initializeFromUser(user);
            gameEngine.startMatch(state);
            return new Result("Chapter " + chapterName + " level " + level
                    + " started - plants arrive on the conveyor belt!" + note, true);
        }
        router.navigateTo(Menu.PlantSelectionMenu);
        return new Result("Entering chapter " + chapterName + " level " + level
                + ". Select your plants!" + note, true);
    }

    public Result handleStartScoreGame() {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        if (gameEngine == null) {
            return new Result("Error: The game engine is not available.", false);
        }
        List<SeedPacket> packets = new ArrayList<>();
        for (String name : user.getUnlockedPlants()) {
            if (packets.size() >= 8) {
                break;
            }
            SeedPacket packet = PlantFactory.createSeedPacket(name);
            if (packet != null) {
                packets.add(packet);
            }
        }
        gameEngine.setPendingLevel(null);
        gameEngine.setScoreMode(true);
        MatchState state = new MatchState(user, 150, difficulty());
        state.setSeedPackets(packets);
        state.initializeFromUser(user);
        gameEngine.startMatch(state);
        router.navigateTo(Menu.GameMenu);
        return new Result("Score run started! Today's zombie waves are the same for every player."
                + " Earn MewPoints with fast, multi and combo kills.", true);
    }

    private int difficulty() {
        return settingsController != null ? settingsController.getDifficultyLevel() : 3;
    }

    public Result handleEnterCollection() {
        if (userApp.getLoggedInUser() == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        router.navigateTo(Menu.CollectionMenu);
        return new Result("Entered Collection Menu.", true);
    }

    public Result handleEnterGreenhouse() {
        if (userApp.getLoggedInUser() == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        router.navigateTo(Menu.GreenhouseMenu);
        return new Result("Entered the Greenhouse.", true);
    }

    public Result handleShowTravelLog() {
        if (userApp.getLoggedInUser() == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        router.navigateTo(Menu.TravelLogMenu);
        return new Result("Opened the Travel Log.", true);
    }

    public Result handleShowLeaderBoard() {
        if (userApp.getLoggedInUser() == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        router.navigateTo(Menu.LeaderboardMenu);
        return new Result("Opened the Leaderboard.", true);
    }

    public Result handleShowCoinWallet() {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        return new Result("Coins: " + user.getCoins(), true);
    }

    public Result handleShowGemWallet() {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        return new Result("Gems: " + user.getGems(), true);
    }

    public Result handleCheat(int amount, String currencyType) {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        if (amount < 1) {
            return new Result("Error: Amount must be at least 1.", false);
        }
        if (currencyType.equalsIgnoreCase("coin")) {
            user.setCoins(user.getCoins() + amount);
            userApp.saveUsers();
            return new Result("Cheat activated: +" + amount + " coins. You now have "
                    + user.getCoins() + " coins.", true);
        }
        if (currencyType.equalsIgnoreCase("diamond")) {
            user.setGems(user.getGems() + amount);
            userApp.saveUsers();
            return new Result("Cheat activated: +" + amount + " diamonds. You now have "
                    + user.getGems() + " diamonds.", true);
        }
        return new Result("Error: Currency must be 'coin' or 'diamond'.", false);
    }

}
