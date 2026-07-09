package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.Enums.PlantTag;
import models.PlantTypes.Explosive;
import models.PlantTypes.Lobber;
import models.PlantTypes.MeleeAttack;
import models.PlantTypes.Mint;
import models.PlantTypes.Modifier;
import models.PlantTypes.Shooter;
import models.PlantTypes.Strikethrough;
import models.PlantTypes.SunProducer;
import models.PlantTypes.WallNut;

// Builds concrete Plant instances by name. Lives in the models package so it
// can populate Plant's protected action/recharge fields directly. Stats are
// hand-authored (there is no plants.csv in the repo) and follow the doc's plant
// categories; they can be swapped for CSV-loaded data later without changing
// callers.
public final class PlantFactory {

    private PlantFactory() {
    }

    public static boolean isKnown(String name) {
        return create(name) != null;
    }

    public static Plant create(String name) {
        if (name == null) {
            return null;
        }
        switch (name.trim().toLowerCase()) {
            case "sunflower":
                return configure(new SunProducer(), "Sunflower", 50, 300, 0, 24.0, 7.5,
                        PlantTag.DAY, PlantTag.SUN);
            case "peashooter":
                return configure(new Shooter(), "Peashooter", 100, 300, 20, 1.4, 7.5,
                        PlantTag.DAY, PlantTag.PEA);
            case "wallnut":
                return configure(new WallNut(), "WallNut", 50, 4000, 0, 0.0, 30.0,
                        PlantTag.DAY);
            case "cherrybomb":
                return configure(new Explosive(), "CherryBomb", 150, 300, 1800, 0.0, 50.0,
                        PlantTag.EXPLOSIVE, PlantTag.FIRE);
            case "cabbagepult":
                return configure(new Lobber(), "Cabbagepult", 100, 300, 40, 3.0, 7.5,
                        PlantTag.DAY);
            case "chomper":
                return configure(new MeleeAttack(), "Chomper", 150, 300, 100, 4.2, 7.5,
                        PlantTag.DAY);
            case "iceberglettuce":
                return configure(new Modifier(), "IcebergLettuce", 0, 300, 0, 0.0, 30.0,
                        PlantTag.ICE);
            case "bonkchoy":
                return configure(new MeleeAttack(), "BonkChoy", 150, 300, 30, 0.8, 7.5,
                        PlantTag.DAY);
            case "bloomerang":
                return configure(new Strikethrough(), "Bloomerang", 175, 300, 30, 1.4, 7.5,
                        PlantTag.DAY, PlantTag.PEA);
            case "peppermint":
                return configure(new Mint(), "Peppermint", 0, 300, 0, 0.0, 50.0,
                        PlantTag.DAY);
            default:
                return null;
        }
    }

    private static Plant configure(Plant plant, String name, int cost, int hp, int damage,
                                   double actionInterval, double rechargeTime, PlantTag... tags) {
        plant.setName(name);
        plant.setCost(cost);
        plant.setMaxHp(hp);
        plant.setHealth(hp);
        plant.setBaseDamage(damage);
        plant.actionInterval = actionInterval;
        plant.rechargeTime = rechargeTime;
        plant.setLevel(1);
        plant.setTags(new ArrayList<>(Arrays.asList(tags)));
        return plant;
    }

    public static List<String> knownPlants() {
        return Arrays.asList("Sunflower", "Peashooter", "WallNut", "CherryBomb", "Cabbagepult",
                "Chomper", "IcebergLettuce", "BonkChoy", "Bloomerang", "Peppermint");
    }
}
