package models.Zombies;

import java.util.Random;
import models.MatchState;
import models.Tile;
import models.Zombie;
import models.Enums.TileType;

public class TombRaiserZombie extends Zombie {
    private double timeSinceLastCast;

    @Override
    public void move(MatchState state) {
        super.move(state);
    }
    public void act(MatchState state) {
        if (currentHealth <= 0) {
            return;
        }
        timeSinceLastCast += 0.1;
        if (timeSinceLastCast >= 5.0) {
            timeSinceLastCast = 0;
            Random random = new Random();
            for (int i = 0; i < 2; i++) {
                int row = random.nextInt(state.getMap().getRows());
                int col = random.nextInt(state.getMap().getColumns());
                Tile tile = state.getMap().getTile(row, col);
                if (tile != null && tile.getType() == TileType.NORMAL && tile.getPlants().isEmpty()) {
                    tile.setType(TileType.GRAVE);
                    System.out.println("Tomb Raiser Zombie created a grave at (" + col + ", " + row + ")!");
                }
            }
        }
    }

    public double getTimeSinceLastCast() { return timeSinceLastCast; }
}
