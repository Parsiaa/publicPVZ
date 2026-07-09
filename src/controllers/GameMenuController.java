package controllers;

import models.Enums.Menu;
import models.User;
import utils.Result;
import utils.UserApp;

public class GameMenuController {
    private UserApp userApp;
    private MenuRouter router;

    public GameMenuController(UserApp userApp, MenuRouter router) {
        this.userApp = userApp;
        this.router = router;
    }

    public Result handleEnterChapter(String chapterName) {
        User user = userApp.getLoggedInUser();
        if (user == null) {
            return new Result("Error: You must be logged in first.", false);
        }
        int chapterIndex = chapterIndexOf(chapterName);
        if (chapterIndex == -1) {
            return new Result("Error: Chapter '" + chapterName + "' doesn't exist.", false);
        }
        if (chapterIndex > user.getHighestChapter() + 1) {
            return new Result("Error: Chapter '" + chapterName + "' is not unlocked yet.", false);
        }
        router.navigateTo(Menu.PlantSelectionMenu);
        return new Result("Entering chapter " + chapterName + ". Select your plants!", true);
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
            return new Result("Cheat activated: +" + amount + " coins. You now have " + user.getCoins() + " coins.", true);
        }
        if (currencyType.equalsIgnoreCase("diamond")) {
            user.setGems(user.getGems() + amount);
            userApp.saveUsers();
            return new Result("Cheat activated: +" + amount + " diamonds. You now have " + user.getGems() + " diamonds.", true);
        }
        return new Result("Error: Currency must be 'coin' or 'diamond'.", false);
    }

    private int chapterIndexOf(String chapterName) {
        switch (chapterName.toLowerCase().replace("-", "").replace("_", "").replace(" ", "")) {
            case "egypt": return 1;
            case "frostbite": return 2;
            case "beach": return 3;
            case "darkages": return 4;
            default: return -1;
        }
    }
}
