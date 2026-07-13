package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import models.Pot;
import models.Quest;
import models.User;
import models.Enums.QuestCategory;
import models.Enums.QuestPriority;
import models.Reward.CurrencyReward;
import models.Reward.InventoryReward;
import models.Reward.Reward;
import models.Reward.UnlockableReward;

public final class SaveManager {

    private static final String SAVE_FILE = "data/users.txt";
    private static final String USER_HEADER = "[user]";

    private SaveManager() {
    }

    public static void save(Map<String, User> users) {
        try {
            Path path = Paths.get(SAVE_FILE);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
                for (User user : users.values()) {
                    writeUser(writer, user);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save users: " + e.getMessage());
        }
    }

    private static void writeUser(PrintWriter writer, User user) {
        writer.println(USER_HEADER);
        writeField(writer, "username", user.getUsername());
        writeField(writer, "password", user.getPassword());
        writeField(writer, "passwordHash", user.getPasswordHash());
        writeField(writer, "nickname", user.getNickname());
        writeField(writer, "email", user.getEmail());
        writeField(writer, "gender", user.getGender());
        writeField(writer, "coins", String.valueOf(user.getCoins()));
        writeField(writer, "gems", String.valueOf(user.getGems()));
        writeField(writer, "securityQuestion", user.getSecurityQuestion());
        writeField(writer, "securityAnswer", user.getSecurityAnswer());
        writeField(writer, "storedStartingPlantFoods", String.valueOf(user.getStoredStartingPlantFoods()));
        writeField(writer, "highestChapter", String.valueOf(user.getHighestChapter()));
        writeField(writer, "highestLevel", String.valueOf(user.getHighestLevel()));
        writeField(writer, "miniGamesCompleted", String.valueOf(user.getMiniGamesCompleted()));
        writeField(writer, "dailyQuestsCompleted", String.valueOf(user.getDailyQuestsCompleted()));
        writeField(writer, "notDailyQuestsCompleted", String.valueOf(user.getNotDailyQuestsCompleted()));
        writeField(writer, "highestMeowPointScore", String.valueOf(user.getHighestMeowPointScore()));
        writeField(writer, "stayLoggedIn", String.valueOf(user.getStayLoggedIn()));
        writeField(writer, "unlockedPlants", String.join(";", user.getUnlockedPlants()));
        writeField(writer, "boostedPlants", String.join(";", user.getBoostedPlants()));
        writeField(writer, "seedPackets", mapToString(user.getSeedPacketInventory()));
        writeField(writer, "plantLevels", mapToString(user.getPlantLevels()));
        writeField(writer, "completedLevels", mapToString(user.getCompletedLevelsByChapter()));
        writeField(writer, "pots", potsToString(user.getGreenhousePots()));
        writeField(writer, "quests", questsToString(user.getActiveQuests()));
    }

    private static void writeField(PrintWriter writer, String key, String value) {
        writer.println(key + "=" + (value == null ? "" : value));
    }

    private static String mapToString(Map<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    private static String potsToString(Pot[][] pots) {
        StringBuilder sb = new StringBuilder();
        for (Pot[] row : pots) {
            for (Pot pot : row) {
                if (sb.length() > 0) {
                    sb.append(";");
                }
                sb.append(pot.isUnlocked() ? 1 : 0).append(",")
                        .append(pot.isEmpty() ? 1 : 0).append(",")
                        .append(pot.getPlantedPlantName() == null ? "-" : pot.getPlantedPlantName()).append(",")
                        .append(pot.isMarigold() ? 1 : 0).append(",")
                        .append(pot.getPlantTime() == null ? "-" : pot.getPlantTime());
            }
        }
        return sb.toString();
    }

    private static String questsToString(java.util.List<Quest> quests) {
        if (quests == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Quest quest : quests) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(quest.getId()).append("|").append(quest.getTitle()).append("|")
                    .append(quest.getPriority()).append("|").append(quest.getCategory()).append("|")
                    .append(quest.getConditionType()).append("|").append(quest.getCurrentProgress())
                    .append("|").append(quest.getTargetProgress()).append("|")
                    .append(rewardToString(quest.getReward()));
        }
        return sb.toString();
    }

    private static String rewardToString(Reward reward) {
        if (reward instanceof CurrencyReward) {
            CurrencyReward r = (CurrencyReward) reward;
            return "currency:" + r.getCurrentType() + ":" + r.getAmount();
        }
        if (reward instanceof UnlockableReward) {
            UnlockableReward r = (UnlockableReward) reward;
            return "unlockable:" + r.getCurrentType() + ":" + r.getTargetName();
        }
        if (reward instanceof InventoryReward) {
            InventoryReward r = (InventoryReward) reward;
            return "inventory:" + r.getItemType() + ":" + r.getAmount();
        }
        return "none";
    }

    public static Map<String, User> load() {
        Map<String, User> users = new LinkedHashMap<>();
        Path path = Paths.get(SAVE_FILE);
        if (!Files.exists(path)) {
            return users;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Map<String, String> fields = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(USER_HEADER)) {
                    addUser(users, fields);
                    fields = new LinkedHashMap<>();
                } else if (fields != null && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    fields.put(parts[0], parts[1]);
                }
            }
            addUser(users, fields);
        } catch (IOException e) {
            System.out.println("Warning: could not load users: " + e.getMessage());
        }
        return users;
    }

    private static void addUser(Map<String, User> users, Map<String, String> fields) {
        if (fields == null || !fields.containsKey("username")) {
            return;
        }
        User user = buildUser(fields);
        users.put(user.getUsername(), user);
    }

    private static User buildUser(Map<String, String> f) {
        User user = new User(f.get("username"), emptyToNull(f.get("password")));
        user.setPasswordHash(emptyToNull(f.get("passwordHash")));
        user.setNickname(emptyToNull(f.get("nickname")));
        user.setEmail(emptyToNull(f.get("email")));
        user.setGender(emptyToNull(f.get("gender")));
        user.setCoins(intOf(f, "coins"));
        user.setGems(intOf(f, "gems"));
        user.setSecurityQuestion(emptyToNull(f.get("securityQuestion")));
        user.setSecurityAnswer(emptyToNull(f.get("securityAnswer")));
        user.setStoredStartingPlantFoods(intOf(f, "storedStartingPlantFoods"));
        user.setHighestChapter(intOf(f, "highestChapter"));
        user.setHighestLevel(intOf(f, "highestLevel"));
        user.setMiniGamesCompleted(intOf(f, "miniGamesCompleted"));
        user.setDailyQuestsCompleted(intOf(f, "dailyQuestsCompleted"));
        user.setNotDailyQuestsCompleted(intOf(f, "notDailyQuestsCompleted"));
        user.setHighestMeowPointScore(intOf(f, "highestMeowPointScore"));
        user.setStayLoggedIn(Boolean.parseBoolean(f.getOrDefault("stayLoggedIn", "false")));
        user.setUnlockedPlants(new java.util.HashSet<>(listOf(f.get("unlockedPlants"))));
        user.setBoostedPlants(new java.util.HashSet<>(listOf(f.get("boostedPlants"))));
        user.setSeedPacketInventory(stringToMap(f.get("seedPackets")));
        user.setPlantLevels(stringToMap(f.get("plantLevels")));
        user.setCompletedLevelsByChapter(stringToMap(f.get("completedLevels")));
        loadPots(user, f.get("pots"));
        user.setActiveQuests(stringToQuests(f.get("quests")));
        return user;
    }

    private static void loadPots(User user, String raw) {
        if (raw == null || raw.isEmpty()) {
            return;
        }
        String[] entries = raw.split(";");
        Pot[][] pots = user.getGreenhousePots();
        int index = 0;
        for (int y = 0; y < pots.length && index < entries.length; y++) {
            for (int x = 0; x < pots[y].length && index < entries.length; x++, index++) {
                String[] p = entries[index].split(",");
                if (p.length < 5) {
                    continue;
                }
                LocalDateTime time = p[4].equals("-") ? null : LocalDateTime.parse(p[4]);
                pots[y][x].loadState(p[0].equals("1"), p[1].equals("1"),
                        p[2].equals("-") ? null : p[2], time, p[3].equals("1"));
            }
        }
    }

    private static java.util.List<Quest> stringToQuests(String raw) {
        java.util.List<Quest> quests = new ArrayList<>();
        if (raw == null || raw.isEmpty()) {
            return quests;
        }
        for (String entry : raw.split(";")) {
            String[] p = entry.split("\\|");
            if (p.length < 8) {
                continue;
            }
            quests.add(new Quest(p[0], p[1], QuestPriority.valueOf(p[2]), QuestCategory.valueOf(p[3]),
                    stringToReward(p[7]), p[4], Integer.parseInt(p[5]), Integer.parseInt(p[6])));
        }
        return quests;
    }

    private static Reward stringToReward(String spec) {
        String[] p = spec.split(":");
        if (p.length < 3) {
            return null;
        }
        switch (p[0]) {
            case "currency": return new CurrencyReward(p[1], Integer.parseInt(p[2]));
            case "unlockable": return new UnlockableReward(p[1], p[2]);
            case "inventory": return new InventoryReward(p[1], Integer.parseInt(p[2]));
            default: return null;
        }
    }

    private static Map<String, Integer> stringToMap(String raw) {
        Map<String, Integer> map = new java.util.HashMap<>();
        if (raw == null || raw.isEmpty()) {
            return map;
        }
        for (String entry : raw.split(";")) {
            int idx = entry.lastIndexOf(':');
            if (idx > 0) {
                map.put(entry.substring(0, idx), Integer.parseInt(entry.substring(idx + 1)));
            }
        }
        return map;
    }

    private static java.util.List<String> listOf(String raw) {
        java.util.List<String> list = new ArrayList<>();
        if (raw != null && !raw.isEmpty()) {
            for (String item : raw.split(";")) {
                if (!item.isEmpty()) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    private static int intOf(Map<String, String> fields, String key) {
        try {
            return Integer.parseInt(fields.getOrDefault(key, "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }
}
