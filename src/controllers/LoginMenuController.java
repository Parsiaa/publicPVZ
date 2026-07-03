package controllers;

import models.User;
import utils.Result;
import utils.SecurityUtils;
import utils.UserApp;

public class LoginMenuController {
    private UserApp userApp;
    private User pendingRecoveryUser;
    private boolean answerAccepted;

    public LoginMenuController(UserApp userApp) {
        this.userApp = userApp;
    }

    public Result handleLoginAccount(String username, String password, boolean stayLoggedIn) {
        if (!userApp.usernameTaken(username)) {
            return new Result("Error: Username doesn't exist.", false);
        }
        User newUser = userApp.getUser().get(username);
        if (!SecurityUtils.matches(password, newUser.getPasswordHash())) {
            return new Result("Error: Password doesn't match.", false);
        }
        newUser.setStayLoggedIn(stayLoggedIn);
        userApp.setLoggedInUser(newUser);
        userApp.saveUsers();
        return new Result("Logged in successfully!", true);
    }

    public Result handleForgotPassword(String username, String email) {
        if (!userApp.usernameTaken(username)) {
            return new Result("Error: Username doesn't exist.", false);
        }
        User user = userApp.getUser().get(username);
        if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email)) {
            return new Result("Error: Email doesn't match this account.", false);
        }
        this.pendingRecoveryUser = user;
        this.answerAccepted = false;
        return new Result("Security question: " + user.getSecurityQuestion(), true);
    }

    public Result handleSecurityAnswer(String answer) {
        if (pendingRecoveryUser == null) {
            return new Result("Error: Start password recovery first.", false);
        }
        if (!pendingRecoveryUser.getSecurityAnswer().equals(answer)) {
            pendingRecoveryUser = null;
            answerAccepted = false;
            return new Result("Error: Security answer is incorrect. Returning to Login Menu.", false);
        }
        answerAccepted = true;
        return new Result("Correct! Please enter your new password.", true);
    }

    public Result handleSetNewPassword(String newPassword) {
        if (pendingRecoveryUser == null || !answerAccepted) {
            return new Result("Error: Answer your security question first.", false);
        }
        String oldPlain = pendingRecoveryUser.getPassword();
        String oldConfirm = pendingRecoveryUser.getPasswordConfirm();
        pendingRecoveryUser.setPassword(newPassword);
        pendingRecoveryUser.setPasswordConfirm(newPassword);
        Result check = pendingRecoveryUser.verifyPassword();
        if (!check.isSuccess()) {
            pendingRecoveryUser.setPassword(oldPlain);
            pendingRecoveryUser.setPasswordConfirm(oldConfirm);
            return check;
        }
        pendingRecoveryUser.setPasswordHash(SecurityUtils.hashSHA256(newPassword));
        userApp.saveUsers();
        pendingRecoveryUser = null;
        answerAccepted = false;
        return new Result("Password reset successfully! You can now log in.", true);
    }
}
