package models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import utils.Result;
import java.util.Set;
import java.util.HashSet;

public class User {
    private String username;
    private String password;
    private String passwordHash;
    private String passwordConfirm;
    private String nickname;
    private String email;
    private String gender;
    private int coins;
    private int gems;
    private String securityQuestion;
    private String securityAnswer;
    private int storedStartingPlantFoods;
    private int highestChapter;
    private int highestLevel;
    private int miniGamesCompleted;
    private int dailyQuestsCompleted;
    private int notDailyQuestsCompleted;
    private int highestMeowPointScore;
    private List<Quest> activeQuests;
    private Set<String> boostedPlants = new HashSet<>();


    public User(String username, String password, String passwordHash, String passwordConfirm,
                String nickname, String email, String gender, int coins, int gems,
                String securityQuestion, String securityAnswer, int storedStartingPlantFoods, int highestChapter,
                int highestLevel, int miniGamesCompleted, int dailyQuestsCompleted, int notDailyQuestsCompleted,
                int highestMeowPointScore, List<Quest> activeQuests) {
        this.username = username;
        this.password = password;
        this.passwordHash = passwordHash;
        this.passwordConfirm = passwordConfirm;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.coins = coins;
        this.gems = gems;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.storedStartingPlantFoods = storedStartingPlantFoods;
        this.highestChapter = highestChapter;
        this.highestLevel = highestLevel;
        this.miniGamesCompleted = miniGamesCompleted;
        this.dailyQuestsCompleted = dailyQuestsCompleted;
        this.notDailyQuestsCompleted = notDailyQuestsCompleted;
        this.highestMeowPointScore = highestMeowPointScore;
        this.activeQuests = activeQuests;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm;}

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }

    public int getGems() { return gems; }
    public void setGems(int gems) { this.gems = gems; }

    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    public int getStoredStartingPlantFoods() { return storedStartingPlantFoods; }
    public void setStoredStartingPlantFoods(int storedStartingPlantFoods) { this.storedStartingPlantFoods = storedStartingPlantFoods; }

    public int getHighestChapter() { return highestChapter; }
    public void setHighestChapter(int highestChapter) { this.highestChapter = highestChapter; }

    public int getHighestLevel() { return highestLevel; }
    public void setHighestLevel(int highestLevel) { this.highestLevel = highestLevel; }

    public int getMiniGamesCompleted() { return miniGamesCompleted; }
    public void setMiniGamesCompleted(int miniGamesCompleted) { this.miniGamesCompleted = miniGamesCompleted; }

    public int getDailyQuestsCompleted() { return dailyQuestsCompleted; }
    public void setDailyQuestsCompleted(int dailyQuestsCompleted) { this.dailyQuestsCompleted = dailyQuestsCompleted; }

    public int getNotDailyQuestsCompleted() { return notDailyQuestsCompleted; }
    public void setNotDailyQuestsCompleted(int notDailyQuestsCompleted) { this.notDailyQuestsCompleted = notDailyQuestsCompleted; }

    public int getHighestMeowPointScore() { return highestMeowPointScore; }
    public void setHighestMeowPointScore(int highestMeowPointScore) { this.highestMeowPointScore = highestMeowPointScore; }

    public List<Quest> getActiveQuests() { return activeQuests; }
    public void setActiveQuests(List<Quest> activeQuests) { this.activeQuests = activeQuests; }


    public Result verifyUsername() {
        List<String> errors = new ArrayList<>();
        if (username == null) username = "";

        if (!username.matches("^[a-zA-Z0-9-]+$")) {
            errors.add("Username contains invalid characters. Only letters, numbers, and '-' are allowed.");
        }
        // If necessary uncomment these three lines.
        // if (username.length() < 3) {
        //     errors.add("Username must be at least 3 characters long.");
        // }

        return new Result(String.join("\n", errors), errors.isEmpty());
    }

    public Result verifyPassword() {
        List<String> errors = new ArrayList<>();

        if (password == null) password = "";

        if (password.length() < 8) {
            errors.add("Password must be at least 8 characters long.");
        }

        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter.");
        }

        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter.");
        }

        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            errors.add("Password must contain at least one number.");
        }

        String specialCharRegex = "[!#$%^&*()=+{}\\[\\]|\\\\/:;'\",<>?]";
        if (!Pattern.compile(specialCharRegex).matcher(password).find()) {
            errors.add("Password must contain at least one special character.");
        }

        if (passwordConfirm == null || !password.equals(passwordConfirm)) {
            errors.add("Passwords do not match.");
        }

        return new Result(String.join("\n", errors), errors.isEmpty());
    }

    public Result verifyNickname() {
        List<String> errors = new ArrayList<>();

        if (nickname.length() < 3) {
            errors.add("Nickname must be at least 3 characters long.");
        }

        if (nickname.length() > 30) {
            errors.add("Nickname must be at most 30 characters long.");
        }

        return new Result(String.join("\n", errors), errors.isEmpty());
    }

    public Result verifyEmail() {
        List<String> errors = new ArrayList<>();
        if (email == null) email = "";

        int atCount = 0;
        for (char c : email.toCharArray()) {
            if (c == '@') atCount++;
        }
        if (atCount != 1) {
            errors.add("Email must contain exactly one '@' symbol.");
            return new Result(String.join("\n", errors), errors.isEmpty());
        }

        String[] parts = email.split("@", -1);
        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.isEmpty()) {
            errors.add("Email username part cannot be empty.");
        } else {
            if (!localPart.matches("^[a-zA-Z0-9._-]+$")) {
                errors.add("Email username can only contain English letters, numbers, dots (.), hyphens (-), and underscores (_).");
            }
            if (!localPart.matches("^[a-zA-Z0-9].*[a-zA-Z0-9]$") && !localPart.matches("^[a-zA-Z0-9]$")) {
                errors.add("Email username must start and end with a letter or number.");
            }
            if (localPart.contains("..")) {
                errors.add("Email username cannot contain consecutive dots (..).");
            }
        }

        if (domainPart.isEmpty()) {
            errors.add("Email domain part cannot be empty.");
        } else {
            if (!domainPart.contains(".")) {
                errors.add("Email domain must contain at least one dot (.).");
            } else {
                if (!domainPart.matches("^[a-zA-Z0-9.-]+$")) {
                    errors.add("Email domain can only contain English letters, numbers, and hyphens (-).");
                }
                if (!domainPart.matches("^[a-zA-Z0-9].*[a-zA-Z0-9]$") && !domainPart.matches("^[a-zA-Z0-9]$")) {
                    errors.add("Email domain must start and end with a letter or number.");
                }
                if (domainPart.contains("..")) {
                    errors.add("Email domain cannot contain consecutive dots (..).");
                }

                int lastDotIndex = domainPart.lastIndexOf('.');
                String extension = domainPart.substring(lastDotIndex + 1);
                if (extension.length() < 2 || !extension.matches("^[a-zA-Z]+$")) {
                    errors.add("Email domain extension must be at least two letters long and contain only letters.");
                }
            }
        }

        return new Result(String.join("\n", errors), errors.isEmpty());
    }

    public boolean hasBoostFor(String plantName) {
        return boostedPlants.contains(plantName);
    }
    public void addBoostFor(String plantName) {
        boostedPlants.add(plantName);
    }
    public void consumeBoostFor(String plantName) {
        boostedPlants.remove(plantName);
    }
}