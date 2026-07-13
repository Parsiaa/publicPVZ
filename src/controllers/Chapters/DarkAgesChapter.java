package controllers.Chapters;

import java.util.Random;
import controllers.ChapterMechanics;
import models.MatchState;
import models.Tile;
import models.Zombie;
import models.Enums.ObstacleType;
import models.Enums.TileType;
import utils.ZombieFactory;

/**
 * Dark Ages: it is always night (no sky suns), new graves rise at every wave
 * on unplanted tiles (some holding sun or plant food), and necromancy tiles
 * raise a zombie from below at each wave start.
 */
public class DarkAgesChapter extends ChapterMechanics {

    private static final double NECROMANCY_SPAWN_CHANCE = 0.5;

    public DarkAgesChapter(Random random) {
        super(random);
    }

    @Override
    public void onMatchStart(MatchState state) {
        state.setNightTime(true);
        for (int i = 0; i < 2; i++) {
            int row = random.nextInt(state.getMap().getRows());
            int column = 4 + random.nextInt(state.getMap().getColumns() - 5);
            Tile tile = state.getMap().getTile(row, column);
            if (tile != null && tile.getType() == TileType.NORMAL) {
                tile.setType(TileType.NECROMANCY);
            }
        }
        raiseGraves(state, 2);
    }

    @Override
    public void onWaveStart(MatchState state, int waveNumber, boolean finalWave) {
        raiseGraves(state, random.nextInt(3));
        for (int row = 0; row < state.getMap().getRows(); row++) {
            for (int col = 0; col < state.getMap().getColumns(); col++) {
                Tile tile = state.getMap().getTile(row, col);
                if (tile != null && tile.getType() == TileType.NECROMANCY
                        && random.nextDouble() < NECROMANCY_SPAWN_CHANCE) {
                    Zombie zombie = ZombieFactory.createZombie("Normal", state.getDifficultyLevel());
                    state.getMap().addZombie(zombie, row, col);
                    System.out.println("A zombie rose from the necromancy ground at ("
                            + (col + 1) + ", " + (row + 1) + ")!");
                }
            }
        }
    }

    private void raiseGraves(MatchState state, int count) {
        for (int i = 0; i < count; i++) {
            int row = random.nextInt(state.getMap().getRows());
            int column = 2 + random.nextInt(state.getMap().getColumns() - 3);
            Tile tile = state.getMap().getTile(row, column);
            if (tile == null || tile.getType() != TileType.NORMAL || !tile.getPlants().isEmpty()) {
                continue;
            }
            tile.setType(TileType.GRAVE);
            double roll = random.nextDouble();
            if (roll < 0.25) {
                tile.setGraveLoot(ObstacleType.GRAVE_SUN);
                System.out.println("A grave holding sun rose at (" + (column + 1) + ", " + (row + 1) + ")!");
            } else if (roll < 0.4) {
                tile.setGraveLoot(ObstacleType.GRAVE_FOOD);
                System.out.println("A grave holding plant food rose at (" + (column + 1) + ", " + (row + 1) + ")!");
            } else {
                System.out.println("A grave rose at (" + (column + 1) + ", " + (row + 1) + ")!");
            }
        }
    }
}
