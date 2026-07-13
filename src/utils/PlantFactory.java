package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import models.Plant;
import models.SeedPacket;
import models.Enums.PlantTag;
import models.PlantTypes.Explosive;
import models.PlantTypes.Homing;
import models.PlantTypes.Lobber;
import models.PlantTypes.MeleeAttack;
import models.PlantTypes.Mint;
import models.PlantTypes.Modifier;
import models.PlantTypes.Shooter;
import models.PlantTypes.SunProducer;
import models.PlantTypes.Strikethrough;
import models.PlantTypes.WallNut;
import models.PlantTypes.Plants.*;


public final class PlantFactory {

    private static final String PLANTS_FILE = "assets/Data/plants.csv";
    private static final int INSTA_KILL_DAMAGE = 999999;
    private static final Map<String, PlantBlueprint> BLUEPRINTS = new LinkedHashMap<>();

    private static class PlantBlueprint {
        private String name;
        private String category;
        private List<PlantTag> tags;
        private int cost;
        private int maxHp;
        private int damage;
        private double actionInterval;
        private double rechargeTime;
    }

    static {
        loadBlueprints();
    }

    private PlantFactory() {
    }

    private static void loadBlueprints() {
        Path path = Paths.get(PLANTS_FILE);
        if (!Files.exists(path)) {
            System.out.println("Warning: " + PLANTS_FILE + " not found; no plants loaded.");
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                PlantBlueprint blueprint = parseLine(splitCsvLine(line));
                if (blueprint != null) {
                    BLUEPRINTS.put(normalize(blueprint.name), blueprint);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + PLANTS_FILE + ": " + e.getMessage());
        }
    }

    private static PlantBlueprint parseLine(List<String> fields) {
        if (fields.size() < 14 || fields.get(1).isEmpty()) {
            return null;
        }
        PlantBlueprint blueprint = new PlantBlueprint();
        blueprint.name = fields.get(1);
        blueprint.category = fields.get(2);
        blueprint.tags = parseTags(fields.get(3));
        blueprint.cost = parseIntSafe(fields.get(4), 0);
        blueprint.maxHp = Math.max(1, parseIntSafe(fields.get(5), 300));
        blueprint.damage = parseDamage(fields.get(6));
        blueprint.actionInterval = parseDoubleSafe(fields.get(12), 0);
        blueprint.rechargeTime = parseDoubleSafe(fields.get(13), 5);
        return blueprint;
    }

    private static List<PlantTag> parseTags(String raw) {
        List<PlantTag> tags = new ArrayList<>();
        for (String part : raw.split(",")) {
            String key = normalize(part);
            for (PlantTag tag : PlantTag.values()) {
                if (normalize(tag.name()).equals(key)) {
                    tags.add(tag);
                    break;
                }
            }
        }
        return tags;
    }

    /** Parses damage cells like "20", "20x2", "20/40/60", "Insta-kill" or "-". */
    private static int parseDamage(String raw) {
        String value = raw.trim();
        if (value.toLowerCase().contains("insta")) {
            return INSTA_KILL_DAMAGE;
        }
        if (value.contains("/")) {
            value = value.substring(0, value.indexOf('/'));
        }
        if (value.toLowerCase().contains("x")) {
            String[] parts = value.toLowerCase().split("x");
            return parseIntSafe(parts[0], 0) * Math.max(1, parseIntSafe(parts[1], 1));
        }
        return parseIntSafe(value, 0);
    }

    public static Plant createPlant(String name) {
        PlantBlueprint blueprint = BLUEPRINTS.get(normalize(name));
        if (blueprint == null) {
            return null;
        }
        Plant plant = instantiate(blueprint);
        plant.setName(blueprint.name);
        plant.setCost(blueprint.cost);
        plant.setMaxHp(blueprint.maxHp);
        plant.setHealth(blueprint.maxHp);
        plant.setBaseDamage(blueprint.damage);
        plant.setActionInterval(blueprint.actionInterval);
        plant.setRechargeTime(blueprint.rechargeTime);
        plant.setLevel(1);
        plant.setTags(blueprint.tags);
        return plant;
    }

    private static Plant instantiate(PlantBlueprint blueprint) {
        if (normalize(blueprint.name).endsWith("mint")) {
            return new Mint();
        }
        switch (normalize(blueprint.category)) {
            case "sunproducer": return new SunProducer();
            case "shooter": return new Shooter();
            case "lobber": return new Lobber();
            case "explosive": return new Explosive();
            case "melee": return new MeleeAttack();
            case "wallnut": return new WallNut();
            case "modifier": return new Modifier();
            case "strikethrough": return new Strikethrough();
            case "homing": return new Homing();
            default: return new Modifier();
        }
    }

    public static SeedPacket createSeedPacket(String name) {
        PlantBlueprint blueprint = BLUEPRINTS.get(normalize(name));
        if (blueprint == null) {
            return null;
        }
        return new SeedPacket(blueprint.name, blueprint.cost, blueprint.rechargeTime);
    }

    public static boolean isKnownPlant(String name) {
        return BLUEPRINTS.containsKey(normalize(name));
    }

    public static String properName(String name) {
        PlantBlueprint blueprint = BLUEPRINTS.get(normalize(name));
        return blueprint != null ? blueprint.name : null;
    }

    public static int getCost(String name) {
        PlantBlueprint blueprint = BLUEPRINTS.get(normalize(name));
        return blueprint != null ? blueprint.cost : -1;
    }

    public static String getCategory(String name) {
        PlantBlueprint blueprint = BLUEPRINTS.get(normalize(name));
        return blueprint != null ? blueprint.category : null;
    }

    public static List<String> getAllPlantNames() {
        List<String> names = new ArrayList<>();
        for (PlantBlueprint blueprint : BLUEPRINTS.values()) {
            names.add(blueprint.name);
        }
        return names;
    }

    private static List<String> splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (char ch : line.toCharArray()) {
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString().trim());
        return fields;
    }

    private static int parseIntSafe(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double parseDoubleSafe(String value, double fallback) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String normalize(String name) {
        if (name == null) {
            return "";
        }
        return name.toLowerCase().replace("﻿", "").replace("-", "").replace("_", "").replace(" ", "");
    }
}
