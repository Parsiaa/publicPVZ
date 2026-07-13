package controllers.Chapters;

import java.util.ArrayList;
import java.util.Random;
import controllers.ChapterMechanics;
import models.MatchState;
import models.Tile;
import models.Zombie;
import models.Enums.ObstacleType;
import models.Enums.TileType;

/**
 * Ancient Egypt: starting graves that block straight shots, and tornado
 * arrivals that push final-wave zombies a few columns forward.
 */
public class EgyptChapter extends ChapterMechanics {

    private static final double TORNADO_CHANCE = 0.3;

    public EgyptChapter(Random random) {
        super(random);
    }

    @Override
    public void onMatchStart(MatchState state) {
        int graves = 3 + random.nextInt(3);
        for (int i = 0; i < graves; i++) {
            int row = random.nextInt(state.getMap().getRows());
            int column = 3 + random.nextInt(state.getMap().getColumns() - 4);
            Tile tile = state.getMap().getTile(row, column);
            if (tile != null && tile.getType() == TileType.NORMAL) {
                tile.setType(TileType.GRAVE);
                tile.setGraveLoot(rollGraveLoot());
            }
        }
    }

    private ObstacleType rollGraveLoot() {
        double roll = random.nextDouble();
        if (roll < 0.3) {
            return ObstacleType.GRAVE_SUN;
        }
        if (roll < 0.5) {
            return ObstacleType.GRAVE_FOOD;
        }
        return null;
    }

    @Override
    public void onWaveStart(MatchState state, int waveNumber, boolean finalWave) {
        if (!finalWave) {
            return;
        }
        for (Zombie zombie : new ArrayList<>(state.getMap().getAllZombies())) {
            if (zombie.getCurrentHealth() > 0 && random.nextDouble() < TORNADO_CHANCE) {
                int shift = 1 + random.nextInt(4);
                zombie.setX(Math.max(1, zombie.getX() - shift));
                state.getMap().moveZombieToTile(zombie);
                System.out.println("A tornado carried zombie " + zombie.getTypeName()
                        + " to column " + ((int) zombie.getX() + 1) + " of lane " + ((int) zombie.getY() + 1) + "!");
            }
        }
    }
}
