package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.MatchState;
import models.SeedPacket;
import models.User;
import models.Enums.Menu;
import utils.Result;
import utils.UserApp;

public class PlantSelectionMenuController {
    private UserApp userApp;
    private MenuRouter router;
    private CollectionMenuController collectionController;
    private List<SeedPacket> selectedPackets;
    private static final int MAX_SLOTS = 8;
    private static final int BOOST_GEM_COST = 2;
    private static final List<String> ALL_PLANTS = Arrays.asList(
            "Sunflower", "Peashooter", "WallNut", "CherryBomb", "Cabbagepult",
            "Chomper", "IcebergLettuce", "BonkChoy", "Bloomerang", "PeppermInt");
    private static final List<Integer> PLANT_COSTS = Arrays.asList(
            50, 100, 50, 150, 100, 150, 0, 150, 175, 200);

    public PlantSelectionMenuController(UserApp userApp, MenuRouter router,
                                        CollectionMenuController collectionController) {
        this.userApp = userApp;
        this.router = router;
        this.collectionController = collectionController;
        this.selectedPackets = new ArrayList<>();
    }

    public Result showAllPlantsInLevel() {
        StringBuilder sb = new StringBuilder("All plants:\n");
        for (int i = 0; i < ALL_PLANTS.size(); i++) {
            sb.append("- ").append(ALL_PLANTS.get(i)).append(" (cost: ").append(PLANT_COSTS.get(i)).append(")\n");
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result showAvailablePlantsInLevel() {
        StringBuilder sb = new StringBuilder("Available plants:\n");
        boolean any = false;
        for (int i = 0; i < ALL_PLANTS.size(); i++) {
            if (collectionController.isPlantUnlocked(ALL_PLANTS.get(i))) {
                sb.append("- ").append(ALL_PLANTS.get(i)).append(" (cost: ").append(PLANT_COSTS.get(i)).append(")\n");
                any = true;
            }
        }
        if (!any) {
            return new Result("No plants available in this level.", true);
        }
        return new Result(sb.toString().trim(), true);
    }

    public Result addPlantToSlot(String plantName) {
        int index = indexOfPlant(plantName);
        if (index == -1) {
            return new Result("Error: Plant '" + plantName + "' doesn't exist.", false);
        }
        if (!collectionController.isPlantUnlocked(ALL_PLANTS.get(index))) {
            return new Result("Error: Plant '" + plantName + "' is locked.", false);
        }
        if (selectedPackets.size() >= MAX_SLOTS) {
            return new Result("Error: All " + MAX_SLOTS + " slots are full.", false);
        }
        for (SeedPacket packet : selectedPackets) {
            if (packet.getPlantName().equalsIgnoreCase(plantName)) {
                return new Result("Error: Plant is already selected.", false);
            }
        }
        selectedPackets.add(new SeedPacket(ALL_PLANTS.get(index), PLANT_COSTS.get(index), 5.0));
        return new Result(ALL_PLANTS.get(index) + " added to slot " + selectedPackets.size() + ".", true);
    }

    public Result removePlantFromSlot(String plantName) {
        for (SeedPacket packet : selectedPackets) {
            if (packet.getPlantName().equalsIgnoreCase(plantName)) {
                selectedPackets.remove(packet);
                return new Result(packet.getPlantName() + " removed from your slots.", true);
            }
        }
        return new Result("Error: Plant is not in your slots.", false);
    }

    public Result boostPlant(String plantName) {
        User user = userApp.getLoggedInUser();
        for (SeedPacket packet : selectedPackets) {
            if (packet.getPlantName().equalsIgnoreCase(plantName)) {
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
        MatchState state = new MatchState(user, 50, difficulty);
        state.setSeedPackets(new ArrayList<>(selectedPackets));
        state.initializeFromUser(user);
        selectedPackets.clear();
        router.navigateTo(Menu.GameMenu);
        return new Result("Match started with " + state.getSeedPackets().size() + " plants!", true);
    }

    private int indexOfPlant(String plantName) {
        for (int i = 0; i < ALL_PLANTS.size(); i++) {
            if (ALL_PLANTS.get(i).equalsIgnoreCase(plantName)) {
                return i;
            }
        }
        return -1;
    }
}
