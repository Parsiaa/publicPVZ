package utils;

import models.Enums.Menu;
import models.User;

import java.util.HashMap;
import java.util.Map;

public class UserApp {
    private Map<String, User> users;
    private User loggedInUser;
    private static Menu currentMenu;

    private static final String USERS_FILE_PATH = "data/users.json";

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
        // TODO: Read from USERS_FILE_PATH using Gson or Jackson.
        // Example with Gson:
        /*
        try (Reader reader = new FileReader(USERS_FILE_PATH)) {
            Type type = new TypeToken<HashMap<String, User>>(){}.getType();
            this.users = new Gson().fromJson(reader, type);
            if (this.users == null) {
                this.users = new HashMap<>(); // Fallback if file is empty
            }
        } catch (IOException e) {
            System.out.println("No previous save file found. Starting fresh.");
            this.users = new HashMap<>();
        }
        */
    }
    public void saveUsers() {
        // TODO: Write this.users to USERS_FILE_PATH using Gson or Jackson.
        // Example with Gson:
        /*
        try (Writer writer = new FileWriter(USERS_FILE_PATH)) {
            new Gson().toJson(this.users, writer);
        } catch (IOException e) {
            System.out.println("Error saving users!");
            e.printStackTrace();
        }
        */
    }
    public void addUser(User newUser) {
        users.put(newUser.getUsername(), newUser);
        saveUsers(); // Immediately save to the JSON file
    }

    public static Menu getCurrentMenu() {
        return currentMenu;
    }
}
