package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import models.Armor;
import models.Zombie;
import models.Blueprints.ArmorData;
import models.Blueprints.ZombieData;
import models.Zombies.BasicZombie;
import models.Zombies.DodoZombie;
import models.Zombies.ExplorerZombie;
import models.Zombies.GargantuarZombie;
import models.Zombies.RaZombie;
import models.Zombies.TombRaiserZombie;


public final class ZombieFactory {

    private static final String ZOMBIES_FILE = "assets/Data/zombies.csv";
    private static final String ARMOR_FILE = "assets/Data/armor.csv";

    /** Zombies that can appear in normal adventure waves in every chapter. */
    private static final List<String> COMMON_WAVE_TYPES = Arrays.asList(
            "Normal", "ConeHead", "BucketHead", "BlockHead", "Knight", "Imp",
            "Gargantuar", "AllStar", "Arcade", "Parasol", "Turquoise",
            "Prospector", "Pianist", "Newspaper");

    private static class ZombieBlueprint {
        private String name;
        private int hitpoints;
        private double speed;
        private int eatDps;
        private int wavePointCost;
        private int weight;
        private List<String> armorAliases;
    }

    private static final Map<String, ZombieBlueprint> BLUEPRINTS = new LinkedHashMap<>();
    private static final Map<String, ArmorData> ARMORS = new HashMap<>();

    static {
        loadArmors();
        loadZombies();
    }

    private ZombieFactory() {
    }

    private static void loadArmors() {
        for (List<String> fields : readCsv(ARMOR_FILE, 4)) {
            List<String> flags = fields.get(3).isEmpty()
                    ? new ArrayList<>() : Arrays.asList(fields.get(3).split(";"));
            ArmorData data = new ArmorData(fields.get(0), fields.get(1),
                    Integer.parseInt(fields.get(2)), flags);
            ARMORS.put(normalize(fields.get(0)), data);
        }
    }

    private static void loadZombies() {
        for (List<String> fields : readCsv(ZOMBIES_FILE, 8)) {
            ZombieBlueprint blueprint = new ZombieBlueprint();
            blueprint.name = fields.get(0);
            blueprint.hitpoints = Integer.parseInt(fields.get(2));
            blueprint.speed = Double.parseDouble(fields.get(3));
            blueprint.eatDps = Integer.parseInt(fields.get(4));
            blueprint.wavePointCost = Integer.parseInt(fields.get(5));
            blueprint.weight = Integer.parseInt(fields.get(6));
            blueprint.armorAliases = fields.get(7).isEmpty()
                    ? new ArrayList<>() : Arrays.asList(fields.get(7).split(";"));
            BLUEPRINTS.put(normalize(blueprint.name), blueprint);
        }
    }

    private static List<List<String>> readCsv(String file, int minFields) {
        List<List<String>> rows = new ArrayList<>();
        Path path = Paths.get(file);
        if (!Files.exists(path)) {
            System.out.println("Warning: " + file + " not found.");
            return rows;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= minFields) {
                    List<String> fields = new ArrayList<>();
                    for (String part : parts) {
                        fields.add(part.trim().replace("﻿", ""));
                    }
                    rows.add(fields);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + file + ": " + e.getMessage());
        }
        return rows;
    }

    public static Zombie createZombie(String type, int difficultyLevel) {
        ZombieBlueprint blueprint = BLUEPRINTS.get(normalize(type));
        if (blueprint == null) {
            return null;
        }
        double factor = difficultyLevel / 3.0;
        int scaledHp = Math.max(1, (int) Math.round(blueprint.hitpoints * factor));
        int scaledDps = (int) Math.round(blueprint.eatDps * factor);
        ZombieData data = new ZombieData(blueprint.name, blueprint.name, scaledHp,
                blueprint.speed, scaledDps, blueprint.armorAliases, null);
        Zombie zombie = instantiate(blueprint.name);
        zombie.setData(data);
        for (String armorAlias : blueprint.armorAliases) {
            ArmorData armorData = ARMORS.get(normalize(armorAlias));
            if (armorData != null) {
                zombie.addArmor(new Armor(armorData));
            }
        }
        return zombie;
    }

    private static Zombie instantiate(String name) {
        switch (normalize(name)) {
            case "gargantuar": return new GargantuarZombie();
            case "ra": return new RaZombie();
            case "explorer": return new ExplorerZombie();
            case "tombraiser": return new TombRaiserZombie();
            case "dodorider": return new DodoZombie();
            default: return new BasicZombie();
        }
    }

    public static int getWaveCost(String type) {
        ZombieBlueprint blueprint = BLUEPRINTS.get(normalize(type));
        return blueprint != null ? blueprint.wavePointCost : -1;
    }

    public static int getWeight(String type) {
        ZombieBlueprint blueprint = BLUEPRINTS.get(normalize(type));
        return blueprint != null ? blueprint.weight : 0;
    }

    public static boolean isKnownZombie(String type) {
        return BLUEPRINTS.containsKey(normalize(type));
    }

    public static List<String> getSpawnableTypes() {
        List<String> types = new ArrayList<>();
        for (String type : COMMON_WAVE_TYPES) {
            if (BLUEPRINTS.containsKey(normalize(type))) {
                types.add(type);
            }
        }
        return types;
    }

    public static List<String> getAllZombieNames() {
        List<String> names = new ArrayList<>();
        for (ZombieBlueprint blueprint : BLUEPRINTS.values()) {
            names.add(blueprint.name);
        }
        return names;
    }

    private static String normalize(String type) {
        if (type == null) {
            return "";
        }
        return type.toLowerCase().replace("-", "").replace("_", "").replace(" ", "");
    }
}
