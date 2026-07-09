package controllers;

import java.util.HashMap;
import utils.Result;
import utils.UserApp;

public class SettingsMenuController {
    private UserApp userApp;
    private java.util.Map<String, Integer> difficultyByUser;

    public SettingsMenuController(UserApp userApp) {
        this.userApp = userApp;
        this.difficultyByUser = new HashMap<>();
    }

    public Result changeDifficultyLevel(int level) {
        if (level < 1 || level > 5) {
            return new Result("Error: Difficulty level must be between 1 and 5.", false);
        }
        difficultyByUser.put(userApp.getLoggedInUser().getUsername(), level);
        return new Result("Difficulty level set to " + level + ".", true);
    }

    public int getDifficultyLevel() {
        return difficultyByUser.getOrDefault(userApp.getLoggedInUser().getUsername(), 3);
    }
}
