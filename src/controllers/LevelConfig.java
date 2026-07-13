package controllers;

import java.util.ArrayList;
import java.util.List;
import models.LevelRule;
import models.Enums.ChapterType;

/**
 * Everything that defines one playable level: its chapter, number,
 * special rules and the restrictions used during plant selection.
 */
public class LevelConfig {

    private final ChapterType chapterType;
    private final int levelNumber;
    private final List<LevelRule> rules = new ArrayList<>();
    private final List<String> bannedPlants = new ArrayList<>();
    private boolean conveyor;
    private boolean miniGame;
    private int maxSlots = 8;
    private int initialSun = 50;

    public LevelConfig(ChapterType chapterType, int levelNumber) {
        this.chapterType = chapterType;
        this.levelNumber = levelNumber;
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public List<LevelRule> getRules() {
        return rules;
    }

    public void addRule(LevelRule rule) {
        rules.add(rule);
    }

    public List<String> getBannedPlants() {
        return bannedPlants;
    }

    public boolean isPlantBanned(String plantName) {
        for (String banned : bannedPlants) {
            if (banned.equalsIgnoreCase(plantName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isConveyor() {
        return conveyor;
    }

    public void setConveyor(boolean conveyor) {
        this.conveyor = conveyor;
    }

    public boolean isMiniGame() {
        return miniGame;
    }

    public void setMiniGame(boolean miniGame) {
        this.miniGame = miniGame;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public int getInitialSun() {
        return initialSun;
    }

    public void setInitialSun(int initialSun) {
        this.initialSun = initialSun;
    }
}
