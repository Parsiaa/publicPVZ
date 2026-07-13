package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import models.LevelRule;
import models.MatchState;
import models.Plant;
import models.SeedPacket;
import models.Tile;
import models.Zombie;
import models.Enums.ChapterType;
import models.Enums.Menu;
import models.Enums.TileType;
import models.PlantTypes.SunProducer;
import models.Rules.BowlingRule;
import models.Rules.ConveyorBeltRule;
import models.Rules.IZombieRule;
import models.Rules.PlantWhatYouGetRule;
import models.Rules.VasebreakerRule;
import utils.PlantFactory;
import utils.Result;
import utils.UserApp;
import views.MatchRenderer;

/**
 * Runs a single match: advances time tick by tick and executes the in-game
 * commands. One tick is 0.1 in-game seconds. User-facing coordinates are
 * 1-based (x = column, y = row); internally everything is 0-based.
 */
public class GameEngine {

    private final UserApp userApp;
    private final MenuRouter router;
    private final MiniGameActions miniGameActions;
    private final ScoreTracker scoreTracker;
    private final MatchRewardHandler rewardHandler;

    private MatchState state;
    private SunManager sunManager;
    private WaveManager waveManager;
    private ChapterMechanics mechanics;
    private LevelConfig pendingConfig;
    private LevelConfig activeConfig;
    private Random random;
    private boolean scoreMode;
    private boolean matchOver;
    private boolean playerWon;

    public GameEngine(UserApp userApp, MenuRouter router) {
        this.userApp = userApp;
        this.router = router;
        this.miniGameActions = new MiniGameActions(this);
        this.scoreTracker = new ScoreTracker();
        this.rewardHandler = new MatchRewardHandler(userApp);
        this.random = new Random();
    }

    public void setPendingLevel(LevelConfig config) {
        this.pendingConfig = config;
    }

    public LevelConfig getPendingConfig() {
        return pendingConfig;
    }

    public void setScoreMode(boolean scoreMode) {
        this.scoreMode = scoreMode;
    }

    public void startMatch(MatchState matchState) {
        this.state = matchState;
        this.activeConfig = pendingConfig;
        this.pendingConfig = null;
        this.matchOver = false;
        this.playerWon = false;
        this.random = scoreMode ? new Random(LocalDate.now().toEpochDay()) : new Random();
        ChapterType chapter = activeConfig != null ? activeConfig.getChapterType() : ChapterType.EGYPT;
        state.setChapterType(chapter);
        this.mechanics = LevelFactory.createMechanics(chapter, random);
        this.sunManager = new SunManager(random);
        this.waveManager = new WaveManager(random, mechanics);
        applyRules();
        mechanics.onMatchStart(state);
        for (LevelRule rule : state.getActiveRules()) {
            rule.onMatchStart(state);
            if (rule.getRuleInfo() != null) {
                System.out.println(rule.getRuleInfo());
            }
        }
        scoreTracker.reset(scoreMode);
    }

    private void applyRules() {
        if (activeConfig != null) {
            for (LevelRule rule : activeConfig.getRules()) {
                state.addActiveRule(rule);
            }
        }
        boolean hasOwnZombies = findRule(VasebreakerRule.class) != null || findRule(IZombieRule.class) != null;
        if (!hasOwnZombies) {
            waveManager.generateWaves(state);
        }
        if (findRule(PlantWhatYouGetRule.class) != null) {
            waveManager.hold();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends LevelRule> T findRule(Class<T> type) {
        if (state == null) {
            return null;
        }
        for (LevelRule rule : state.getActiveRules()) {
            if (type.isInstance(rule)) {
                return (T) rule;
            }
        }
        return null;
    }

    public Result handleAdvanceTime(int ticks) {
        Result guard = requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        if (ticks < 1) {
            return new Result("Error: Tick count must be at least 1.", false);
        }
        for (int i = 0; i < ticks && !matchOver; i++) {
            processTick();
        }
        if (matchOver) {
            return new Result(playerWon ? "You won the level!" : "You lost the level.", true);
        }
        return new Result("Advanced " + ticks + " ticks. Current tick: " + state.getCurrentTick(), true);
    }

    private void processTick() {
        state.incrementTick();
        tickSeedPackets();
        actPlants();
        tickZombieEffects();
        moveZombies();
        sunManager.update(state);
        waveManager.update(state);
        mechanics.onTick(state);
        for (LevelRule rule : new ArrayList<>(state.getActiveRules())) {
            rule.onTick(state);
        }
        scoreTracker.onWaveChange(state, waveManager.getCurrentWaveNumber());
        cleanUpDeadPlants();
        cleanUpDeadZombies();
        checkLossConditions();
        if (!matchOver) {
            checkWinConditions();
        }
    }

    private void tickSeedPackets() {
        for (SeedPacket packet : state.getSeedPackets()) {
            if (packet.getCurrentCooldown() > 0) {
                packet.setCurrentCooldown(Math.max(0, packet.getCurrentCooldown() - 0.1));
            }
        }
    }

    private void actPlants() {
        for (Plant plant : new ArrayList<>(state.getMap().getAllPlants())) {
            Tile tile = state.getMap().getTile((int) plant.getY(), (int) plant.getX());
            boolean frozenInIce = tile != null && tile.getType() == TileType.ICE;
            if (!plant.isDead() && !frozenInIce) {
                plant.act(state);
            }
        }
    }

    private void tickZombieEffects() {
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (zombie.getCurrentHealth() > 0) {
                zombie.tickEffects();
                zombie.onTick(state);
            }
        }
    }

    private void moveZombies() {
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (zombie.getCurrentHealth() <= 0 || zombie.getEffects().contains("sunproducer")) {
                continue;
            }
            Tile tile = state.getMap().getTile((int) zombie.getY(),
                    (int) Math.max(0, Math.floor(zombie.getX())));
            if (tile != null && tile.getType() == TileType.ICE) {
                continue;
            }
            zombie.move(state);
        }
    }

    private void cleanUpDeadPlants() {
        for (Plant plant : new ArrayList<>(state.getMap().getAllPlants())) {
            if (plant.isDead()) {
                plant.onDeath(state);
                state.getMap().removePlant(plant);
                state.incrementLostPlantsCount();
            }
        }
    }

    private void cleanUpDeadZombies() {
        int killsThisTick = 0;
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (zombie.getCurrentHealth() <= 0) {
                state.getMap().removeZombie(zombie);
                state.incrementKilledZombiesCount();
                killsThisTick++;
                rewardHandler.dropZombieLoot(state, zombie, random);
            }
        }
        scoreTracker.onKills(state, killsThisTick);
    }

    private void checkLossConditions() {
        if (!state.isPlayingAsZombie()) {
            for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
                if (zombie.getCurrentHealth() > 0 && zombie.getX() < 0) {
                    int row = (int) zombie.getY();
                    if (state.getMap().hasLawnMower(row)) {
                        state.getMap().triggerLawnMower(row);
                    } else {
                        loseMatch();
                        return;
                    }
                }
            }
        }
        for (LevelRule rule : state.getActiveRules()) {
            if (rule.checkLossCondition(state)) {
                loseMatch();
                return;
            }
        }
    }

    private void checkWinConditions() {
        for (LevelRule rule : state.getActiveRules()) {
            if (rule.checkWinCondition(state)) {
                winMatch();
                return;
            }
        }
        if (waveManager.allWavesDefeated(state)) {
            winMatch();
        }
    }

    private void loseMatch() {
        System.out.println("The zombie ate your brain; LOSER!!!");
        endMatch(false);
    }

    private void winMatch() {
        System.out.println("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
        endMatch(true);
    }

    private void endMatch(boolean won) {
        this.matchOver = true;
        this.playerWon = won;
        rewardHandler.awardMatchEnd(state, activeConfig, won);
        if (state.getUser() != null) {
            scoreTracker.finish(state.getUser(), won);
        }
        this.scoreMode = false;
        router.navigateTo(Menu.GameMenu);
    }

    public Result handlePlant(String type, int x, int y) {
        Result guard = requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        ConveyorBeltRule conveyor = findRule(ConveyorBeltRule.class);
        if (conveyor != null) {
            return miniGameActions.plantFromConveyor(conveyor, type, x, y);
        }
        BowlingRule bowling = findRule(BowlingRule.class);
        if (bowling != null) {
            return miniGameActions.plantBowlingNut(bowling, type, x, y);
        }
        SeedPacket packet = findPacket(type);
        if (packet == null) {
            return new Result("Error: Plant '" + type + "' is not among your selected plants.", false);
        }
        boolean freeBuildPhase = findRule(PlantWhatYouGetRule.class) != null && !waveManager.isReleased();
        if (!freeBuildPhase && !packet.isReadyToPlant()) {
            return new Result("Error: " + packet.getPlantName() + " is recharging; ready in "
                    + String.format("%.1f", packet.getCurrentCooldown()) + " seconds.", false);
        }
        Plant plant = PlantFactory.createPlant(packet.getPlantName());
        if (plant == null) {
            return new Result("Error: Plant '" + type + "' doesn't exist.", false);
        }
        if (state.getSunAmount() < plant.getCost()) {
            return new Result("Error: Not enough sun. " + plant.getName() + " costs " + plant.getCost()
                    + " but you have " + state.getSunAmount() + ".", false);
        }
        Result placement = placePlant(plant, x, y);
        if (placement != null) {
            return placement;
        }
        state.setSunAmount(state.getSunAmount() - plant.getCost());
        if (packet.getExpirationTimer() > 0) {
            state.getSeedPackets().remove(packet);
        } else if (!freeBuildPhase) {
            packet.startCooldown();
        }
        PlantingHelper.applyBoostIfAny(state, packet, plant);
        return new Result(plant.getName() + " planted at (" + x + ", " + y + ").", true);
    }

    /** Validates the tile and puts the plant on the map; returns an error Result or null on success. */
    Result placePlant(Plant plant, int x, int y) {
        return PlantingHelper.placePlant(state, plant, x, y);
    }

    public Result handlePluck(int x, int y) {
        Result guard = requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        Tile tile = tileAt(x, y);
        if (tile == null) {
            return new Result("Error: (" + x + ", " + y + ") is outside the map.", false);
        }
        Plant top = tile.getTopPlant();
        if (top == null) {
            return new Result("Error: There is no plant at (" + x + ", " + y + ").", false);
        }
        state.getMap().removePlant(top);
        return new Result(top.getName() + " was plucked from (" + x + ", " + y + ").", true);
    }

    public Result handleFeedPlant(int x, int y) {
        Result guard = requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        Tile tile = tileAt(x, y);
        Plant top = tile != null ? tile.getTopPlant() : null;
        if (top == null) {
            return new Result("Error: There is no plant at (" + x + ", " + y + ").", false);
        }
        if (!state.consumePlantFood()) {
            return new Result("Error: You have no plant food.", false);
        }
        top.triggerPlantFood(state);
        scoreTracker.onPlantFood(state);
        return new Result("Plant food used on " + top.getName() + "; you have "
                + state.getPlantFoods() + " plant foods left.", true);
    }

    public Result handleCollectSun(int x, int y) {
        Result guard = requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        Tile tile = tileAt(x, y);
        if (tile == null) {
            return new Result("Error: (" + x + ", " + y + ") is outside the map.", false);
        }
        for (Plant plant : tile.getPlants()) {
            if (plant instanceof SunProducer && ((SunProducer) plant).hasPendingSun()) {
                int collected = ((SunProducer) plant).collectSun(state);
                return new Result("Collected " + collected + " sun from " + plant.getName()
                        + ". Sun: " + state.getSunAmount(), true);
            }
        }
        int collected = sunManager.collectAt(state, y - 1, x - 1);
        if (collected > 0) {
            return new Result("Collected " + collected + " sun. Sun: " + state.getSunAmount(), true);
        }
        if (collected == 0) {
            return new Result("The radioactive sun exploded!", true);
        }
        return new Result("Error: There is no sun to collect at (" + x + ", " + y + ").", false);
    }

    public Result handleBreakVase(int x, int y) {
        Result guard = requireActiveMatch();
        return guard != null ? guard : miniGameActions.breakVase(x, y);
    }

    public Result handlePlaceZombie(String type, int x, int y) {
        Result guard = requireActiveMatch();
        return guard != null ? guard : miniGameActions.placeZombie(type, x, y);
    }

    public Result handleStartZombieWaves() {
        Result guard = requireActiveMatch();
        return guard != null ? guard : rewardHandler.startZombieWaves(waveManager);
    }

    public Result handleShowSunAmount() {
        Result guard = requireActiveMatch();
        return guard != null ? guard : new Result("Sun: " + state.getSunAmount(), true);
    }

    public Result handleShowMap() {
        Result guard = requireActiveMatch();
        return guard != null
                ? guard : new Result(MatchRenderer.renderMap(state, waveManager.getCurrentWaveNumber()), true);
    }

    public Result handleShowPlantsStatus() {
        Result guard = requireActiveMatch();
        return guard != null ? guard : new Result(MatchRenderer.renderPlantsStatus(state), true);
    }

    public Result handleShowTileStatus(int x, int y) {
        Result guard = requireActiveMatch();
        if (guard != null) {
            return guard;
        }
        Tile tile = tileAt(x, y);
        if (tile == null) {
            return new Result("Error: (" + x + ", " + y + ") is outside the map.", false);
        }
        return new Result(MatchRenderer.renderTileStatus(tile), true);
    }

    public Result handleZombiesInfo() {
        Result guard = requireActiveMatch();
        return guard != null ? guard : new Result(MatchRenderer.renderZombiesInfo(state), true);
    }

    Result requireActiveMatch() {
        if (state == null) {
            return new Result("Error: No active match. Start a game first.", false);
        }
        if (matchOver) {
            return new Result("Error: The match is already over.", false);
        }
        return null;
    }

    Tile tileAt(int x, int y) {
        return state.getMap().getTile(y - 1, x - 1);
    }

    private SeedPacket findPacket(String plantName) {
        for (SeedPacket packet : state.getSeedPackets()) {
            if (packet.getPlantName().equalsIgnoreCase(plantName)) {
                return packet;
            }
        }
        return null;
    }

    public MatchState getState() {
        return state;
    }

    public boolean isMatchOver() {
        return matchOver;
    }

    public boolean isPlayerWon() {
        return playerWon;
    }
}
