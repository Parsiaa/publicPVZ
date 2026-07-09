package controllers;

import models.User;
import utils.Result;
import utils.SecurityUtils;
import utils.UserApp;

public class ProfileMenuController {
    private UserApp userApp;

    public ProfileMenuController(UserApp userApp) {
        this.userApp = userApp;
    }

    public Result changeUsername(String newUsername) {
        User user = userApp.getLoggedInUser();
        if (user.getUsername().equals(newUsername)) {
            return new Result("Error: New username must be different from the current one.", false);
        }
        if (userApp.usernameTaken(newUsername)) {
            return new Result("Error: Username is already taken.", false);
        }
        String old = user.getUsername();
        user.setUsername(newUsername);
        Result check = user.verifyUsername();
        if (!check.isSuccess()) {
            user.setUsername(old);
            return check;
        }
        userApp.getUser().remove(old);
        userApp.getUser().put(newUsername, user);
        userApp.saveUsers();
        return new Result("Username changed to " + newUsername + ".", true);
    }

    public Result changeNickname(String newNickname) {
        User user = userApp.getLoggedInUser();
        if (user.getNickname() != null && user.getNickname().equals(newNickname)) {
            return new Result("Error: New nickname must be different from the current one.", false);
        }
        String old = user.getNickname();
        user.setNickname(newNickname);
        Result check = user.verifyNickname();
        if (!check.isSuccess()) {
            user.setNickname(old);
            return check;
        }
        userApp.saveUsers();
        return new Result("Nickname changed to " + newNickname + ".", true);
    }

    public Result changeEmail(String newEmail) {
        User user = userApp.getLoggedInUser();
        if (user.getEmail() != null && user.getEmail().equals(newEmail)) {
            return new Result("Error: New email must be different from the current one.", false);
        }
        String old = user.getEmail();
        user.setEmail(newEmail);
        Result check = user.verifyEmail();
        if (!check.isSuccess()) {
            user.setEmail(old);
            return check;
        }
        userApp.saveUsers();
        return new Result("Email changed to " + newEmail + ".", true);
    }

    public Result changePassword(String newPassword, String oldPassword) {
        User user = userApp.getLoggedInUser();
        if (!SecurityUtils.matches(oldPassword, user.getPasswordHash())) {
            return new Result("Error: Old password is incorrect.", false);
        }
        if (oldPassword.equals(newPassword)) {
            return new Result("Error: New password must be different from the current one.", false);
        }
        String old = user.getPassword();
        String oldConfirm = user.getPasswordConfirm();
        user.setPassword(newPassword);
        user.setPasswordConfirm(newPassword);
        Result check = user.verifyPassword();
        if (!check.isSuccess()) {
            user.setPassword(old);
            user.setPasswordConfirm(oldConfirm);
            return check;
        }
        user.setPasswordHash(SecurityUtils.hashSHA256(newPassword));
        userApp.saveUsers();
        return new Result("Password changed successfully.", true);
    }

    public Result getUserInfo() {
        User user = userApp.getLoggedInUser();
        return new Result("Username: " + user.getUsername()
                + "\nNickname: " + user.getNickname()
                + "\nGames played: " + user.getMiniGamesCompleted()
                + "\nCoins: " + user.getCoins()
                + "\nGems: " + user.getGems()
                + "\nLevels passed: " + user.getHighestLevel()
                + "\nHighest Meow Points: " + user.getHighestMeowPointScore(), true);
    }
}
