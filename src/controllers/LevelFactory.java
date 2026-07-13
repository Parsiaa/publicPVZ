package controllers;

import java.util.List;
import java.util.Random;
import controllers.Chapters.BeachChapter;
import controllers.Chapters.DarkAgesChapter;
import controllers.Chapters.EgyptChapter;
import controllers.Chapters.FrostbiteChapter;
import models.Enums.ChapterType;
import models.Rules.BowlingRule;
import models.Rules.ConveyorBeltRule;
import models.Rules.DeadLineRule;
import models.Rules.IZombieRule;
import models.Rules.LockedPlantsRule;
import models.Rules.LoveYourPlantsRule;
import models.Rules.NightOps;
import models.Rules.PlantWhatYouGetRule;
import models.Rules.SaveOurSeedsRule;
import models.Rules.TimedWarRule;
import models.Rules.VasebreakerRule;
import utils.PlantFactory;

/**
 * Builds the level configuration for each chapter. Every chapter has:
 * level 1 = a normal level, levels 2 and 3 = special levels (all eight
 * special types are covered across the four chapters), level 4 = boss (phase 2).
 */
public final class LevelFactory {

    public static final int PLAYABLE_LEVELS_PER_CHAPTER = 3;

    private LevelFactory() {
    }

    /** Story order of the chapters: Egypt, Frostbite, Beach, Dark Ages. */
    public static int chapterIndex(ChapterType chapter) {
        switch (chapter) {
            case EGYPT: return 1;
            case FROSTBITE: return 2;
            case BEACH: return 3;
            default: return 4;
        }
    }

    public static ChapterMechanics createMechanics(ChapterType chapter, Random random) {
        switch (chapter) {
            case FROSTBITE: return new FrostbiteChapter(random);
            case BEACH: return new BeachChapter(random);
            case DARK_AGES: return new DarkAgesChapter(random);
            default: return new EgyptChapter(random);
        }
    }

    public static ChapterType chapterTypeOf(String name) {
        switch (name.toLowerCase().replace("-", "").replace("_", "").replace(" ", "")) {
            case "egypt": return ChapterType.EGYPT;
            case "frostbite": return ChapterType.FROSTBITE;
            case "beach": return ChapterType.BEACH;
            case "darkages": return ChapterType.DARK_AGES;
            default: return null;
        }
    }

    public static LevelConfig createAdventureLevel(ChapterType chapter, int level,
                                                   List<String> unlockedPlants, Random random) {
        LevelConfig config = new LevelConfig(chapter, level);
        if (level == 1) {
            return config;
        }
        switch (chapter) {
            case EGYPT:
                configureEgypt(config, level, unlockedPlants, random);
                break;
            case FROSTBITE:
                configureFrostbite(config, level, random);
                break;
            case BEACH:
                configureBeach(config, level);
                break;
            default:
                configureDarkAges(config, level);
                break;
        }
        return config;
    }

    private static void configureEgypt(LevelConfig config, int level,
                                       List<String> unlockedPlants, Random random) {
        if (level == 2) {
            config.setConveyor(true);
            config.setInitialSun(0);
            config.addRule(new ConveyorBeltRule(unlockedPlants, random));
        } else {
            if (!unlockedPlants.isEmpty()) {
                config.getBannedPlants().add(unlockedPlants.get(random.nextInt(unlockedPlants.size())));
            }
            config.setMaxSlots(5);
            config.addRule(new LockedPlantsRule(config.getBannedPlants(), config.getMaxSlots()));
        }
    }

    private static void configureFrostbite(LevelConfig config, int level, Random random) {
        if (level == 2) {
            config.addRule(new SaveOurSeedsRule(random));
        } else {
            config.addRule(new TimedWarRule(90, 10));
        }
    }

    private static void configureBeach(LevelConfig config, int level) {
        if (level == 2) {
            config.addRule(new NightOps());
        } else {
            config.addRule(new DeadLineRule(2));
        }
    }

    private static void configureDarkAges(LevelConfig config, int level) {
        if (level == 2) {
            config.addRule(new LoveYourPlantsRule(5));
        } else {
            config.setInitialSun(650);
            config.addRule(new PlantWhatYouGetRule(650));
            banSunProducers(config);
        }
    }

    private static void banSunProducers(LevelConfig config) {
        for (String name : PlantFactory.getAllPlantNames()) {
            if ("Sun Producer".equalsIgnoreCase(PlantFactory.getCategory(name))) {
                config.getBannedPlants().add(name);
            }
        }
    }

    public static LevelConfig createMiniGame(String name, int stage, Random random) {
        LevelConfig config = new LevelConfig(ChapterType.EGYPT, stage);
        config.setMiniGame(true);
        String key = name.toLowerCase();
        if (key.contains("vase")) {
            config.setInitialSun(0);
            config.addRule(new VasebreakerRule(stage, random));
        } else if (key.contains("bowling")) {
            config.setInitialSun(0);
            config.setConveyor(true);
            config.addRule(new BowlingRule(stage, random));
        } else if (key.contains("zombie")) {
            config.setInitialSun(150);
            config.setConveyor(true);
            config.addRule(new IZombieRule(stage, random));
        } else {
            return null;
        }
        return config;
    }
}
