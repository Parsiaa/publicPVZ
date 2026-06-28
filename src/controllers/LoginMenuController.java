package controllers;

import models.User;
import utils.Result;
import utils.UserApp;

public class LoginMenuController {
    private UserApp userApp;
    public LoginMenuController(UserApp userApp) {
        this.userApp = userApp;
    }

    public Result handleLoginAccount(String username, String password, boolean stayLoggedIn) {
        if (!userApp.usernameTaken(username)) {
            return new Result("Error: Username doesn't exist.", false);
        }
        User newUser = userApp.getUser().get(username);
        if (!newUser.getPassword().equals(password)) {
            return new Result("Error: Password doesn't match.", false);
        }
        newUser.setStayLoggedIn(stayLoggedIn);
        userApp.setLoggedInUser(newUser);
        return new Result("Logged in successfully!", true);
    }

    public Result handleForgotPassword() {
        //TODO
        return null;
    }
    public Result handleSecurityAnswer() {
        //TODO
        return null;
    }
}
