package models;

import java.util.ArrayList;
import java.util.List;


public class MatchState {
    private User user;
    private Map map; // Note: Ensure this refers to your custom models.Map, not java.util.Map!
    private List<SeedPacket> seedPackets;

    private List<Wave> waves;
    private Wave currentWave;
    private int currentTick;
    private int difficultyLevel;

    private int sunAmount;
    private int uncollectedSuns;
    private int plantFoods;

    private int lootedCoins;
    private int lootedGems;
    private int lootedPots;

    private boolean isNightTime;
    private List<LevelRule> activeRules;
    private int lostPlantsCount;
    private boolean isPlayingAsZombie;
    private int targetMatchCount;
    private models.Enums.ChapterType chapterType;
    private int killedZombiesCount;


    public MatchState(User user, int initialSun, int difficultyLevel) {
        this.user = user;
        this.map = new Map();
        this.seedPackets = new ArrayList<>();
        this.waves = new ArrayList<>();
        this.activeRules = new ArrayList<>();

        this.sunAmount = initialSun;
        this.difficultyLevel = difficultyLevel;
        this.currentTick = 0;
        this.uncollectedSuns = 0;

        this.lootedCoins = 0;
        this.lootedGems = 0;
        this.lootedPots = 0;
        this.lostPlantsCount = 0;

        this.isNightTime = false;
        this.isPlayingAsZombie = false;
        this.targetMatchCount = 0;
    }

    public void initializeFromUser(User u) {
        this.user = u;
        this.plantFoods = Math.min(u.getStoredStartingPlantFoods(), 3);

        for (SeedPacket packet : seedPackets) {
            if (u.hasBoostFor(packet.getPlantName())) {
                packet.setHasStoredBoost(true);
            }
        }
    }
    public void addSun(int amount) {
        this.sunAmount += amount;
    }
    public boolean consumePlantFood() {
        if (this.plantFoods > 0) {
            this.plantFoods--;
            return true;
        }
        return false;
    }
    public void addLoot(int coins, int gems, int pots) {
        this.lootedCoins += coins;
        this.lootedGems += gems;
        this.lootedPots += pots;
    }
    public boolean isPreviousWavesDefeatedby(int currentWaveIndex) {
        for (int i = 0; i < currentWaveIndex; i++) {
            Wave pastWave = waves.get(i);
            if (!pastWave.isCompletelyDefeated()) {
                return false;
            }
        }
        return true;
    }
    public Wave getNextWave() {
        int nextIndex = (currentWave == null) ? 0 : waves.indexOf(currentWave) + 1;
        if (nextIndex < waves.size()) {
            currentWave = waves.get(nextIndex);
            return currentWave;
        }
        return null;
    }
    public User getUser() { return user; }

    public Map getMap() { return map; }

    public int getSunAmount() { return sunAmount; }
    public void setSunAmount(int sunAmount) { this.sunAmount = sunAmount; }

    public int getCurrentTick() { return currentTick; }
    public void incrementTick() { this.currentTick++; }

    public int getUncollectedSuns() { return uncollectedSuns; }
    public void setUncollectedSuns(int uncollectedSuns) { this.uncollectedSuns = uncollectedSuns; }

    public int getPlantFoods() { return plantFoods; }
    public void addPlantFood() { this.plantFoods = Math.min(this.plantFoods + 1, 3); }

    public List<SeedPacket> getSeedPackets() { return seedPackets; }
    public void setSeedPackets(List<SeedPacket> seedPackets) { this.seedPackets = seedPackets; }

    public List<Wave> getWaves() { return waves; }
    public void setWaves(List<Wave> waves) { this.waves = waves; }

    public Wave getCurrentWave() { return currentWave; }

    public int getLootedCoins() { return lootedCoins; }
    public int getLootedGems() { return lootedGems; }
    public int getLootedPots() { return lootedPots; }

    public int getDifficultyLevel() { return difficultyLevel; }

    public boolean isNightTime() { return isNightTime; }
    public void setNightTime(boolean nightTime) { isNightTime = nightTime; }

    public List<LevelRule> getActiveRules() { return activeRules; }
    public void addActiveRule(LevelRule rule) { this.activeRules.add(rule); }

    public int getLostPlantsCount() { return lostPlantsCount; }
    public void incrementLostPlantsCount() { this.lostPlantsCount++; }

    public boolean isPlayingAsZombie() { return isPlayingAsZombie; }
    public void setPlayingAsZombie(boolean playingAsZombie) { isPlayingAsZombie = playingAsZombie; }

    public int getTargetMatchCount() { return targetMatchCount; }
    public void setTargetMatchCount(int targetMatchCount) { this.targetMatchCount = targetMatchCount; }

    public models.Enums.ChapterType getChapterType() { return chapterType; }
    public void setChapterType(models.Enums.ChapterType chapterType) { this.chapterType = chapterType; }

    public int getKilledZombiesCount() { return killedZombiesCount; }
    public void incrementKilledZombiesCount() { this.killedZombiesCount++; }
    
}
