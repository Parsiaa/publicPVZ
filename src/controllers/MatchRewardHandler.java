package controllers;

import java.util.Random;
import models.MatchState;
import models.User;
import models.Zombie;
import utils.Result;
import utils.UserApp;

/**
 * Everything the player earns from a match: random zombie drops during play
 * and the coins, gems and level progress awarded when the match ends.
 */
public class MatchRewardHandler {

    private static final double LOOT_DROP_CHANCE = 0.10;

    private final UserApp userApp;

    public MatchRewardHandler(UserApp userApp) {
        this.userApp = userApp;
    }

    public void dropZombieLoot(MatchState state, Zombie zombie, Random random) {
        if (zombie.getEffects().contains("glowing")) {
            state.addPlantFood();
            System.out.println("The glowing zombie dropeed a plant food; you have "
                    + state.getPlantFoods() + " plant foods now.");
        }
        if (random.nextDouble() >= LOOT_DROP_CHANCE) {
            return;
        }
        int roll = random.nextInt(3);
        if (roll == 0) {
            state.addLoot(50, 0, 0);
            System.out.println("A zombie dropeed a coin; you have " + state.getLootedCoins() + " coins now.");
        } else if (roll == 1) {
            state.addLoot(0, 1, 0);
            System.out.println("A zombie dropeed a diamond; you have " + state.getLootedGems() + " diamonds now.");
        } else {
            state.addLoot(0, 0, 1);
            System.out.println("A zombie dropeed a pot; you have " + state.getLootedPots() + " pots now.");
        }
    }

    /** Credits the winnings and records adventure/mini-game progress, then saves. */
    public void awardMatchEnd(MatchState state, LevelConfig config, boolean won) {
        User user = state.getUser();
        if (user == null) {
            return;
        }
        if (won) {
            user.setCoins(user.getCoins() + state.getLootedCoins());
            user.setGems(user.getGems() + state.getLootedGems());
            recordProgress(user, config);
        }
        userApp.saveUsers();
    }

    private void recordProgress(User user, LevelConfig config) {
        if (config == null) {
            return;
        }
        if (config.isMiniGame()) {
            user.setMiniGamesCompleted(user.getMiniGamesCompleted() + 1);
            return;
        }
        String chapterName = config.getChapterType().name();
        if (config.getLevelNumber() > user.getCompletedLevels(chapterName)) {
            user.setCompletedLevels(chapterName, config.getLevelNumber());
            user.setHighestLevel(user.getHighestLevel() + 1);
            if (config.getLevelNumber() >= LevelFactory.PLAYABLE_LEVELS_PER_CHAPTER) {
                int chapterIndex = LevelFactory.chapterIndex(config.getChapterType());
                user.setHighestChapter(Math.max(user.getHighestChapter(), chapterIndex));
                System.out.println("Chapter " + chapterName + " completed! The next chapter is unlocked.");
            }
        }
    }

    /** Releases held zombie waves for Plant-What-You-Get levels. */
    public Result startZombieWaves(WaveManager waveManager) {
        if (waveManager.isReleased()) {
            return new Result("Error: The zombie waves have already started.", false);
        }
        waveManager.release();
        return new Result("The zombie waves have been released!", true);
    }
}
