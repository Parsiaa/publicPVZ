package controllers;

import java.util.ArrayList;
import java.util.List;
import models.MatchState;
import models.SeedPacket;
import models.User;
import models.Enums.Menu;
import utils.PlantFactory;
import utils.Result;
import utils.UserApp;

public class PlantSelectionMenuController {
    private UserApp userApp;
    private MenuRouter router;
    private CollectionMenuController collectionController;
    private GameEngine gameEngine;
    private List<SeedPacket> selectedPackets;
    private static final int MAX_SLOTS = 8;
    private static final int BOOST_GEM_COST = 2;

    public PlantSelectionMenuController(UserApp userApp, MenuRouter router,
                                        CollectionMenuController collectionController) {
        this.userApp = userApp;
        this.router = router;
        this.collectionController = collectionController;
        this.selectedPackets = new ArrayList<>();
    }

    public Result showAllPlantsInLevel() {
        StringBuilder sb = new StringBuilder("All plants:\n");
        for (String name : PlantFactory.getAllPlantNames()) {
            sb.append("- ").append(name).append(" (cost: ").append(PlantFactory.getCost(name)).append(")\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result showAvailablePlantsInLevel() {
        StringBuilder sb = new StringBuilder("Available plants:\n");
        boolean any = false;
        for (String name : PlantFactory.getAllPlantNames()) {
            if (collectionController.isPlantUnlocked(name)) {
                sb.append("- ").append(name).append(" (cost: ").append(PlantFactory.getCost(name)).append(")\n");
                any = true;
            }
        }
        if (!any) {
            return new Result("No plants available in this level.", true);
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result addPlantToSlot(String plantName) {
        String properName = PlantFactory.properName(plantName);
        if (properName == null) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        if (!collectionController.isPlantUnlocked(properName)) {
            return new Result("Error: Plant '" + properName + "' is locked.", false);
        }
        LevelConfig config = gameEngine != null ? gameEngine.getPendingConfig() : null;
        if (config != null && config.isPlantBanned(properName)) {
            return new Result("Error: Plant '" + properName + "' is locked in this level.", false);
        }
        int maxSlots = config != null ? config.getMaxSlots() : MAX_SLOTS;
        if (selectedPackets.size() >= maxSlots) {
            return new Result("Error: All " + maxSlots + " slots are full.", false);
        }
        for (SeedPacket packet : selectedPackets) {
            if (packet.getPlantName().equalsIgnoreCase(properName)) {
                return new Result("Error: Plant is already selected.", false);
            }
        }
        selectedPackets.add(PlantFactory.createSeedPacket(properName));
        return new Result(properName + " added to slot " + selectedPackets.size() + ".", true);
    }

    public Result removePlantFromSlot(String plantName) {
        for (SeedPacket packet : selectedPackets) {
            if (packet.getPlantName().equalsIgnoreCase(plantName)
                    || packet.getPlantName().equals(PlantFactory.properName(plantName))) {
                selectedPackets.remove(packet);
                return new Result(packet.getPlantName() + " removed from your slots.", true);
            }
        }
        return new Result("Error: Plant is not in your slots.", false);
    }

    public Result boostPlant(String plantName) {
        User user = userApp.getLoggedInUser();
        for (SeedPacket packet : selectedPackets) {
            if (packet.getPlantName().equalsIgnoreCase(plantName)
                    || packet.getPlantName().equals(PlantFactory.properName(plantName))) {
                if (user.getGems() < BOOST_GEM_COST) {
                    return new Result("Error: Not enough gems. Boosting costs " + BOOST_GEM_COST + " gems.", false);
                }
                user.setGems(user.getGems() - BOOST_GEM_COST);
                packet.applyBoost();
                user.addBoostFor(packet.getPlantName());
                return new Result(packet.getPlantName() + " has been boosted for this level!", true);
            }
        }
        return new Result("Error: Plant is not in your slots.", false);
    }

    public Result finalizeAndStart(SettingsMenuController settingsController) {
        User user = userApp.getLoggedInUser();
        if (selectedPackets.isEmpty()) {
            return new Result("Error: Select at least one plant before starting.", false);
        }
        int difficulty = settingsController != null ? settingsController.getDifficultyLevel() : 3;
        LevelConfig config = gameEngine != null ? gameEngine.getPendingConfig() : null;
        int initialSun = config != null ? config.getInitialSun() : 50;
        MatchState state = new MatchState(user, initialSun, difficulty);
        state.setSeedPackets(new ArrayList<>(selectedPackets));
        state.initializeFromUser(user);
        selectedPackets.clear();
        if (gameEngine != null) {
            gameEngine.setScoreMode(false);
            gameEngine.startMatch(state);
        }
        router.navigateTo(Menu.GameMenu);
        return new Result("Match started with " + state.getSeedPackets().size() + " plants!", true);
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
}
