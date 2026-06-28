package controllers;

import models.User;
import utils.Result;
import java.util.ArrayList;
import utils.UserApp;

public class SignUpMenuController {

    private UserApp userApp;
    public SignUpMenuController(UserApp userApp) {
        this.userApp = userApp;
    }
    public Result handleCreateAccount(String username, String password, String passwordConfirm,
                                      String email, String nickname, String securityQuestion,
                                      String securityAnswer) {
        if (userApp.usernameTaken(username)) {
            return new Result("Error: Username is already taken.", false);
        }
        User newUser = new User(
                username, password, password, passwordConfirm, nickname, email, "Not Specified",
                0, 0, securityQuestion, securityAnswer, 0, 0, 0, 0, 0, 0, 0, new ArrayList<>()
        );
        Result usernameResult = newUser.verifyUsername();
        if (!usernameResult.isSuccess()) {
            return usernameResult;
        }
        Result passwordResult = newUser.verifyPassword();
        if (!passwordResult.isSuccess()) {
            return passwordResult;
        }

        Result emailResult = newUser.verifyEmail();
        if (!emailResult.isSuccess()) {
            return emailResult;
        }

        Result nicknameResult = newUser.verifyNickname();
        if (!nicknameResult.isSuccess()) {
            return nicknameResult;
        }
        userApp.addUser(newUser);
        return new Result("Account created successfully! You can now log in.", true);
    }
}
