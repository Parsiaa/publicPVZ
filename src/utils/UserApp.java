package utils;

import models.Enums.Menu;
import models.User;

import java.util.HashMap;
import java.util.Map;

public class UserApp {
    private Map<String, User> users;
    private User loggedInUser;
    private static Menu currentMenu;

    public UserApp() {
        this.users = new HashMap<>();
        this.loggedInUser = null;
        loadUsers();
    }
    public boolean usernameTaken(String username) {
        return users.containsKey(username);
    }
    public User authenticateUser(String username, String password) {
        if (users.containsKey(username)) {
            User potentialUser = users.get(username);

            // Assuming your User class has a getPassword() method
            if (potentialUser.getPassword().equals(password)) {
                this.loggedInUser = potentialUser;
                return potentialUser;
            }
        }
        // If username doesn't exist, or password doesn't match
        return null;
    }
    public boolean registerUser(String username, String password) {
        if (usernameTaken(username)) {
            return false; // Registration fails
        }
        User newUser = new User(username, password);
        users.put(username, newUser);
        saveUsers();
        return true;
    }

    public void logout() {
        this.loggedInUser = null;
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    public void loadUsers() {
        this.users = new HashMap<>(SaveManager.load());
    }

    public void saveUsers() {
        SaveManager.save(this.users);
    }

    /** Returns the user that closed the app while "stay logged in" was on, if any. */
    public User findStayLoggedInUser() {
        for (User user : users.values()) {
            if (user.getStayLoggedIn()) {
                return user;
            }
        }
        return null;
    }
    public void addUser(User newUser) {
        users.put(newUser.getUsername(), newUser);
        saveUsers(); // Immediately save to the JSON file
    }

    public Map<String, User> getUser() {
        return this.users;
    }

    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }
}
