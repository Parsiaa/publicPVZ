package models.PlantTypes.Plants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import models.MatchState;
import models.Zombie;
import models.PlantTypes.WallNut;

/** When bitten, forces the attacking zombie into an adjacent lane. */
public class Garlic extends WallNut {
    @Override
    public void onBittenBy(Zombie zombie, MatchState state) {
        List<Integer> lanes = new ArrayList<>();
        if (y - 1 >= 0) {
            lanes.add((int) y - 1);
        }
        if (y + 1 < state.getMap().getRows()) {
            lanes.add((int) y + 1);
        }
        if (lanes.isEmpty()) {
            return;
        }
        int lane = lanes.get(ThreadLocalRandom.current().nextInt(lanes.size()));
        zombie.setY(lane);
        state.getMap().moveZombieToTile(zombie);
        System.out.println(zombie.getTypeName() + " was pushed to lane " + (lane + 1) + " by garlic!");
    }
}
