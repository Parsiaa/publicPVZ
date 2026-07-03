package controllers;

import models.User;
import utils.Result;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import utils.SecurityUtils;
import utils.UserApp;

public class SignUpMenuController {
    private UserApp userApp;
    private User pendingUser;
    private static final List<String> SECURITY_QUESTIONS = Arrays.asList(
            "What was the name of your first pet?",
            "What is your mother's maiden name?",
            "What city were you born in?");

    public SignUpMenuController(UserApp userApp) {
        this.userApp = userApp;
    }

    public Result handleCreateAccount(String username, String password, String passwordConfirm,
                                      String nickname, String email, String gender) {
        if (userApp.usernameTaken(username)) {
            return new Result("Error: Username is already taken.", false);
        }
        if (!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")) {
            return new Result("Error: Gender must be 'male' or 'female'.", false);
        }
        User newUser = new User(
                username, password, null, passwordConfirm, nickname, email, gender,
                0, 0, null, null, 0, 0, 0, 0, 0, 0, 0, false, new ArrayList<>()
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
        newUser.setPasswordHash(SecurityUtils.hashSHA256(password));
        this.pendingUser = newUser;
        StringBuilder sb = new StringBuilder("Now pick a security question:\n");
        for (int i = 0; i < SECURITY_QUESTIONS.size(); i++) {
            sb.append(i + 1).append(". ").append(SECURITY_QUESTIONS.get(i)).append("\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result handlePickQuestion(int questionNumber, String answer, String answerConfirm) {
        if (pendingUser == null) {
            return new Result("Error: Register first before picking a security question.", false);
        }
        if (questionNumber < 1 || questionNumber > SECURITY_QUESTIONS.size()) {
            return new Result("Error: Question number must be between 1 and " + SECURITY_QUESTIONS.size() + ".", false);
        }
        if (answer == null || answer.trim().isEmpty()) {
            return new Result("Error: Answer cannot be empty.", false);
        }
        if (!answer.equals(answerConfirm)) {
            return new Result("Error: Answer and its confirmation do not match.", false);
        }
        pendingUser.setSecurityQuestion(SECURITY_QUESTIONS.get(questionNumber - 1));
        pendingUser.setSecurityAnswer(answer);
        userApp.addUser(pendingUser);
        pendingUser = null;
        return new Result("Account created successfully! You can now log in.", true);
    }

    public List<String> getSecurityQuestions() {
        return SECURITY_QUESTIONS;
    }
}
